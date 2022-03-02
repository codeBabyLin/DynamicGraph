package cn.DynamicGraph

import java.io.File


import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.factory.GraphDatabaseFactory


object NeoStarter{

  def registerShutdownHook(graphDb: GraphDatabaseService): Unit ={
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        graphDb.shutdown()
      }
    }))
  }
  def main(args: Array[String]): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    val graphDb: GraphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    val tx = graphDb.beginTx()
    //graphDb.asInstanceOf[DynamicGraphDatabaseFacade].print_test()
    //val node = graphDb.createNode().asInstanceOf[DynamicNodeProxy]
    val node = graphDb.getNodeById(1).asInstanceOf[DynamicNodeProxyM1]



    //node.setVersionProperty("name","001","2010")
    //node.setVersionProperty("age",15,"2010")
    //node.setVersionProperty("addr","beijing","2010")

    //node.setVersionProperty("name","001","2011")
    //node.setVersionProperty("age",16,"2011")
    //node.setVersionProperty("addr","shanghai","2011")


    //node.re

    //node.setProperty("test","001")
    //node.setProperty("test","007")
    //println(node.getId)
    //println(node.getVersionProterty("age","2010"))
    //println(node.getVersionAllProperties("2011"))
    println(node.getAllProperties)



    tx.success()
    tx.close()

    graphDb.shutdown()

    //print("hello world")
  }
}