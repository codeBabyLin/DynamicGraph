package DynamicGraph

import DynamicGraph.store.DynamicStoreType
import org.junit.Test

class NeoStoreTest {

  @Test
  def testNeoStore(): Unit ={
    val tesA: Array[String] = Array("1","2","3")
    tesA.map(x => {
      val i = x.toInt
      i*10
    }).foreach(println)
  }

}
