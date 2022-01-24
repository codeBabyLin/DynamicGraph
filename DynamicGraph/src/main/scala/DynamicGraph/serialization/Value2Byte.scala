package DynamicGraph.serialization

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

}
