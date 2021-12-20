package DynamicGraph.state

import java.util

import org.eclipse.collections.api.IntIterable
import org.eclipse.collections.api.set.primitive.LongSet
import org.neo4j.internal.kernel.api.schema.constraints.ConstraintDescriptor
import org.neo4j.kernel.api.txstate.TransactionCountingStateVisitor
import org.neo4j.storageengine.api.StorageProperty
import org.neo4j.storageengine.api.schema.IndexDescriptor
import org.neo4j.storageengine.api.txstate.DynamicTxStateVisitor


/*trait DynamicTxStateVisitor extends TxStateVisitor{
  def visitNodeVersionChanges()
}*/


class DynamicTxStateVisitorImpl(dynamicRecordState: DynamicRecordState, tcs: TransactionCountingStateVisitor) extends DynamicTxStateVisitor{
  override def visitNodeVersionChanges(nodeId: Long, version: Long): Unit = {

  }

  override def visitCreatedNode(l: Long): Unit = tcs.visitCreatedNode(l)

  override def visitDeletedNode(l: Long): Unit = tcs.visitDeletedNode(l)

  override def visitCreatedRelationship(l: Long, i: Int, l1: Long, l2: Long): Unit = tcs.visitCreatedRelationship(l,i,l1,l2)

  override def visitDeletedRelationship(l: Long): Unit = tcs.visitDeletedRelationship(l)

  override def visitNodePropertyChanges(l: Long, iterator: util.Iterator[StorageProperty], iterator1: util.Iterator[StorageProperty], intIterable: IntIterable): Unit = tcs.visitNodePropertyChanges(l,iterator,iterator1,intIterable)

  override def visitRelPropertyChanges(l: Long, iterator: util.Iterator[StorageProperty], iterator1: util.Iterator[StorageProperty], intIterable: IntIterable): Unit = tcs.visitRelPropertyChanges(l,iterator,iterator1,intIterable)

  override def visitGraphPropertyChanges(iterator: util.Iterator[StorageProperty], iterator1: util.Iterator[StorageProperty], intIterable: IntIterable): Unit = tcs.visitGraphPropertyChanges(iterator,iterator1,intIterable)

  override def visitNodeLabelChanges(l: Long, longSet: LongSet, longSet1: LongSet): Unit = tcs.visitNodeLabelChanges(l,longSet,longSet1)

  override def visitAddedIndex(indexDescriptor: IndexDescriptor): Unit = tcs.visitAddedIndex(indexDescriptor)

  override def visitRemovedIndex(indexDescriptor: IndexDescriptor): Unit = tcs.visitRemovedIndex(indexDescriptor)

  override def visitAddedConstraint(constraintDescriptor: ConstraintDescriptor): Unit = tcs.visitAddedConstraint(constraintDescriptor)

  override def visitRemovedConstraint(constraintDescriptor: ConstraintDescriptor): Unit = tcs.visitRemovedConstraint(constraintDescriptor)

  override def visitCreatedLabelToken(l: Long, s: String): Unit = tcs.visitCreatedLabelToken(l,s)

  override def visitCreatedPropertyKeyToken(l: Long, s: String): Unit = tcs.visitCreatedPropertyKeyToken(l,s)

  override def visitCreatedRelationshipTypeToken(l: Long, s: String): Unit = tcs.visitCreatedRelationshipTypeToken(l,s)

  override def close(): Unit = tcs.close()

  override def visitNodeVersionChange(var1: Long, var2: Long): Unit = ???
}
