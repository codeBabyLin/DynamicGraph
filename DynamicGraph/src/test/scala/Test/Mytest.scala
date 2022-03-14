package Test

import cn.DynamicGraph.Common.DGVersion
import io.netty.buffer.{ByteBuf, ByteBufAllocator, Unpooled}
import org.junit.{Assert, Test}
import org.neo4j.values.storable.{ArrayValue, ByteValue, LongValue, NoValue, StringValue, Value, Values}

class Mytest extends{
  @Test
  def testok(): Unit ={
    val s1 = System.currentTimeMillis()
    var i = 999999999
    while(i>0){
      i = i - 1
    }
    //val log = Logger.getLogger(this.getClass)
    //val log2 = Logger.getRootLogger
    //log2.info("hello dfdf")
    //log.info("hello ")
    val s2 = System.currentTimeMillis()
    println(s1)
    println(s2)

  }
  def value2byte(value: Value): Unit ={
    val byteBuf: ByteBuf = Unpooled.buffer()
    value match{
      case value: ArrayValue =>
      case value: NoValue =>
      case value:LongValue =>
      case value: StringValue =>

    }
  }

  @Test
  def testValue(): Unit ={



    val v = Values.of("hello world")
    val k = v
    val l: Long = 19
    val sk = BigInt(212222).toByteArray
    val test = Values.of(sk)
    println(v)
    println(test)

    println(BigInt(10).toByteArray)
    println(1.toByte)



  }

  @Test
  def testKL(): Unit ={
    val x = DGVersion.setStartEndVersion(1, 10)

    Assert.assertEquals(1,DGVersion.getStartVersion(x))
    Assert.assertEquals(10,DGVersion.getEndVersion(x))
    //println(DGVersion.getStartVersion(x))
    //println(DGVersion.getEndVersion(x))

    val y  = DGVersion.setEndVersion(100)

    Assert.assertEquals(0,DGVersion.getStartVersion(y))
    Assert.assertEquals(100,DGVersion.getEndVersion(y))


    val z  = DGVersion.setStartVersion(100)

    Assert.assertEquals(100,DGVersion.getStartVersion(z))
    Assert.assertEquals(65535,DGVersion.getEndVersion(z))

    val yl = DGVersion.setStartVersion(90,y)
    Assert.assertEquals(90,DGVersion.getStartVersion(yl))
    Assert.assertEquals(100,DGVersion.getEndVersion(yl))

    val zl  = DGVersion.setEndVersion(200,z)

    Assert.assertEquals(100,DGVersion.getStartVersion(zl))
    Assert.assertEquals(200,DGVersion.getEndVersion(zl))

    println(DGVersion.toString(zl))

  }


  @Test
  def testjkjk(): Unit ={
    val x = 1
    val y = -1
    println((y<< 2) & x)
  }

  @Test
  def testByte(): Unit ={
    val allocator = ByteBufAllocator.DEFAULT;
    val byteBuf = allocator.buffer()
    val sds: Int = -127
    byteBuf.writeByte(sds)
    val hj = byteBuf.readByte().toInt
    println(hj)
  }

}
