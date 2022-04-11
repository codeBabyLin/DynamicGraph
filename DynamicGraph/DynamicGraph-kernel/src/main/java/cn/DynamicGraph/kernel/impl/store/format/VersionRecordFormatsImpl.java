//package cn.DynamicGraph.kernel.impl.store.format;
//
//import cn.DynamicGraph.kernel.impl.store.format.standard.NodeVersionRecordFormat;
//import org.neo4j.kernel.impl.store.format.*;
//import org.neo4j.kernel.impl.store.format.standard.*;
//import org.neo4j.kernel.impl.store.record.*;
//
//public class VersionRecordFormatsImpl extends VersionBaseRecordFormats{
//    public static final String STORE_VERSION;
//    public static final RecordFormats RECORD_FORMATS;
//    public static final String NAME = "standard";
//
//    public VersionRecordFormatsImpl() {
//        super(STORE_VERSION, StoreVersion.STANDARD_V3_4.introductionVersion(), 8, new Capability[]{Capability.SCHEMA, Capability.DENSE_NODES, Capability.LUCENE_5, Capability.POINT_PROPERTIES, Capability.TEMPORAL_PROPERTIES});
//    }
//    //nodeRecordFormat-> DynamicNodeRecordFormat
//
//    public NodeVersionRecordFormat node() {
//        return new NodeVersionRecordFormat();
//    }
//
//    public RecordFormat<RelationshipGroupRecord> relationshipGroup() {
//        return new RelationshipGroupRecordFormat();
//    }
//
//    public RecordFormat<RelationshipRecord> relationship() {
//        return new RelationshipRecordFormat();
//    }
//
//    public RecordFormat<PropertyRecord> property() {
//        return new PropertyRecordFormat();
//    }
//
//    public RecordFormat<LabelTokenRecord> labelToken() {
//        return new LabelTokenRecordFormat();
//    }
//
//    public RecordFormat<PropertyKeyTokenRecord> propertyKeyToken() {
//        return new PropertyKeyTokenRecordFormat();
//    }
//
//    public RecordFormat<RelationshipTypeTokenRecord> relationshipTypeToken() {
//        return new RelationshipTypeTokenRecordFormat();
//    }
//
//    public RecordFormat<DynamicRecord> dynamic() {
//        return new DynamicRecordFormat();
//    }
//
//    public FormatFamily getFormatFamily() {
//        return StandardFormatFamily.INSTANCE;
//    }
//
//    @Override
//    public boolean hasCompatibleCapabilities(RecordFormats var1, CapabilityType var2) {
//        return false;
//    }
//
//    public String name() {
//        return "standard";
//    }
//
//    static {
//        STORE_VERSION = StoreVersion.STANDARD_V3_4.versionString();
//        RECORD_FORMATS = new StandardV3_4();
//    }
//}
