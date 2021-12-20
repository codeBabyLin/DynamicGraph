package DynamicGraph.state

import java.util

import org.neo4j.kernel.impl.storageengine.impl.recordstorage.{RecordState, TransactionRecordState}
import org.neo4j.storageengine.api.StorageCommand

class DynamicRecordState(trs:TransactionRecordState ) extends RecordState{

  def nodeVersionChanged(nodeId: Long, version: Long): Unit ={

  }

  /*  void nodeAddProperty(long nodeId, int propertyKey, Value value) {
    RecordProxy<NodeRecord, Void> node = this.recordChangeSet.getNodeRecords().getOrLoad(nodeId, (Object)null);
    this.propertyCreator.primitiveSetProperty(node, propertyKey, value, this.recordChangeSet.getPropertyRecords());
  }*/
  override def hasChanges: Boolean = ???

  override def extractCommands(collection: util.Collection[StorageCommand]): Unit = ???
}
