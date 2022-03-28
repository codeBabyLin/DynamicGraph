package Test

import java.text.SimpleDateFormat
import java.util
import java.util.{Calendar, Date}

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
  def getDateOff(format: String,date1Str: String,date2Str: String):Int = {
    val simpleDateFormat = new SimpleDateFormat(format)

    val date1 =simpleDateFormat.parse(date1Str)
    val date2 = simpleDateFormat.parse(date2Str)
    val calender:Calendar = Calendar.getInstance()
    val calender2:Calendar = Calendar.getInstance()
    calender.setTime(date1)
    calender2.setTime(date2)

    var dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)+1}.${calender.get(Calendar.DATE)}"
    var dayCount: Int = 0
    while(!calender.equals(calender2)) {
      dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)+1}.${calender.get(Calendar.DATE)}"
      //dateArray.update(dayCount,dateStr)
      dayCount = dayCount+1
      calender.add(Calendar.DATE,1)
    }
    dayCount
  }
  def getDateOffArray(format: String,date1Str: String,date2Str: String,dayOff: Int):Array[String] = {
    val simpleDateFormat = new SimpleDateFormat(format)

    val date1 =simpleDateFormat.parse(date1Str)
    val date2 = simpleDateFormat.parse(date2Str)
    val calender:Calendar = Calendar.getInstance()
    val calender2:Calendar = Calendar.getInstance()
    calender.setTime(date1)
    calender2.setTime(date2)


    val dateArray:Array[String] = new Array[String](dayOff)
    var dayCount: Int = 0

    while(!calender.equals(calender2)) {
      val str = calender.getTime.formatted(format)
      val dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)+1}.${calender.get(Calendar.DATE)}"
      dateArray.update(dayCount,dateStr)
      dayCount = dayCount+1
      calender.add(Calendar.DATE,1)
    }

    dateArray

  }


  @Test
  def testDate(): Unit ={

    val simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd")

    val date1 =simpleDateFormat.parse("2021.12.21")
    val date2 = simpleDateFormat.parse("2022.3.22")
    val calender:Calendar = Calendar.getInstance()
    val calender2:Calendar = Calendar.getInstance()
    calender.setTime(date1)
    calender2.setTime(date2)

    var dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)+1}.${calender.get(Calendar.DATE)}"
    val endateStr = "2022.3.22"
    val dateArray:Array[String] = new Array[String](91)
    var dayCount: Int = 0

    while(!calender.equals(calender2)) {
      dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)+1}.${calender.get(Calendar.DATE)}"
      //val str = calender.getTime.toString
      println(dateStr)
      dateArray.update(dayCount,dateStr)
      dayCount = dayCount+1
      calender.add(Calendar.DATE,1)
    }


   // println(s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH)}.${calender.get(Calendar.DATE)}")


  }



}
