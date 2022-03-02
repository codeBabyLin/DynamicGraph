package cn.DynamicGraph.kernel.impl.store.allocator;

import cn.DynamicGraph.store.record.DynamicVersionRecord;
import cn.DynamicGraph.store.record.DynamicVersionRecordAllocator;
import cn.DynamicGraph.store.record.DynamicVersionRecord;
import org.neo4j.helpers.collection.Iterators;
import org.neo4j.kernel.impl.store.record.DynamicRecord;

import java.util.Collection;
import java.util.Iterator;

public class ReusableVersionRecordsAllocator implements DynamicVersionRecordAllocator {
    private final int recordSize;
    private final Iterator<DynamicVersionRecord> recordIterator;

    public ReusableVersionRecordsAllocator(int recordSize, DynamicVersionRecord... records) {
        this.recordSize = recordSize;
        this.recordIterator = Iterators.iterator(records);
    }

    public ReusableVersionRecordsAllocator(int recordSize, Collection<DynamicVersionRecord> records) {
        this.recordSize = recordSize;
        this.recordIterator = records.iterator();
    }

    public ReusableVersionRecordsAllocator(int recordSize, Iterator<DynamicVersionRecord> recordsIterator) {
        this.recordSize = recordSize;
        this.recordIterator = recordsIterator;
    }

    public int getRecordDataSize() {
        return this.recordSize;
    }

    public DynamicVersionRecord nextRecord() {
        DynamicVersionRecord record = (DynamicVersionRecord)this.recordIterator.next();
        if (!record.inUse()) {
            record.setCreated();
        }

        record.setInUse(true);
        return record;
    }

    public boolean hasNext() {
        return this.recordIterator.hasNext();
    }
}
