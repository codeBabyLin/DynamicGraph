package cn.DynamicGraph.kernel.impl.store.allocator;

import cn.DynamicGraph.kernel.impl.store.allocator.ReusableVersionRecordsAllocator;
import cn.DynamicGraph.store.record.DynamicVersionRecord;
import cn.DynamicGraph.store.record.DynamicVersionRecordAllocator;

import java.util.Collection;
import java.util.Iterator;

public class ReusableVersionRecordsCompositeAllocator implements DynamicVersionRecordAllocator {
    private final ReusableVersionRecordsAllocator reusableRecordsAllocator;
    private final DynamicVersionRecordAllocator recordAllocator;

    public ReusableVersionRecordsCompositeAllocator(Collection<DynamicVersionRecord> records, DynamicVersionRecordAllocator recordAllocator) {
        this(records.iterator(), recordAllocator);
    }

    public ReusableVersionRecordsCompositeAllocator(Iterator<DynamicVersionRecord> recordsIterator, DynamicVersionRecordAllocator recordAllocator) {
        this.reusableRecordsAllocator = new ReusableVersionRecordsAllocator(recordAllocator.getRecordDataSize(), recordsIterator);
        this.recordAllocator = recordAllocator;
    }

    public int getRecordDataSize() {
        return this.recordAllocator.getRecordDataSize();
    }

    public DynamicVersionRecord nextRecord() {
        return this.reusableRecordsAllocator.hasNext() ? this.reusableRecordsAllocator.nextRecord() : this.recordAllocator.nextRecord();
    }
}
