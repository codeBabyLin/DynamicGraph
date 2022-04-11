//package cn.DynamicGraph
//
//import java.io.File
//
//import org.junit.{After, Before, Test}
//import org.neo4j.graphdb.{GraphDatabaseService, Label, RelationshipType}
//import org.neo4j.graphdb.factory.GraphDatabaseFactory
//import collection.JavaConverters._
//class RelationshipVersionTest {
//
//  def registerShutdownHook(graphDb: GraphDatabaseService): Unit ={
//    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
//      override def run(): Unit = {
//        graphDb.shutdown()
//      }
//    }))
//  }
//
//  var graphDb: GraphDatabaseService = _
//
//  def delfile(file: File): Unit ={
//    if(file.isDirectory){
//      val files = file.listFiles()
//      files.foreach(delfile)
//    }
//    file.delete()
//  }
//
//  @Before
//  def init(): Unit ={
//    val path = "F:\\DynamicGraphStore"
//    delfile(new File(path))
//
//  }
//
//  @Test
//  def testRelationShip(): Unit ={
//    val path = "F:\\DynamicGraphStore"
//    val dataBaseDir = new File(path,"data")
//    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
//    registerShutdownHook(graphDb)
//    var tx = graphDb.beginTx()
//    var node1 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
//    val nodeId1 = node1.getId
//    var node2 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
//    val nodeId2 = node2.getId
//    val relType = new RelationshipType {
//      override def name(): String = "friend"
//    }
//    node1.setVersionLabel(new Label {
//      override def name(): String = "boy"
//    },10086)
//    node2.setVersionLabel(new Label {
//      override def name(): String = "boy"
//    },10086)
//
//
//
//    var r1 = node1.createRelationshipTo(node2,relType)
//    val re1Id = r1.getId
//    //r1.setRelVersion(100)
//
//    //r1.setVersionProeprty("year","2022",1001)
//   // r1.setVersionProeprty("year","2077",10086)
//
//    tx.success()
//    tx.close()
//
//
//    tx = graphDb.beginTx()
//    r1 = graphDb.getRelationshipById(re1Id)
//   // println(r1.getVersionProperty("year",10086))
//    println(r1.getRelVersion)
//
//
//
//
//
//  }
//
//
//  @After
//  def close(): Unit ={
//    //graphDb.shutdown()
//  }
//
//}
