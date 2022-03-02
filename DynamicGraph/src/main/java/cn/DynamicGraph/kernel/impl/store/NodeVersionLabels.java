package cn.DynamicGraph.kernel.impl.store;

import cn.DynamicGraph.store.record.DynamicVersionRecord;
import cn.DynamicGraph.store.record.DynamicVersionRecordAllocator;

import java.util.Collection;

public interface NodeVersionLabels {
    long[] get(NodeVersionStore var1);

    long[] getIfLoaded();

    Collection<DynamicVersionRecord> put(long[] var1, NodeVersionStore var2, DynamicVersionRecordAllocator var3);

    Collection<DynamicVersionRecord> add(long var1, NodeVersionStore var3, DynamicVersionRecordAllocator var4);

    Collection<DynamicVersionRecord> remove(long var1, NodeVersionStore var3);

    boolean isInlined();
}
