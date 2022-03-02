package cn.DynamicGraph.kernel.impl.store;

import cn.DynamicGraph.kernel.impl.store.record.NodeVersionRecord;
import cn.DynamicGraph.store.record.DynamicVersionRecord;
import cn.DynamicGraph.store.record.DynamicVersionRecordAllocator;
import org.neo4j.collection.PrimitiveLongCollections;
import org.neo4j.kernel.impl.store.*;
//import org.neo4j.kernel.impl.store.LabelIdArray;
import org.neo4j.kernel.impl.util.Bits;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class InlineNodeVersionLabels implements NodeVersionLabels{
    private static final int LABEL_BITS = 36;
    private final NodeVersionRecord node;

    public InlineNodeVersionLabels(NodeVersionRecord node) {
        this.node = node;
    }

    public long[] get(NodeVersionStore nodeStore) {
        return get(this.node);
    }

    public static long[] get(NodeVersionRecord node) {
        return parseInlined(node.getLabelField());
    }

    public long[] getIfLoaded() {
        return parseInlined(this.node.getLabelField());
    }

    public Collection<DynamicVersionRecord> put(long[] labelIds, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
        Arrays.sort(labelIds);
        return putSorted(this.node, labelIds, nodeStore, allocator);
    }

    public static Collection<DynamicVersionRecord> putSorted(NodeVersionRecord node, long[] labelIds, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
        return (Collection)(tryInlineInNodeRecord(node, labelIds, node.getDynamicLabelRecords()) ? Collections.emptyList() : DynamicNodeVersionLabels.putSorted(node, labelIds, nodeStore, allocator));
    }

    public Collection<DynamicVersionRecord> add(long labelId, NodeVersionStore nodeStore, DynamicVersionRecordAllocator allocator) {
        long[] augmentedLabelIds = labelCount(this.node.getLabelField()) == 0 ? new long[]{labelId} : LabelIdArray.concatAndSort(parseInlined(this.node.getLabelField()), labelId);
        return putSorted(this.node, augmentedLabelIds, nodeStore, allocator);
    }

    public Collection<DynamicVersionRecord> remove(long labelId, NodeVersionStore nodeStore) {
        long[] newLabelIds = LabelIdArray.filter(parseInlined(this.node.getLabelField()), labelId);
        boolean inlined = tryInlineInNodeRecord(this.node, newLabelIds, this.node.getDynamicLabelRecords());

        assert inlined;

        return Collections.emptyList();
    }

    static boolean tryInlineInNodeRecord(NodeVersionRecord node, long[] ids, Collection<DynamicVersionRecord> changedDynamicRecords) {
        if (ids.length > 7) {
            return false;
        } else {
            byte bitsPerLabel = (byte)(ids.length > 0 ? 36 / ids.length : 36);
            Bits bits = Bits.bits(5);
            if (!inlineValues(ids, bitsPerLabel, bits)) {
                return false;
            } else {
                node.setLabelField(combineLabelCountAndLabelStorage((byte)ids.length, bits.getLongs()[0]), changedDynamicRecords);
                return true;
            }
        }
    }

    private static boolean inlineValues(long[] values, int maxBitsPerLabel, Bits target) {
        long limit = 1L << maxBitsPerLabel;
        long[] var5 = values;
        int var6 = values.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            long value = var5[var7];
            if (Long.highestOneBit(value) >= limit) {
                return false;
            }

            target.put(value, maxBitsPerLabel);
        }

        return true;
    }

    public static long[] parseInlined(long labelField) {
        byte numberOfLabels = labelCount(labelField);
        if (numberOfLabels == 0) {
            return PrimitiveLongCollections.EMPTY_LONG_ARRAY;
        } else {
            long existingLabelsField = NodeLabelsField.parseLabelsBody(labelField);
            byte bitsPerLabel = (byte)(36 / numberOfLabels);
            Bits bits = Bits.bitsFromLongs(new long[]{existingLabelsField});
            long[] result = new long[numberOfLabels];

            for(int i = 0; i < result.length; ++i) {
                result[i] = bits.getLong(bitsPerLabel);
            }

            return result;
        }
    }

    private static long combineLabelCountAndLabelStorage(byte labelCount, long labelBits) {
        return (long)labelCount << 36 | labelBits;
    }

    private static byte labelCount(long labelField) {
        return (byte)((int)((labelField & 1030792151040L) >>> 36));
    }

    public boolean isInlined() {
        return true;
    }

    public String toString() {
        return String.format("Inline(0x%x:%s)", this.node.getLabelField(), Arrays.toString(this.getIfLoaded()));
    }
}
