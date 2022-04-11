package cn.DynamicGraph.kernel.impl.store;

import java.util.Arrays;

public class LabelIdArray {
    private LabelIdArray() {
    }

    static long[] concatAndSort(long[] existing, long additional) {
        assertNotContains(existing, additional);
        long[] result = new long[existing.length + 1];
        System.arraycopy(existing, 0, result, 0, existing.length);
        result[existing.length] = additional;
        Arrays.sort(result);
        return result;
    }

    private static void assertNotContains(long[] existingLabels, long labelId) {
        if (Arrays.binarySearch(existingLabels, labelId) >= 0) {
            throw new IllegalStateException("Label " + labelId + " already exists.");
        }
    }

    static long[] filter(long[] ids, long excludeId) {
        boolean found = false;
        long[] result = ids;
        int writerIndex = ids.length;

        for(int var6 = 0; var6 < writerIndex; ++var6) {
            long id = result[var6];
            if (id == excludeId) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalStateException("Label " + excludeId + " not found.");
        } else {
            result = new long[ids.length - 1];
            writerIndex = 0;
            long[] var11 = ids;
            int var12 = ids.length;

            for(int var8 = 0; var8 < var12; ++var8) {
                long id = var11[var8];
                if (id != excludeId) {
                    result[writerIndex++] = id;
                }
            }

            return result;
        }
    }

    public static long[] prependNodeId(long nodeId, long[] labelIds) {
        long[] result = new long[labelIds.length + 1];
        System.arraycopy(labelIds, 0, result, 1, labelIds.length);
        result[0] = nodeId;
        return result;
    }

    public static long[] stripNodeId(long[] storedLongs) {
        return Arrays.copyOfRange(storedLongs, 1, storedLongs.length);
    }
}
