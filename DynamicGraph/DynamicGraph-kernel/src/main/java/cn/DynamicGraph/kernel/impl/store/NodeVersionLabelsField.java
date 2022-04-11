//package cn.DynamicGraph.kernel.impl.store;
//
//import cn.DynamicGraph.kernel.impl.store.record.NodeVersionRecord;
//
//public class NodeVersionLabelsField {
//    private NodeVersionLabelsField() {
//    }
//
//    public static NodeVersionLabels parseLabelsField(NodeVersionRecord node) {
//        long labelField = node.getLabelField();
//        return (NodeVersionLabels)(fieldPointsToDynamicRecordOfLabels(labelField) ? new DynamicNodeVersionLabels(node) : new InlineNodeVersionLabels(node));
//    }
//
//    public static long[] get(NodeVersionRecord node, NodeVersionStore nodeStore) {
//        return fieldPointsToDynamicRecordOfLabels(node.getLabelField()) ? DynamicNodeVersionLabels.get(node, nodeStore) : InlineNodeVersionLabels.get(node);
//    }
//
//    public static boolean fieldPointsToDynamicRecordOfLabels(long labelField) {
//        return (labelField & 549755813888L) != 0L;
//    }
//
//    public static long parseLabelsBody(long labelField) {
//        return labelField & 68719476735L;
//    }
//
//    public static long firstDynamicLabelRecordId(long labelField) {
//        assert fieldPointsToDynamicRecordOfLabels(labelField);
//
//        return parseLabelsBody(labelField);
//    }
//
//    public static boolean isSane(long[] labelIds) {
//        long prev = -1L;
//        long[] var3 = labelIds;
//        int var4 = labelIds.length;
//
//        for(int var5 = 0; var5 < var4; ++var5) {
//            long labelId = var3[var5];
//            if (labelId <= prev) {
//                return false;
//            }
//
//            prev = labelId;
//        }
//
//        return true;
//    }
//
//}
