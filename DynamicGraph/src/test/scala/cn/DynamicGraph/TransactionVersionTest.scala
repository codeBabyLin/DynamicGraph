package cn.DynamicGraph

import java.io.File
import java.util.function.{BiConsumer, Consumer}

import cn.DynamicGraph.Common.{DGVersion, Serialization}
import org.junit.{Assert, Test}
import org.neo4j.graphdb.{Label, Node, RelationshipType}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.kernel.impl.transaction.log.TransactionIdStore
import  collection.JavaConverters._
class TransactionVersionTest extends BaseTest {


  //to do make a class to supply versionget and versioncommit 5
  //to do rewrite NodeProxyM1,M2 and DynamicGraphDatabaseFacade 6
  //todo find data to expriment 7
  //todo find paper make a conclusion 8
  //todo to verify project code 9
  //todo historyreadFunction
  //todo write a property
  //to do finish nodeCreate and nodeDelete 1
  //to do finish nodelabel add and delete 2
  //to do finish nodeProperty addd and delete 3
  //to do finish rel as node 4
  //to do verify version manager 0
  //to do to writecomplete test
/*
  val txIdStore = db.getDependencyResolver.resolveDependency(classOf[TransactionIdStore])
  txIdStore.getLastCommittedTransactionId*/

  @Test
  def testVersionStore(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    //println(graphDb.getCurrentVersion)
    var version = graphDb.getNextVersion
    graphDb.commitVersion(version, true)//
    version = graphDb.getNextVersion
    graphDb.commitVersion(version, false)//
    version = graphDb.getNextVersion
    graphDb.commitVersion(version, true)//
    //println(graphDb.getCurrentVersion)
    val versions = graphDb.listAllVersions()

    Assert.assertEquals(2,versions.size)



  }



