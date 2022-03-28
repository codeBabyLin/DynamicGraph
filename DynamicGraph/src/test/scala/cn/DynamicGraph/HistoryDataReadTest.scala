package cn.DynamicGraph

import java.io.File

import cn.DynamicGraph.Common.DGVersion
import org.junit.{Assert, Test}
import org.neo4j.graphdb.{GraphDatabaseService, RelationshipType, Transaction}
import org.neo4j.graphdb.factory.GraphDatabaseFactory

class HistoryDataReadTest extends BaseTest {


  def prepareHistoryData(graphDb: GraphDatabaseService): Unit ={
    var tx = graphDb.beginTx() //0  n:0 r:0
    val node1 = graphDb.createNode()
    val ndoe1Id = node1.getId
    val node2 = graphDb.createNode()
    val ndoe2Id = node2.getId
    val node3 = graphDb.createNode()
    val ndoe3Id = node3.getId
    val node4 = graphDb.createNode()
    val ndoe4Id = node4.getId

    val r1 = node1.createRelationshipTo(node2,new RelationshipType {
      override def name(): String = "friend"
    })
    val r1Id = r1.getId
    val r2 = node3.createRelationshipTo(node4,new RelationshipType {
      override def name(): String = "fans"
    })
    val r2Id = r2.getId
    tx.success()
    tx.close()


    tx = graphDb.beginTx()//1 n:4 r:2
    graphDb.createNode()
    graphDb.createNode()
    graphDb.createNode()
    graphDb.createNode()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()//2 n: 8  r:2

    graphDb.createNode()
    graphDb.createNode()
    graphDb.createNode()
    graphDb.createNode()
    graphDb.getRelationshipById(r1Id).delete()
    graphDb.getNodeById(ndoe1Id).delete()

    tx.success()
    tx.close()

    //t3 n:11 r 1






  }

  @Test
  def testReadSpecifiedVersion(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)
    prepareHistoryData(graphDb)

    val tx = graphDb.beginTx()

    var nodecnt = graphDb.getAllNodesInSingleVersion(2).stream().count()
    var relcnt = graphDb.getAllRelationshipsInSingleVersion(2).stream().count()
    Assert.assertEquals(8,nodecnt)
    Assert.assertEquals(2,relcnt)

    nodecnt = graphDb.getAllNodesInVersionDelta(1,2).stream().count()
    relcnt = graphDb.getAllRelationshipsInVersionDelta(1,2).stream().count()
    Assert.assertEquals(4,nodecnt)
    Assert.assertEquals(0,relcnt)


    nodecnt = graphDb.getAllNodesInVersions(1,3).stream().count()
    relcnt = graphDb.getAllRelationshipsInVersions(1,3).stream().count()
    Assert.assertEquals(3,nodecnt)
    Assert.assertEquals(1,relcnt)


    nodecnt = graphDb.getAllNodesInVersionDelta(1,3).stream().count()
    relcnt = graphDb.getAllRelationshipsInVersionDelta(1,3).stream().count()
    Assert.assertEquals(9,nodecnt)
    Assert.assertEquals(1,relcnt)



  }





}
