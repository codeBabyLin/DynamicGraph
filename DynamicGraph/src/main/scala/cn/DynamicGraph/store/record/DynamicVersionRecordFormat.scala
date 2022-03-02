package cn.DynamicGraph.store.record

import org.neo4j.io.pagecache.PageCursor
import org.neo4j.kernel.impl.store.format.{BaseOneByteHeaderRecordFormat, BaseRecordFormat}
import org.neo4j.kernel.impl.store.record.{DynamicRecord, Record, RecordLoad}

class DynamicVersionRecordFormat extends BaseOneByteHeaderRecordFormat[DynamicVersionRecord](BaseRecordFormat.INT_STORE_HEADER_READER,8,16,36){

  val RECORD_HEADER_SIZE = 12

  override def newRecord(): DynamicVersionRecord = new DynamicVersionRecord(-1)

  override def read(record: DynamicVersionRecord, cursor: PageCursor, mode: RecordLoad, recordSize: Int): Unit = {
    val firstInteger:Long = cursor.getInt.toLong & 4294967295L
    val isStartRecord = (firstInteger & -2147483648L) == 0L
    val inUse = (firstInteger & 268435456L) != 0L
    if (mode.shouldLoad(inUse)) {
      val dataSize = recordSize - this.getRecordHeaderSize
      val nrOfBytes = (firstInteger & 16777215L).toInt
      if (nrOfBytes > recordSize) {
        cursor.setCursorException(payloadTooBigErrorMessage(record, recordSize, nrOfBytes))
        return
      }
      val nextBlock = cursor.getInt.toLong & 4294967295L
      val version = cursor.getInt
      val nextModifier = (firstInteger & 251658240L) << 8
      val longNextBlock = BaseRecordFormat.longFromIntAndMod(nextBlock, nextModifier)
      record.initialize(inUse, isStartRecord, longNextBlock, -1, nrOfBytes,version)
      if (longNextBlock != Record.NO_NEXT_BLOCK.intValue.toLong && nrOfBytes < dataSize || nrOfBytes > dataSize) {
        cursor.setCursorException(this.illegalBlockSizeMessage(record, dataSize))
        return
      }
      readData(record, cursor)
    }
    else record.setInUse(inUse)
  }

  override def write(record: DynamicVersionRecord, cursor: PageCursor, i: Int): Unit = {
    if (record.inUse) {
      val nextBlock = record.getNextBlock
      val version = record.getVersion
      var highByteInFirstInteger = if (nextBlock == Record.NO_NEXT_BLOCK.intValue.toLong) 0
      else ((nextBlock & 64424509440L) >> 8).toInt
      highByteInFirstInteger |= Record.IN_USE.byteValue << 28
      highByteInFirstInteger |= (if (record.isStartRecord) 0
      else 1) << 31
      var firstInteger = record.getLength
      assert(firstInteger < 16777215)
      firstInteger |= highByteInFirstInteger
      cursor.putInt(firstInteger)
      cursor.putInt(nextBlock.toInt)
      cursor.putInt(version)
      cursor.putBytes(record.getData)
    }
    else cursor.putByte(Record.NOT_IN_USE.byteValue)
  }

  def payloadTooBigErrorMessage(record: DynamicVersionRecord, recordSize: Int, nrOfBytes: Int): String = s"DynamicVersionRecord[${record.getId}}] claims to have a payload of ${nrOfBytes} bytes, which is larger than the record size of ${recordSize} bytes."

  private def illegalBlockSizeMessage(record: DynamicVersionRecord, dataSize: Int): String = s"Next block set[${record.getNextBlock}}] current block illegal size[${record.getLength}/${dataSize}}]"

  def readData(record: DynamicVersionRecord, cursor: PageCursor): Unit = {
    val len = record.getLength
    if (len == 0) record.setData(DynamicRecord.NO_DATA)
    else {
      var data = record.getData
      if (data == null || data.length != len) data = new Array[Byte](len)
      cursor.getBytes(data)
      record.setData(data)
    }
  }

  override def getNextRecordReference(record: DynamicVersionRecord): Long = record.getNextBlock
}
