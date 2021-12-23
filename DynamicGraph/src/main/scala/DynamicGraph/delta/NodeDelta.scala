package DynamicGraph.delta

import org.neo4j.kernel.impl.store.record.NodeRecord

class NodeDelta(nodeId: Long) extends NodeRecord(nodeId){
  val removedProperty: Map[Long,Any] = Map.empty
  val addedProperty: Map[Long,Any] = Map.empty
  val updatedProperty:Map[Long,Any] = Map.empty

  val removedLabels: Array[String] = Array.empty
  val addedLabels: Array[String] = Array.empty

}
