package cn.DynamicGraph.serialization

import io.netty.buffer.{ByteBuf, ByteBufAllocator, Unpooled}
import org.neo4j.values.storable.{ByteArray, LongValue, StringValue, StringWrappingStringValue, Value}

object Value2Byte {

  def write(vv: Map[Int,Any]): Array[Byte] ={
    val allocator: ByteBufAllocator = ByteBufAllocator.DEFAULT
    val byteBuf: ByteBuf = allocator.heapBuffer()
    write(vv,byteBuf)
  }
  def exportBytes(byteBuf: ByteBuf): Array[Byte] = {
    val dst = new Array[Byte](byteBuf.writerIndex())
    byteBuf.readBytes(dst)
    dst
  }
  def write(vv: Map[Int, Any], byteBuf: ByteBuf): Array[Byte] ={
    MapSerializer.writeMap(vv,byteBuf)
    exportBytes(byteBuf)
  }

  def read(ary: Array[Byte]): Map[Int,Any] ={
    val allocator: ByteBufAllocator = ByteBufAllocator.DEFAULT
    val byteBuf: ByteBuf = Unpooled.copiedBuffer(ary)
    MapSerializer.readMap(byteBuf)
  }

  def readLongLongMap(ary: Array[Byte]): Map[Long, Long] = {
    val allocator: ByteBufAllocator = ByteBufAllocator.DEFAULT
    val byteBuf: ByteBuf = Unpooled.copiedBuffer(ary)
    readLongLongMap(byteBuf)
  }

  def writeLongLongMap(map: Map[Long, Long], byteBuf: ByteBuf): Array[Byte] ={

    byteBuf.writeInt(map.size)
    map.foreach(x => {
      byteBuf.writeLong(x._1)
      byteBuf.writeLong(x._2)
    })
    //writeLongLongMap(vv,byteBuf)
    exportBytes(byteBuf)
  }
  def writeLongLongMap(vv: Map[Long,Long]): Array[Byte] ={
    val allocator: ByteBufAllocator = ByteBufAllocator.DEFAULT
    val byteBuf: ByteBuf = allocator.heapBuffer()
    writeLongLongMap(vv,byteBuf)
  }
/*  def writeLongLongMap(map: Map[Long, Long], byteBuf: ByteBuf): Unit ={
    byteBuf.writeInt(map.size)
    map.foreach(x => {
      byteBuf.writeLong(x._1)
      byteBuf.writeLong(x._2)
    })
  }*/

  def readLongLongMap(byteBuf: ByteBuf): Map[Long, Long] = {
    val propNum: Int = byteBuf.readInt()
    val propsMap: Map[Long, Long] = new Array[Long](propNum).map(it => {
      val key = byteBuf.readLong()
      val value = byteBuf.readLong()
      key->value
    }).toMap
    propsMap
  }

}
