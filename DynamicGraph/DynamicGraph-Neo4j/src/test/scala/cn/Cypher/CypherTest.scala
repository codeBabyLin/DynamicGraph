package cn.Cypher

import java.io.File

import cn.DynamicGraph.BaseTest
import org.junit.Test
import org.neo4j.graphdb.Label

class CypherTest extends BaseTest{

  @Test
  def testOkl() {
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path, "data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)

    var tx = graphDb.beginTx()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()
    tx.success()
    tx.close()


    tx = graphDb.beginTx()
    tx.success()
    tx.close()



    val cy = "Create(n:student{name:'JoeJoe',age:67})"
    graphDb.execute(cy)

    tx = graphDb.beginTx()

    val node = graphDb.findNode(new Label {
      override def name(): String = "student"
    },"age",67)
    println(node.getNodeVersion)


  }

}
