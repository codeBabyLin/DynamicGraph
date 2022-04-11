//package cn.DynamicGraph.kernel.impl.store;
//
//import cn.DynamicGraph.kernel.impl.store.record.NodeVersionRecord;
//import cn.DynamicGraph.kernel.impl.store.format.VersionRecordFormats;
//import cn.DynamicGraph.store.record.DynamicVersionRecord;
//import cn.DynamicGraph.store.versionStore.DynamicVersionArrayStore;
//import org.neo4j.io.pagecache.PageCache;
//import org.neo4j.kernel.configuration.Config;
//import org.neo4j.kernel.impl.store.*;
//import org.neo4j.kernel.impl.store.id.IdGeneratorFactory;
//import org.neo4j.kernel.impl.store.id.IdType;
//import org.neo4j.kernel.impl.store.record.RecordLoad;
//import org.neo4j.kernel.impl.util.Bits;
//import org.neo4j.logging.LogProvider;
//
//import java.io.File;
//import java.nio.file.OpenOption;
//import java.util.Arrays;
//import java.util.Iterator;
//
//public class NodeVersionStore extends CommonAbstractStore<NodeVersionRecord, NoStoreHeader> {
//    public static final String TYPE_DESCRIPTOR = "NodeVersionStore";
//    private final DynamicVersionArrayStore dynamicLabelStore;
//
//    public static Long readOwnerFromDynamicLabelsRecord(DynamicVersionRecord record) {
//        byte[] data = record.getData();
//        byte[] header = PropertyType.ARRAY.readDynamicRecordHeader(data);
//        byte[] array = Arrays.copyOfRange(data, header.length, data.length);
//        int requiredBits = header[2];
//        if (requiredBits == 0) {
//            return null;
//        } else {
//            Bits bits = Bits.bitsFromBytes(array);
//            return bits.getLong(requiredBits);
//        }
//    }
//
//    public NodeVersionStore(File file, File idFile, Config config, IdGeneratorFactory idGeneratorFactory, PageCache pageCache, LogProvider logProvider, DynamicVersionArrayStore dynamicLabelStore, VersionRecordFormats recordFormats, OpenOption... openOptions) {
//        super(file, idFile, config, IdType.NODE, idGeneratorFactory, pageCache, logProvider, "NodeVersionStore", recordFormats.node(), NoStoreHeaderFormat.NO_STORE_HEADER_FORMAT, recordFormats.storeVersion(), openOptions);
//        this.dynamicLabelStore = dynamicLabelStore;
//    }
//
//    public <FAILURE extends Exception> void accept(Processor<FAILURE> processor, NodeVersionRecord record) throws FAILURE {
//        processor.processNode(this, record);
//    }
//
//    public void ensureHeavy(NodeVersionRecord node) {
//        if (NodeVersionLabelsField.fieldPointsToDynamicRecordOfLabels(node.getLabelField())) {
//            this.ensureHeavy(node, NodeLabelsField.firstDynamicLabelRecordId(node.getLabelField()));
//        }
//
//    }
//
//    public void ensureHeavy(NodeVersionRecord node, long firstDynamicLabelRecord) {
//        if (node.isLight()) {
//            node.setLabelField(node.getLabelField(), this.dynamicLabelStore.getRecords(firstDynamicLabelRecord, RecordLoad.NORMAL));
//        }
//    }
//
//    public void updateRecord(NodeVersionRecord record) {
//        super.updateRecord(record);
//        this.updateDynamicLabelRecords(record.getDynamicLabelRecords());
//    }
//
//    public DynamicVersionArrayStore getDynamicLabelStore() {
//        return this.dynamicLabelStore;
//    }
//    public DynamicVersionArrayStore getDynamicVersionLabelStore() {
//        return this.dynamicLabelStore;
//    }
//
//    public void updateDynamicLabelRecords(Iterable<DynamicVersionRecord> dynamicLabelRecords) {
//        Iterator var2 = dynamicLabelRecords.iterator();
//
//        while(var2.hasNext()) {
//            DynamicVersionRecord record = (DynamicVersionRecord)var2.next();
//            this.dynamicLabelStore.updateRecord(record);
//        }
//
//    }
//}
