package cn.DynamicGraph

import java.io.File

import org.junit.Test
import org.neo4j.graphdb.factory.GraphDatabaseFactory

class CypherTest extends BaseTest{


  @Test
  def testjkl(): Unit ={
    val path = "F:\\DynamicGraphStore"
    val dataBaseDir = new File(path,"data")
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
    registerShutdownHook(graphDb)


    var tx = graphDb.beginTx()
    val cy1 = "match(n) return n"
    var t = graphDb.execute(cy1).hasNext
    println(t)
    val cy2 = "create(n:student{name:'JoeJoe',age:23})"
    graphDb.execute(cy2)
    tx.success()
    tx.close()

    tx = graphDb.beginTx()
    t = graphDb.execute(cy1).hasNext
    println(t)
    val cy3 = "match(n) delete n"
    graphDb.execute(cy3)
    tx.success()
    tx.close()


    tx = graphDb.beginTx()
     t = graphDb.execute(cy1).hasNext
    println(t)
    tx.success()
    tx.close()

  }


}
