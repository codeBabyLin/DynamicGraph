package cn.DynamicGraph

import cn.DynamicGraph.Common.DGVersion
import cn.DynamicGraph.kernel.impl.store.DbVersionStore
import org.neo4j.graphdb.Transaction
import org.neo4j.kernel.impl.core.{NodeProxy, RelationshipProxy}
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade
import org.neo4j.kernel.impl.store.MetaDataStore
import org.neo4j.kernel.impl.transaction.log.TransactionIdStore

class DynamicGraphDatabaseFacade extends GraphDatabaseFacade{


  private var currentVersion: Long = _

  def getTransactionIdStore(): DbVersionStore ={
    val txIdStore:DbVersionStore = this.getDependencyResolver.resolveDependency(classOf[DbVersionStore])
    //this
    txIdStore
    //txIdStore.getLastCommittedTransactionId
  }

  override def beginTx(version: Long): Transaction = {
    //val transaction = super.beginTx()
    super.beginTx(version)

  }

  override def beginTx(): Transaction = {
    val transaction = super.beginTx()
    //transaction.setVersion(DGVersion.setStartEndVersion(this.getNextVersion,65535))
    transaction.setVersion(DGVersion.setStartVersion(this.getNextVersion))
    //val func = (e:java.lang.Long,l:java.lang.Boolean) => this.commitVersion(e,l)
    //transaction.setFunction(func)
    transaction.setVersionstore(this.getTransactionIdStore())
    transaction
  }

  override def listAllVersions(): Array[Long] = {
    getTransactionIdStore().listAllVersionsSuccessCommited()
  }


  def commitVersion(version: Long): Boolean = {
    getTransactionIdStore().transactionCommit(version)
  }
  override def getNextVersion: Long = {
    getTransactionIdStore().getNextVersion
  }

  override def getCurrentVersion: Long = {
   getTransactionIdStore().getHighestPossibleIdInUse
  }

  override def seekVersion(version: Long): Unit = {
    this.currentVersion  = version
  }
  //println("ahahah")
  def print_test(): Unit ={
    //print("hello  world")
  }

  override def newRelationshipProxy(id: Long): RelationshipProxy = new RelationshipProxyEx(this,id)

  override def newRelationshipProxy(id: Long, startNodeId: Long, typeId: Int, endNodeId: Long): RelationshipProxy = new RelationshipProxyEx(this,id, startNodeId, typeId, endNodeId)

  override def newNodeProxy(nodeId: Long): DynamicNodeProxyM2 = {
    new DynamicNodeProxyM2(this,nodeId)
  }

  override def commitVersion(version: Long, isInuse: Boolean): Boolean = {
    this.getTransactionIdStore().transactionCommit(version,isInuse)
  }
}