  @Test
  def testVersionSet(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    ///var version = graphDb.getNextVersion
    var tx = graphDb.beginTx()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()
    var node = graphDb.createNode()
    val tVersion = tx.getVersion
    val nodeId = node.getId
    node.addLabel(new Label {
      override def name(): String = "student"
    })
    node.addLabel(new Label {
      override def name(): String = "boy"
    })
    //node.setProperty()
    tx.success()
    tx.close


    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId)
    val version = node.getNodeVersion
    node.delete()
    node.getVersionLabel
    Assert.assertEquals(tVersion,version)



  }


  @Test
  def testNodeCreateAndDelete(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode().getId
    var node2 = graphDb.createNode().getId
    var node3 = graphDb.createNode().getId
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    graphDb.getNodeById(node1).delete()
    graphDb.getNodeById(node2).delete()
    var node4 = graphDb.createNode().getId
    var node5 = graphDb.createNode().getId
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//2

    var node6 = graphDb.createNode().getId
    var node7 = graphDb.createNode().getId
    graphDb.getNodeById(node3).addLabel(new Label {
      override def name(): String = "boy"
    })
    tx.success()
    tx.close()




    tx = graphDb.beginTx()

    val nodeh = graphDb.getNodeById(node3)
    Assert.assertEquals(false,DGVersion.hasEndVersion(nodeh.getNodeVersion))
    println(s"node3 => ${DGVersion.toString(nodeh.getNodeVersion())}")
    val nodehs = graphDb.getNodeById(node1)
    Assert.assertEquals(1,DGVersion.getEndVersion(nodehs.getNodeVersion))
    println(s"node1 => ${DGVersion.toString(nodehs.getNodeVersion())}")

 /*   val nodes = graphDb.getAllNodes
    nodes.stream().forEach(new Consumer[Node] {
      override def accept(t: Node): Unit = {
        println(t.getId)
      }
    })*/
    tx.success()
    tx.close()




  }

  @Test
  def testRelCreatAndDelete(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode()
    var node2 = graphDb.createNode()
    var node3 = graphDb.createNode()
    var r1 = node1.createRelationshipTo(node2,new RelationshipType {
      override def name(): String = "friend"
    })
    var r2 = node2.createRelationshipTo(node3,new RelationshipType {
      override def name(): String = "friend"
    })
    val r1Id = r1.getId
    val r2Id = r2.getId
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    graphDb.getRelationshipById(r1Id).delete()
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//2
    val rev = graphDb.getRelationshipById(r1Id).getRelVersion

    Assert.assertEquals(true,DGVersion.hasEndVersion(rev))
    Assert.assertEquals(1,DGVersion.getEndVersion(rev))
    println(s"rel1 => ${DGVersion.toString(rev)}")



  }

  @Test
  def nodeLabelAddAndDelete(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode()
    val nodeId = node1.getId
    node1.addLabel(new Label {
      override def name(): String = "boy"
    })
    node1.addLabel(new Label {
      override def name(): String = "student"
    })

    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    graphDb.createNode()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()//2
    node1 = graphDb.getNodeById(nodeId)

    node1.removeLabel(new Label {
      override def name(): String = "boy"
    })

    tx.success()

    tx.close()



    tx = graphDb.beginTx()//3

    node1 = graphDb.getNodeById(nodeId)
    val map = node1.getVersionLabel()
    //map.asScala
    Assert.assertEquals(2,map.size())
    node1.getVersionLabel.asScala.foreach(x => {
      val s = s"label:${x._1.name()},${DGVersion.toString(x._2)}"
      println(s)

    })



  }

  @Test
  def testNodeaddAndremoveProperty(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode()
    var node3 = graphDb.createNode()
    val nodeId = node1.getId
    node1.setProperty("name","codeBaby")
    node1.setProperty("age",2022)
    var r1 = node1.createRelationshipTo(node3, new RelationshipType {
      override def name(): String = "friend"
    })
    val r1Id = r1.getId
    r1.setProperty("time","2022.3.14")
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    graphDb.createNode()
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//2
    node1 = graphDb.getNodeById(nodeId)
    r1 = graphDb.getRelationshipById(r1Id)
    r1.setProperty("time","2077.9.9")
    node1.setProperty("name","JoeJoe")
    node1.removeProperty("age")

    tx.success()
    tx.close()

    tx =graphDb.beginTx()

    node1 = graphDb.getNodeById(nodeId)
    r1 = graphDb.getRelationshipById(r1Id)
    val rjk: Object = r1.getProperty("time")
    val obj: Object = node1.getProperty("name")
    //val xkl:Object  = node1.getProperty("age")

   /* val data = Serialization.readJMapFromObject(obj)
    val data2 = Serialization.readJMapFromObject(xkl)
    val data3 = Serialization.readJMapFromObject(rjk)
    Assert.assertEquals(2,data.size())
    Assert.assertEquals(1,data2.size())
    Assert.assertEquals(2,data3.size())
    println(data)
    println(data2)
    println(data3)*/
    println(rjk)
    println(obj)
    //println(xkl)

  }


  @Test
  def testNodeFind(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode()
    var node3 = graphDb.createNode()
    val nodeId = node1.getId
    val label = new Label {
      override def name(): String = "test"
    }
    node1.setProperty("name","codeBaby")
    node1.setProperty("age",2022)
    node1.addLabel(label)
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    node1 = graphDb.findNode(label,"name","codeBaby")
    val obj = node1.getProperty("age")
    println(obj)
    tx.success()
    tx.close()

    //graphDb


  }

  @Test
  def testNewPropertyAddAndRemove(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode()
    var node3 = graphDb.createNode()
    val nodeId = node1.getId
    node1.setProperty("name","codeBaby")
    node1.setProperty("age",2022)
    var r1 = node1.createRelationshipTo(node3, new RelationshipType {
      override def name(): String = "friend"
    })
    val r1Id = r1.getId
    r1.setProperty("time","2022.3.14")
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//1
    graphDb.createNode()
    tx.success()
    tx.close()

    tx = graphDb.beginTx()//2
    node1 = graphDb.getNodeById(nodeId)
    r1 = graphDb.getRelationshipById(r1Id)
    r1.setProperty("time","2077.9.9")
    node1.setProperty("name","JoeJoe")
    node1.removeProperty("age")

    tx.success()
    tx.close()

    tx =graphDb.beginTx()

    node1 = graphDb.getNodeById(nodeId)
    r1 = graphDb.getRelationshipById(r1Id)
    val rjk: Object = r1.getProperty("time")
    val obj: Object = node1.getProperty("name")
    //val xkl:Object  = node1.getProperty("age")

    /* val data = Serialization.readJMapFromObject(obj)
     val data2 = Serialization.readJMapFromObject(xkl)
     val data3 = Serialization.readJMapFromObject(rjk)
     Assert.assertEquals(2,data.size())
     Assert.assertEquals(1,data2.size())
     Assert.assertEquals(2,data3.size())
     println(data)
     println(data2)
     println(data3)*/
    println(rjk)
    println(obj)
    //println(xkl)

  }



}
