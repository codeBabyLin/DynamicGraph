package cn.DynamicGraph

import java.io.File

import org.neo4j.graphdb.factory.GraphDatabaseFactory

class TransactionVersionTest extends BaseTest {


  def testNodeAddTransaction(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)
    var tx = graphDb.beginTx()
    var node = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    val nodeId = node.getId
    println(node.getNodeVersion())

    tx.success()
    tx.close()
    tx = graphDb.beginTx()
    node = graphDb.getNodeById(nodeId).asInstanceOf[DynamicNodeProxyM2]
    println(node.getNodeVersion())

    val node1 = graphDb.createNode().asInstanceOf[DynamicNodeProxyM2]
    node1.getNodeVersion()

  }

}
