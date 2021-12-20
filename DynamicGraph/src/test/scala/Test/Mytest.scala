package Test

import org.junit.Test

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

}
