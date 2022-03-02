package cn.DynamicGraph

import java.io.File

import org.junit.{After, Before}
import org.neo4j.graphdb.GraphDatabaseService

class BaseTest {
  def registerShutdownHook(graphDb: GraphDatabaseService): Unit ={
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        graphDb.shutdown()
      }
    }))
  }

  var graphDb: GraphDatabaseService = _

  def delfile(file: File): Unit ={
    if(file.isDirectory){
      val files = file.listFiles()
      files.foreach(delfile)
    }
    file.delete()
  }

  @Before
  def init(): Unit ={
    val path = "F:\\DynamicGraphStore"
    delfile(new File(path))

  }
  @After
  def close(): Unit ={
    //graphDb.shutdown()
  }
}
