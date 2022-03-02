package cn.DynamicGraph

import org.neo4j.kernel.impl.core.{NodeProxy, RelationshipProxy}
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade

class DynamicGraphDatabaseFacade extends GraphDatabaseFacade{


  //println("ahahah")
  def print_test(): Unit ={
    //print("hello  world")
  }

  override def newRelationshipProxy(id: Long): RelationshipProxy = new RelationshipProxyEx(this,id)

  override def newRelationshipProxy(id: Long, startNodeId: Long, typeId: Int, endNodeId: Long): RelationshipProxy = new RelationshipProxyEx(this,id, startNodeId, typeId, endNodeId)

  override def newNodeProxy(nodeId: Long): DynamicNodeProxyM2 = {
    new DynamicNodeProxyM2(this,nodeId)
  }


}
