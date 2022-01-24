package Test

import io.netty.buffer.{ByteBuf, Unpooled}
import org.junit.Test
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


}
