package DynamicGraph

import java.io.File

import org.junit.{After, Assert, Before, Test}

import org.neo4j.graphdb.{GraphDatabaseService, Label}
import org.neo4j.graphdb.factory.GraphDatabaseFactory

class NodeVersionTest {

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


  @Test
  def testNodeVersionFunc(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)
    var tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val nodeId = node.getId
    node.setProperty("name","LiBai")
    node.setNodeVersion(100)
    tx.success()
    tx.close()
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    Assert.assertEquals(nodeId,node.getId)
    Assert.assertEquals(100,node.getNodeVersion())
    println(node.getId)
    println(node.getAllProperties)
    println(node.getNodeVersion())
    tx.close()
    graphDb.shutdown()
  }

  @Test
  def testNodeVersion(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")

    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    //t1
    var tx = graphDb.beginTx()
    var node = graphDb.createNode()
   /* node.addLabel(new Label("JoJo") {
      override def name(): String = "JoJo"
    })*/
    //node.getDegree
    node.setProperty("name","LiBai")
    node.setProperty("age",18)
    node.setProperty("year",2021)
    val nodeId = node.getId
    println(nodeId)
    //tx.failure()
    tx.success()
    tx.close()

    //t2
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId)
    node.setProperty("name","DuFu")
    node.setProperty("age",20)
    node.setProperty("year",2022)
    //tx.failure()
    tx.success()
    tx.close()

    //t3

    tx = graphDb.beginTx()

    node = graphDb.getNodeById(nodeId)
    println()
    println(node.getAllProperties)








    graphDb.shutdown()
  }

  @Test
  def testNodeversionProperties(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")

    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)
    //t1
    var tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    node.setVersionProperty("age",18,"1001")
    node.setVersionProperty("year",2021,"1001")
    val nodeId = node.getId
    //println(nodeId)
    tx.success()
    tx.close()
    //t2
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    node.setVersionProperty("age",20,"10086")
    node.setVersionProperty("year",2022,"10086")
    //tx.failure()
      tx.success()
    tx.close()
    //t3
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    println(node.getVersionProterty("age","1001"))
    println(node.getVersionProterty("year","10086"))
    //println(node.getAllProperties)
    graphDb.shutdown()
  }


  @Test
  def testNodeLabelVersion(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")

    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val nodeId = node.getId
    val label1 = new Label {
      override def name(): String = "student"
    }
    val label2 = new Label {
      override def name(): String = "boy"
    }
    node.addLabel(label1)
    //node.addLabel(label2)
    tx.success()
    tx.close()

    tx =  graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]

    node.addLabel(label2)
    tx.success()
    tx.close()

    tx =  graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    node.getLabels.forEach(println)

    graphDb.shutdown()
  }



  @After
  def close(): Unit ={
    //graphDb.shutdown()
  }


}
