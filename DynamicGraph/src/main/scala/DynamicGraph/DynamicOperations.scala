package DynamicGraph

import java.util
import java.util.{Collection, Optional}

import org.neo4j.internal.kernel.api.exceptions.EntityNotFoundException
import org.neo4j.internal.kernel.api.schema.{LabelSchemaDescriptor, RelationTypeSchemaDescriptor, SchemaDescriptor}
import org.neo4j.internal.kernel.api.schema.constraints.ConstraintDescriptor
import org.neo4j.internal.kernel.api.{CursorFactory, ExplicitIndexWrite, IndexReference, SchemaWrite, Write}
import org.neo4j.kernel.api.schema.constraints.IndexBackedConstraintDescriptor
import org.neo4j.kernel.impl.api.KernelTransactionImplementation
import org.neo4j.kernel.impl.api.state.TxState
import org.neo4j.kernel.impl.locking.ResourceTypes
import org.neo4j.kernel.impl.newapi.{AllStoreHolder, DefaultCursors, NodeSchemaMatcher, Operations}
import org.neo4j.storageengine.api.EntityType
import org.neo4j.values.storable.{Value, Values}

class DynamicOperations(opr: Operations) extends Write with ExplicitIndexWrite with SchemaWrite{
  val ktx: KernelTransactionImplementation = opr.getKtx()
  val allStoreHolder: AllStoreHolder = opr.getAllStoreHolder()
  val cursors: CursorFactory  = opr.cursors()
  val nodeCursor = cursors.allocateNodeCursor()
  private def acquireExclusiveNodeLock(node: Long): Unit = {
    if (!this.ktx.hasTxStateWithChanges || !this.ktx.txState.nodeIsAddedInThisTx(node)) this.ktx.statementLocks.optimistic.acquireExclusive(this.ktx.lockTracer, ResourceTypes.NODE, Array[Long](node):_*)
  }

  @throws[EntityNotFoundException]
  private def singleNode(node: Long): Unit = {
    this.allStoreHolder.singleNode(node, this.nodeCursor)
    if (!this.nodeCursor.next) throw new EntityNotFoundException(EntityType.NODE, node)
  }
  def nodeGetVersion(nodeId: Long): Long ={
    1l
  }
  def nodeSetVersion(nodeId:Long, version: Long): Unit ={
    this.acquireExclusiveNodeLock(nodeId)
    this.ktx.assertOpen()
    this.singleNode(nodeId)
    //val labels = this.acquireSharedNodeLabelLocks
    val existingVersion = this.nodeGetVersion(nodeId)

    //var existingPropertyKeyIds = null
    //val hasRelatedSchema = this.indexingService.hasRelatedSchema(labels, propertyKey, EntityType.NODE)
    //if (hasRelatedSchema) existingPropertyKeyIds = this.loadSortedPropertyKeyList

  /*  if (hasRelatedSchema && !(existingValue == value)) {
      val uniquenessConstraints = this.indexingService.getRelatedUniquenessConstraints(labels, propertyKey, EntityType.NODE)
      NodeSchemaMatcher.onMatchingSchema(uniquenessConstraints.iterator, propertyKey, existingPropertyKeyIds, (uniquenessConstraint: IndexBackedConstraintDescriptor) => {
        def foo(uniquenessConstraint: IndexBackedConstraintDescriptor) = this.validateNoExistingNodeWithExactValues(uniquenessConstraint, this.getAllPropertyValues(uniquenessConstraint.schema, propertyKey, value), node)

        foo(uniquenessConstraint)
      })
    }*/

    if (existingVersion == version) return
    else{
      this.ktx.txState().asInstanceOf[TxState].nodeDoChangeVersion(nodeId,version)
    }

/*    if (existingValue eq Values.NO_VALUE) {
      this.autoIndexing.nodes.propertyAdded(this, node, propertyKey, value)
      this.ktx.txState.nodeDoAddProperty(node, propertyKey, value)
      if (hasRelatedSchema) this.updater.onPropertyAdd(this.nodeCursor, this.propertyCursor, labels, propertyKey, existingPropertyKeyIds, value)
      return Values.NO_VALUE
    }
    else {
      this.autoIndexing.nodes.propertyChanged(this, node, propertyKey, existingValue, value)
      if (propertyHasChanged(value, existingValue)) {
        this.ktx.txState.nodeDoChangeProperty(node, propertyKey, value)
        if (hasRelatedSchema) this.updater.onPropertyChange(this.nodeCursor, this.propertyCursor, labels, propertyKey, existingPropertyKeyIds, existingValue, value)
      }
      return existingValue
    }*/
  }
  override def nodeCreate(): Long = opr.nodeCreate()

  override def nodeCreateWithLabels(ints: Array[Int]): Long = opr.nodeCreateWithLabels(ints)

  override def nodeDelete(node: Long): Boolean = opr.nodeDelete(node)

  override def nodeDetachDelete(nodeId: Long): Int = opr.nodeDetachDelete(nodeId)

  override def relationshipCreate(sourceNode: Long, relationshipType: Int, targetNode: Long): Long = opr.relationshipCreate(sourceNode,relationshipType,targetNode)

  override def relationshipDelete(l: Long): Boolean = opr.relationshipDelete(l)

  override def nodeAddLabel(l: Long, i: Int): Boolean = opr.nodeAddLabel(l,i)

  override def nodeRemoveLabel(l: Long, i: Int): Boolean = opr.nodeRemoveLabel(l,i)

