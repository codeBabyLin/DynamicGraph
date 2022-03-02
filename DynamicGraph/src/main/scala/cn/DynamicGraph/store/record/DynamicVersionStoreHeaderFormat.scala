package cn.DynamicGraph.store.record

import org.neo4j.io.pagecache.PageCursor
import org.neo4j.kernel.impl.store.IntStoreHeaderFormat
import org.neo4j.kernel.impl.store.format.RecordFormat

class DynamicVersionStoreHeaderFormat(dataSizeFromConfiguration: Int, recordFormat: RecordFormat[DynamicVersionRecord])
  extends IntStoreHeaderFormat(dataSizeFromConfiguration + recordFormat.getRecordHeaderSize){

  override def writeHeader(cursor: PageCursor): Unit = {
    if (this.header >= 1 && this.header <= 65535) super.writeHeader(cursor)
    else throw new IllegalArgumentException("Illegal block size[" + this.header + "], limit is 65535")
  }
}
