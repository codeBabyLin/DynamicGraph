//package cn.DynamicGraph.kernel.impl.store;
//
//import cn.DynamicGraph.kernel.impl.store.allocator.ReusableVersionRecordsCompositeAllocator;
//import cn.DynamicGraph.kernel.impl.store.record.NodeVersionRecord;
//import cn.DynamicGraph.store.record.DynamicVersionRecord;
//import cn.DynamicGraph.store.record.DynamicVersionRecordAllocator;
//import cn.DynamicGraph.store.versionStore.AbstractDynamicVersionStore;
//import cn.DynamicGraph.store.versionStore.DynamicVersionArrayStore;
//import org.neo4j.helpers.collection.Iterables;
//import org.neo4j.helpers.collection.Pair;
//import org.neo4j.kernel.impl.store.*;
//import org.neo4j.kernel.impl.store.record.DynamicRecord;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Iterator;
//
//public class DynamicNodeVersionLabels implements NodeVersionLabels{
//    private final NodeVersionRecord node;
//
//    public DynamicNodeVersionLabels(NodeVersionRecord node) {
//        this.node = node;
//    }
//
//    public long[] get(NodeVersionStore nodeStore) {
//        return get(this.node, nodeStore);
//    }
//
//    public static long[] get(NodeVersionRecord node, NodeVersionStore nodeStore) {
//        if (node.isLight()) {
//            nodeStore.ensureHeavy(node, NodeLabelsField.firstDynamicLabelRecordId(node.getLabelField()));
//        }
//
//        return getDynamicLabelsArray(node.getUsedDynamicLabelRecords(), nodeStore.getDynamicVersionLabelStore());
//    }
//
//    public long[] getIfLoaded() {
//        return this.node.isLight() ? null : LabelIdArray.stripNodeId((long[])((long[]) DynamicVersionArrayStore.getRightArray(AbstractDynamicVersionStore.readFullByteArrayFromHeavyRecords((scala.collection.Iterable<DynamicVersionRecord>) this.node.getUsedDynamicLabelRecords(), PropertyType.ARRAY)).asObject()));
//    }
//
//    public Collection<DynamicVersionRecord> put(long[] labelIds, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
//        Arrays.sort(labelIds);
//        return putSorted(this.node, labelIds, nodeStore, allocator);
//    }
//
//    static Collection<DynamicVersionRecord> putSorted(NodeVersionRecord node, long[] labelIds, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
//        long existingLabelsField = node.getLabelField();
//        long existingLabelsBits = NodeVersionLabelsField.parseLabelsBody(existingLabelsField);
//        Collection<DynamicVersionRecord> changedDynamicRecords = node.getDynamicLabelRecords();
//        long labelField = node.getLabelField();
//        if (NodeLabelsField.fieldPointsToDynamicRecordOfLabels(labelField)) {
//            nodeStore.ensureHeavy(node, existingLabelsBits);
//            changedDynamicRecords = node.getDynamicLabelRecords();
//            setNotInUse(changedDynamicRecords);
//        }
//
//        if (!InlineNodeVersionLabels.tryInlineInNodeRecord(node, labelIds, changedDynamicRecords)) {
//            Iterator<DynamicVersionRecord> recycledRecords = changedDynamicRecords.iterator();
//            Collection allocatedRecords = allocateRecordsForDynamicLabels(node.getId(), labelIds, (DynamicVersionRecordAllocator)(new ReusableVersionRecordsCompositeAllocator(recycledRecords, allocator)));
//
//            while(recycledRecords.hasNext()) {
//                DynamicVersionRecord removedRecord = (DynamicVersionRecord)recycledRecords.next();
//                removedRecord.setInUse(false);
//                allocatedRecords.add(removedRecord);
//            }
//
//            node.setLabelField(dynamicPointer(allocatedRecords), allocatedRecords);
//            changedDynamicRecords = allocatedRecords;
//        }
//
//        return changedDynamicRecords;
//    }
//
//    public Collection<DynamicVersionRecord> add(long labelId, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
//        nodeStore.ensureHeavy(this.node, NodeVersionLabelsField.firstDynamicLabelRecordId(this.node.getLabelField()));
//        long[] existingLabelIds = getDynamicLabelsArray(this.node.getUsedDynamicLabelRecords(), nodeStore.getDynamicVersionLabelStore());
//        long[] newLabelIds = LabelIdArray.concatAndSort(existingLabelIds, labelId);
//        Collection<DynamicVersionRecord> existingRecords = this.node.getDynamicLabelRecords();
//        Collection<DynamicVersionRecord> changedDynamicRecords = allocateRecordsForDynamicLabels(this.node.getId(), newLabelIds, (DynamicVersionRecordAllocator)(new ReusableVersionRecordsCompositeAllocator(existingRecords, allocator)));
//        this.node.setLabelField(dynamicPointer(changedDynamicRecords), changedDynamicRecords);
//        return changedDynamicRecords;
//    }
//
//    public Collection<DynamicVersionRecord> remove(long labelId, NodeVersionStore nodeStore) {
//        nodeStore.ensureHeavy(this.node, NodeLabelsField.firstDynamicLabelRecordId(this.node.getLabelField()));
//        long[] existingLabelIds = getDynamicLabelsArray(this.node.getUsedDynamicLabelRecords(), nodeStore.getDynamicVersionLabelStore());
//        long[] newLabelIds = LabelIdArray.filter(existingLabelIds, labelId);
//        Collection<DynamicVersionRecord> existingRecords = this.node.getDynamicLabelRecords();
//        if (InlineNodeVersionLabels.tryInlineInNodeRecord(this.node, newLabelIds, existingRecords)) {
//            setNotInUse(existingRecords);
//        } else {
//            Collection<DynamicVersionRecord> newRecords = allocateRecordsForDynamicLabels(this.node.getId(), newLabelIds, (DynamicVersionRecordAllocator)(new ReusableVersionRecordsCompositeAllocator(existingRecords, nodeStore.getDynamicVersionLabelStore())));
//            this.node.setLabelField(dynamicPointer(newRecords), existingRecords);
//            if (!newRecords.equals(existingRecords)) {
//                Iterator var8 = existingRecords.iterator();
//
//                while(var8.hasNext()) {
//                    DynamicRecord record = (DynamicRecord)var8.next();
//                    if (!newRecords.contains(record)) {
//                        record.setInUse(false);
//                    }
//                }
//            }
//        }
//
//        return existingRecords;
//    }
//
//    public long getFirstDynamicRecordId() {
//        return NodeVersionLabelsField.firstDynamicLabelRecordId(this.node.getLabelField());
//    }
//
//    public static long dynamicPointer(Collection<DynamicVersionRecord> newRecords) {
//        return 549755813888L | ((DynamicVersionRecord) Iterables.first(newRecords)).getId();
//    }
//
//    private static void setNotInUse(Collection<DynamicVersionRecord> changedDynamicRecords) {
//        Iterator var1 = changedDynamicRecords.iterator();
//
//        while(var1.hasNext()) {
//            DynamicVersionRecord record = (DynamicVersionRecord)var1.next();
//            record.setInUse(false);
//        }
//
//    }
//
//    public boolean isInlined() {
//        return false;
//    }
//
//    public String toString() {
//        return this.node.isLight() ? String.format("Dynamic(id:%d)", NodeVersionLabelsField.firstDynamicLabelRecordId(this.node.getLabelField())) : String.format("Dynamic(id:%d,[%s])", NodeVersionLabelsField.firstDynamicLabelRecordId(this.node.getLabelField()), Arrays.toString(getDynamicLabelsArrayFromHeavyRecords(this.node.getUsedDynamicLabelRecords())));
//    }
//
//    public static Collection<DynamicVersionRecord> allocateRecordsForDynamicLabels(long nodeId, long[] labels, AbstractDynamicVersionStore dynamicLabelStore) {
//        return allocateRecordsForDynamicLabels(nodeId, labels, (DynamicVersionRecordAllocator)dynamicLabelStore);
//    }
//
//    public static Collection<DynamicVersionRecord> allocateRecordsForDynamicLabels(long nodeId, long[] labels, DynamicVersionRecordAllocator allocator) {
//        long[] storedLongs = LabelIdArray.prependNodeId(nodeId, labels);
//        ArrayList records = new ArrayList();
//        DynamicVersionArrayStore.allocateRecords(records, storedLongs, allocator, false);
//        return records;
//    }
//
//    public static long[] getDynamicLabelsArray(Iterable<DynamicVersionRecord> records, AbstractDynamicVersionStore dynamicLabelStore) {
//        long[] storedLongs = (long[])((long[]) DynamicVersionArrayStore.getRightArray(dynamicLabelStore.readFullByteArray((scala.collection.Iterable<DynamicVersionRecord>) records, PropertyType.ARRAY)).asObject());
//        return LabelIdArray.stripNodeId(storedLongs);
//    }
//
//    public static long[] getDynamicLabelsArrayFromHeavyRecords(Iterable<DynamicVersionRecord> records) {
//        long[] storedLongs = (long[])((long[])DynamicVersionArrayStore.getRightArray(AbstractDynamicVersionStore.readFullByteArrayFromHeavyRecords((scala.collection.Iterable<DynamicVersionRecord>) records, PropertyType.ARRAY)).asObject());
//        return LabelIdArray.stripNodeId(storedLongs);
//    }
//
//    public static Pair<Long, long[]> getDynamicLabelsArrayAndOwner(Iterable<DynamicVersionRecord> records, AbstractDynamicVersionStore dynamicLabelStore) {
//        long[] storedLongs = (long[])((long[])DynamicVersionArrayStore.getRightArray(dynamicLabelStore.readFullByteArray((scala.collection.Iterable<DynamicVersionRecord>) records, PropertyType.ARRAY)).asObject());
//        return Pair.of(storedLongs[0], LabelIdArray.stripNodeId(storedLongs));
//    }
//}
