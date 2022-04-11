//package cn.driver
//
//import java.io.File
//
//import org.neo4j.server.CommunityEntryPoint
//
//object Start {
//
//  def delfile(file: File): Unit ={
//    if(file.isDirectory){
//      val files = file.listFiles()
//      files.foreach(delfile)
//    }
//    file.delete()
//  }
//
//  def main(args: Array[String]): Unit = {
//    val NEO4J_HOME = "F:\\DynamicGraphStore\\Server"
//    delfile(new File(NEO4J_HOME))
//    val NEO4J_CONF = "F:\\IdCode\\DynamicGraph\\"
//    CommunityEntryPoint.main(Array(s"--home-dir=${NEO4J_HOME}", s"--config-dir=${NEO4J_CONF}"))
//
//  }
//
//}
