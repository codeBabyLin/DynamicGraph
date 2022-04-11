//package cn.DynamicGraph.kernel.impl.store.format;
//
//import cn.DynamicGraph.kernel.impl.store.format.standard.NodeVersionRecordFormat;
//import org.neo4j.helpers.Service;
//import org.neo4j.kernel.impl.store.format.*;
//import org.neo4j.kernel.impl.store.record.*;
//
//public interface VersionRecordFormats {
//    String storeVersion();
//
//    String introductionVersion();
//
//    int generation();
//
//    NodeVersionRecordFormat node();
//
//    RecordFormat<RelationshipGroupRecord> relationshipGroup();
//
//    RecordFormat<RelationshipRecord> relationship();
//
//    RecordFormat<PropertyRecord> property();
//
//    RecordFormat<LabelTokenRecord> labelToken();
//
//    RecordFormat<PropertyKeyTokenRecord> propertyKeyToken();
//
//    RecordFormat<RelationshipTypeTokenRecord> relationshipTypeToken();
//
//    RecordFormat<DynamicRecord> dynamic();
//
//    RecordFormat<MetaDataRecord> metaData();
//
//    Capability[] capabilities();
//
//    boolean hasCapability(Capability var1);
//
//    FormatFamily getFormatFamily();
//
//    boolean hasCompatibleCapabilities(RecordFormats var1, CapabilityType var2);
//
//    String name();
//
//    public abstract static class Factory extends Service {
//        public Factory(String key, String... altKeys) {
//            super(key, altKeys);
//        }
//
//        public abstract RecordFormats newInstance();
//    }
//}
