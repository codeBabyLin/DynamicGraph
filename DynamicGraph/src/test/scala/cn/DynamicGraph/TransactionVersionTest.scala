package cn.DynamicGraph

import java.io.File
import java.util.function.Consumer

import cn.DynamicGraph.Common.{DGVersion, Serialization}
import org.junit.{Assert, Test}
import org.neo4j.graphdb.{Label, Node, RelationshipType}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.kernel.impl.transaction.log.TransactionIdStore

class TransactionVersionTest extends BaseTest {


  //todo make a class to supply versionget and versioncommit 5
  //todo rewrite NodeProxyM1,M2 and DynamicGraphDatabaseFacade 6
  //todo find data to expriment 7
  //todo find paper make a conclusion 8
  //todo to verify project code 9
  //to do finish nodeCreate and nodeDelete 1
  //to do finish nodelabel add and delete 2
  //to do finish nodeProperty addd and delete 3
  //to do finish rel as node 4
  //to do verify version manager 0
/*
  val txIdStore = db.getDependencyResolver.resolveDependency(classOf[TransactionIdStore])
  txIdStore.getLastCommittedTransactionId*/
  @Test
  def testNodeAddTransaction(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)
    var nm = graphDb.getCurrentVersion
    var tx = graphDb.beginTx()
    nm = graphDb.getCurrentVersion
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val nodeId = node.getId
    println(DGVersion.getStartVersion(node.getNodeVersion()))
    println(DGVersion.getStartVersion(node.getNodeVersion()))
    tx.success()
    tx.close()

    nm = graphDb.getCurrentVersion
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    println(DGVersion.getStartVersion(node.getNodeVersion()))
    nm = graphDb.getCurrentVersion
    node.delete()
    val node1 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    println(DGVersion.getStartVersion(node1.getNodeVersion()))
    tx.success()
    tx.close
    nm = graphDb.getCurrentVersion
    tx = graphDb.beginTx()
    tx.success()
    tx.close
    val sk = graphDb.listAllVersions()
    nm = graphDb.getCurrentVersion
    tx = graphDb.beginTx()
    tx.success()
    tx.close

    nm = graphDb.getCurrentVersion
    sk.foreach(println)

  }

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
    graphDb.listAllVersions().map(println)
    //println()


  }

  @Test
  def testNodeCreateAndGet(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    ///var version = graphDb.getNextVersion
    var tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val nodeId = node.getId
    var version = tx.getVersion

    tx.success()
    tx.close()
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    val nc = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val v1 = DGVersion.getStartVersion(node.getNodeVersion())
    val x = DGVersion.getStartVersion(tx.getVersion)
    tx.success()
    tx.close()
   // graphDb.commitVersion(version,true)
  //  version = graphDb.getNextVersion
   // tx = graphDb.beginTx(DGVersion.setStartVersion(version))
    val v2 = graphDb.listAllVersions()
    val l3 = v2

    //node.setProperty("")


  }

  @Test
  def testNodeStore(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    ///var version = graphDb.getNextVersion
    var tx = graphDb.beginTx()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
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
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    val version = node.getNodeVersion()
    node.delete()
    node.getVersionLabels(0).map(println)
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
    //node1 = graphDb.getNodeById(node1)
    //node2 = graphDb.getNodeById(node2)
    //node3 = graphDb.getNodeById(node3)
    //node1.delete()
    //node2.delete()
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




    tx = graphDb.beginTx(DGVersion.setStartEndVersion(0,1))

    val nodeh = graphDb.getNodeById(node3).asInstanceOf[DynamicNodeProxyM2]
    println(s"node2 => ${DGVersion.toString(nodeh.getNodeVersion())}")
    val nodehs = graphDb.getNodeById(node1).asInstanceOf[DynamicNodeProxyM2]
    println(s"node2 => ${DGVersion.toString(nodehs.getNodeVersion())}")

    val nodes = graphDb.getAllNodes
    nodes.stream().forEach(new Consumer[Node] {
      override def accept(t: Node): Unit = {
        println(t.getId)
      }
    })
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
    val rev = graphDb.getRelationshipById(r1Id).asInstanceOf[RelationshipProxyEx].getVersion

    println(s"rel1 => ${DGVersion.toString(rev)}")



  }

  @Test
  def nodeLabelAddAndDelete(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()//0
    var node1 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
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
    node1 = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]

    node1.removeLabel(new Label {
      override def name(): String = "boy"
    })

    tx.success()

    tx.close()



    tx = graphDb.beginTx()//3

    node1 = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    node1.getVersionLabels().foreach(x => {
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
    var node1 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    var node3 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
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
    node1 = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    r1 = graphDb.getRelationshipById(r1Id)
    r1.setProperty("time","2077.9.9")
    node1.setProperty("name","JoeJoe")
    node1.removeProperty("age")

    tx.success()
    tx.close()

    tx =graphDb.beginTx()

    node1 = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    r1 = graphDb.getRelationshipById(r1Id)
    val rjk: Object = r1.getProperty("time")
    val obj: Object = node1.getProperty("name")
    val xkl:Object  = node1.getProperty("age")

    val data = Serialization.readJMapFromObject(obj)
    val data2 = Serialization.readJMapFromObject(xkl)
    val data3 = Serialization.readJMapFromObject(rjk)
    println(data)
    println(data2)
    println(data3)




  }


}