  override def nodeSetProperty(l: Long, i: Int, value: Value): Value = opr.nodeSetProperty(l,i,value)


  override def nodeRemoveProperty(l: Long, i: Int): Value = opr.nodeRemoveProperty(l,i)

  override def relationshipSetProperty(l: Long, i: Int, value: Value): Value = opr.relationshipSetProperty(l,i,value)

  override def relationshipRemoveProperty(l: Long, i: Int): Value = opr.relationshipRemoveProperty(l,i)

  override def graphSetProperty(i: Int, value: Value): Value = opr.graphSetProperty(i,value)

  override def graphRemoveProperty(i: Int): Value = opr.graphRemoveProperty(i)

  override def nodeAddToExplicitIndex(s: String, l: Long, s1: String, o: Any): Unit = opr.nodeAddToExplicitIndex(s,l,s1,o)

  override def nodeRemoveFromExplicitIndex(s: String, l: Long, s1: String, o: Any): Unit = opr.nodeRemoveFromExplicitIndex(s,l,s1,o)

  override def nodeRemoveFromExplicitIndex(s: String, l: Long, s1: String): Unit = opr.nodeRemoveFromExplicitIndex(s,l,s1)

  override def nodeRemoveFromExplicitIndex(s: String, l: Long): Unit = opr.nodeRemoveFromExplicitIndex(s,l)

  override def nodeExplicitIndexDrop(s: String): Unit = opr.nodeExplicitIndexDrop(s)

  override def nodeExplicitIndexSetConfiguration(s: String, s1: String, s2: String): String = opr.nodeExplicitIndexSetConfiguration(s,s1,s2)

  override def nodeExplicitIndexRemoveConfiguration(s: String, s1: String): String = opr.nodeExplicitIndexRemoveConfiguration(s,s1)

  override def relationshipAddToExplicitIndex(s: String, l: Long, s1: String, o: Any): Unit = opr.relationshipAddToExplicitIndex(s,l,s1,o)

  override def relationshipRemoveFromExplicitIndex(s: String, l: Long, s1: String, o: Any): Unit = opr.relationshipRemoveFromExplicitIndex(s,l,s1,o)

  override def relationshipRemoveFromExplicitIndex(s: String, l: Long, s1: String): Unit = opr.relationshipRemoveFromExplicitIndex(s,l,s1)

  override def relationshipRemoveFromExplicitIndex(s: String, l: Long): Unit = opr.relationshipRemoveFromExplicitIndex(s,l)

  override def nodeExplicitIndexCreateLazily(s: String, map: util.Map[String, String]): Unit = opr.nodeExplicitIndexCreateLazily(s,map)

  override def nodeExplicitIndexCreate(s: String, map: util.Map[String, String]): Unit = opr.nodeExplicitIndexCreate(s,map)

  override def relationshipExplicitIndexCreateLazily(s: String, map: util.Map[String, String]): Unit = opr.relationshipExplicitIndexCreateLazily(s,map)

  override def relationshipExplicitIndexCreate(s: String, map: util.Map[String, String]): Unit = opr.relationshipExplicitIndexCreate(s,map)

  override def relationshipExplicitIndexDrop(s: String): Unit = opr.relationshipExplicitIndexDrop(s)

  override def relationshipExplicitIndexSetConfiguration(s: String, s1: String, s2: String): String = opr.relationshipExplicitIndexSetConfiguration(s,s1,s2)

  override def relationshipExplicitIndexRemoveConfiguration(s: String, s1: String): String = opr.relationshipExplicitIndexRemoveConfiguration(s,s1)

  override def indexCreate(schemaDescriptor: SchemaDescriptor): IndexReference = opr.indexCreate(schemaDescriptor)

  override def indexCreate(schemaDescriptor: SchemaDescriptor, optional: Optional[String]): IndexReference = opr.indexCreate(schemaDescriptor,optional)

  override def indexCreate(schemaDescriptor: SchemaDescriptor, s: String, optional: Optional[String]): IndexReference = opr.indexCreate(schemaDescriptor,s,optional)

  override def indexDrop(indexReference: IndexReference): Unit = opr.indexDrop(indexReference)

  override def uniquePropertyConstraintCreate(schemaDescriptor: SchemaDescriptor): ConstraintDescriptor = opr.uniquePropertyConstraintCreate(schemaDescriptor)

  override def uniquePropertyConstraintCreate(schemaDescriptor: SchemaDescriptor, s: String): ConstraintDescriptor = opr.uniquePropertyConstraintCreate(schemaDescriptor,s)

  override def nodeKeyConstraintCreate(labelSchemaDescriptor: LabelSchemaDescriptor): ConstraintDescriptor = opr.nodeKeyConstraintCreate(labelSchemaDescriptor)

  override def nodeKeyConstraintCreate(labelSchemaDescriptor: LabelSchemaDescriptor, s: String): ConstraintDescriptor = opr.nodeKeyConstraintCreate(labelSchemaDescriptor,s)

  override def nodePropertyExistenceConstraintCreate(labelSchemaDescriptor: LabelSchemaDescriptor): ConstraintDescriptor = opr.nodePropertyExistenceConstraintCreate(labelSchemaDescriptor)

  override def relationshipPropertyExistenceConstraintCreate(relationTypeSchemaDescriptor: RelationTypeSchemaDescriptor): ConstraintDescriptor = opr.relationshipPropertyExistenceConstraintCreate(relationTypeSchemaDescriptor)

  override def constraintDrop(constraintDescriptor: ConstraintDescriptor): Unit = opr.constraintDrop(constraintDescriptor)
}
