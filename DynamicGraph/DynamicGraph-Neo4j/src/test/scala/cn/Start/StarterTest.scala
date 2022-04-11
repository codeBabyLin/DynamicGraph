//package cn.Start
//
//import java.io.File
//
//import cn.DynamicGraph.BootStrap.Neo4jStarter
//import org.junit.{Before, Test}
//import org.neo4j.server.CommunityEntryPoint
//
//class StarterTest {
//  val path = "F:\\DynamicGraphStore\\Server"
//  def delfile(file: File): Unit ={
//    if(file.isDirectory){
//      val files = file.listFiles()
//      files.foreach(delfile)
//    }
//    file.delete()
//  }
//  @Before
//  def init(): Unit ={
//    delfile(new File(path))
//  }
//
//  @Test
//  def testStart(): Unit ={
//    val args = new Array[String](0);
//    Neo4jStarter.main(args)
//  }
//
//
//}
