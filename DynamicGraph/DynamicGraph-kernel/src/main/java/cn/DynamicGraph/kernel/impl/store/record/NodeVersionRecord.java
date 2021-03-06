//package cn.DynamicGraph.kernel.impl.store.record;
//
//import cn.DynamicGraph.kernel.impl.store.NodeVersionLabelsField;
//import cn.DynamicGraph.store.record.DynamicVersionRecord;
//import org.neo4j.helpers.collection.Iterables;
//import org.neo4j.kernel.impl.store.record.*;
//
//import java.util.*;
//
//public class NodeVersionRecord extends PrimitiveRecord {
//    private long nextRel;
//    private long labels;
//    private Collection<DynamicVersionRecord> dynamicLabelRecords;
//    private boolean isLight;
//    private boolean dense;
//    private long version;
//
//    public NodeVersionRecord(long id) {
//        super(id);
//    }
//
//    public NodeVersionRecord initialize(boolean inUse, long nextProp, boolean dense, long nextRel, long labels, long version) {
//        this.version = version;
//        return initialize(inUse,nextProp, dense, nextRel, labels);
//    }
//    public NodeVersionRecord initialize(boolean inUse, long nextProp, boolean dense, long nextRel, long labels) {
//        super.initialize(inUse, nextProp);
//        this.nextRel = nextRel;
//        this.dense = dense;
//        this.labels = labels;
//        this.dynamicLabelRecords = Collections.emptyList();
//        this.isLight = true;
//        return this;
//    }
//
//    /** @deprecated */
//    @Deprecated
//    public NodeVersionRecord(long id, boolean dense, long nextRel, long nextProp) {
//        this(id, false, dense, nextRel, nextProp, 0L);
//    }
//
//    /** @deprecated */
//    @Deprecated
//    public NodeVersionRecord(long id, boolean inUse, boolean dense, long nextRel, long nextProp, long labels) {
//        super(id, nextProp);
//        this.nextRel = nextRel;
//        this.dense = dense;
//        this.labels = labels;
//        this.setInUse(inUse);
//    }
//
//    /** @deprecated */
//    @Deprecated
//    public NodeVersionRecord(long id, boolean dense, long nextRel, long nextProp, boolean inUse) {
//        this(id, dense, nextRel, nextProp);
//        this.setInUse(inUse);
//    }
//
//    public void clear() {
//        this.initialize(false, (long) Record.NO_NEXT_PROPERTY.intValue(), false, (long)Record.NO_NEXT_RELATIONSHIP.intValue(), (long)Record.NO_LABELS_FIELD.intValue());
//    }
//
//    public long getVersion(){return this.version;}
//    public void setVersion(long version){
//        this.version = version;
//    }
//
//    public long getNextRel() {
//        return this.nextRel;
//    }
//
//    public void setNextRel(long nextRel) {
//        this.nextRel = nextRel;
//    }
//
//    public void setLabelField(long labels, Collection<DynamicVersionRecord> dynamicRecords) {
//        this.labels = labels;
//        this.dynamicLabelRecords = dynamicRecords;
//        this.isLight = dynamicRecords.isEmpty();
//    }
//
//    public long getLabelField() {
//        return this.labels;
//    }
//
//    public boolean isLight() {
//        return this.isLight;
//    }
//
//    public Collection<DynamicVersionRecord> getDynamicLabelRecords() {
//        return this.dynamicLabelRecords;
//    }
//
//    public Iterable<DynamicVersionRecord> getUsedDynamicLabelRecords() {
//        return Iterables.filter(AbstractBaseRecord::inUse, this.dynamicLabelRecords);
//    }
//
//    public boolean isDense() {
//        return this.dense;
//    }
//
//    public void setDense(boolean dense) {
//        this.dense = dense;
//    }
//
//    public String toString() {
//        String denseInfo = (this.dense ? "group" : "rel") + "=" + this.nextRel;
//        String lightHeavyInfo = this.isLight ? "light" : (this.dynamicLabelRecords.isEmpty() ? "heavy" : "heavy,dynlabels=" + this.dynamicLabelRecords);
//        return "Node[" + this.getId() + ",used=" + this.inUse() + "," + denseInfo + ",prop=" + this.getNextProp() + ",labels=" + NodeVersionLabelsField.parseLabelsField(this) + "," + lightHeavyInfo + ",secondaryUnitId=" + this.getSecondaryUnitId() + "]";
//    }
//
//    public void setIdTo(PropertyRecord property) {
//        property.setNodeId(this.getId());
//    }
//
//    public NodeVersionRecord clone() {
//        NodeVersionRecord clone = (new NodeVersionRecord(this.getId())).initialize(this.inUse(), this.nextProp, this.dense, this.nextRel, this.labels);
//        clone.isLight = this.isLight;
//        if (this.dynamicLabelRecords.size() > 0) {
//            List<DynamicVersionRecord> clonedLabelRecords = new ArrayList(this.dynamicLabelRecords.size());
//            Iterator var3 = this.dynamicLabelRecords.iterator();
//
//            while(var3.hasNext()) {
//                DynamicVersionRecord labelRecord = (DynamicVersionRecord)var3.next();
//                clonedLabelRecords.add(labelRecord.clone());
//            }
//
//            clone.dynamicLabelRecords = clonedLabelRecords;
//        }
//
//        clone.setSecondaryUnitId(this.getSecondaryUnitId());
//        return clone;
//    }
//}
