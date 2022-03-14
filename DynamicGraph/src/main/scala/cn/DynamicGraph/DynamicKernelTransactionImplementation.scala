//package cn.DynamicGraph
//
//import java.util
//import java.util.Optional
//
//import org.neo4j.internal.kernel.api.schema.SchemaDescriptor
//import org.neo4j.internal.kernel.api.{CursorFactory, ExecutionStatistics, ExplicitIndexRead, ExplicitIndexWrite, Locks, NodeCursor, Procedures, PropertyCursor, Read, RelationshipScanCursor, SchemaRead, SchemaWrite, Token, TokenRead, TokenWrite, Transaction, Write}
//import org.neo4j.internal.kernel.api.security.{AuthSubject, SecurityContext}
//import org.neo4j.kernel.api.{KernelTransaction, Statement}
//import org.neo4j.kernel.api.exceptions.Status
//import org.neo4j.kernel.api.txstate.auxiliary.AuxiliaryTransactionState
//import org.neo4j.kernel.api.txstate.{ExplicitIndexTransactionState, TransactionState, TxStateHolder}
//import org.neo4j.kernel.impl.api.{ClockContext, KernelTransactionImplementation}
//import org.neo4j.kernel.impl.newapi.Operations
//import org.neo4j.storageengine.api.schema.IndexDescriptor
//import scala.collection.JavaConversions._
//import scala.collection.JavaConverters._
//class DynamicKernelTransactionImplementation(ktl: KernelTransactionImplementation) extends KernelTransaction with TxStateHolder with ExecutionStatistics{
//
//
//  var startVersion: Long = 0
//  var endVersion: Long = 0
//
//  override def acquireStatement(): Statement = ktl.acquireStatement()
//
//  override def indexUniqueCreate(schemaDescriptor: SchemaDescriptor, s: String): IndexDescriptor = ktl.indexUniqueCreate(schemaDescriptor,s)
//
//  override def securityContext(): SecurityContext = ktl.securityContext()
//
//  override def subjectOrAnonymous(): AuthSubject = ktl.subjectOrAnonymous()
//
//  override def lastTransactionTimestampWhenStarted(): Long = ktl.lastTransactionTimestampWhenStarted()
//
//  override def lastTransactionIdWhenStarted(): Long = ktl.lastTransactionIdWhenStarted()
//
//  override def startTime(): Long = ktl.startTime()
//
//  override def timeout(): Long = ktl.timeout()
//
//  override def registerCloseListener(closeListener: KernelTransaction.CloseListener): Unit = ktl.registerCloseListener(closeListener)
//
//  override def transactionType(): Transaction.Type = ktl.transactionType()
//
//  override def getTransactionId: Long = ktl.getTransactionId
//
//  override def getCommitTime: Long = ktl.getCommitTime
//
//  override def overrideWith(securityContext: SecurityContext): KernelTransaction.Revertable = ktl.overrideWith(securityContext)
//
//  override def clocks(): ClockContext = ktl.clocks()
//
//  override def ambientNodeCursor(): NodeCursor = ktl.ambientNodeCursor()
//
//  override def ambientRelationshipCursor(): RelationshipScanCursor = ktl.ambientRelationshipCursor()
//
//  override def ambientPropertyCursor(): PropertyCursor = ktl.ambientPropertyCursor()
//
//  override def setMetaData(data: java.util.Map[String, AnyRef]): Unit = ktl.setMetaData(data)
//
//  override def getMetaData: util.Map[String, AnyRef] = ktl.getMetaData
//
//  override def isSchemaTransaction: Boolean = ktl.isSchemaTransaction
//
//  override def txState(): TransactionState = ktl.txState()
//
//  override def auxiliaryTxState(o: Any): AuxiliaryTransactionState = ktl.auxiliaryTxState()
//
//  override def explicitIndexTxState(): ExplicitIndexTransactionState = ktl.explicitIndexTxState()
//
//  override def hasTxStateWithChanges: Boolean = ktl.hasTxStateWithChanges
//
//  override def pageHits(): Long = ktl.pageHits()
//
//  override def pageFaults(): Long = ktl.pageFaults()
//
//  override def assertOpen(): Unit = ktl.assertOpen()
//
//  override def success(): Unit = ktl.success()
//
//  override def failure(): Unit = ktl.failure()
//
//  override def dataRead(): Read = ktl.dataRead()
//
//  override def dataWrite(): Write = ktl.dataWrite()
//
//  def DynamicDataWrite(): DynamicOperations = {
//    new DynamicOperations(ktl.dataWrite().asInstanceOf[Operations])
//  }
//
//
//  override def indexRead(): ExplicitIndexRead = ktl.indexRead()
//
//  override def indexWrite(): ExplicitIndexWrite = ktl.indexWrite()
//
//  override def tokenRead(): TokenRead = ktl.tokenRead()
//
//  override def tokenWrite(): TokenWrite = ktl.tokenWrite()
//
//  override def token(): Token = ktl.token()
//
//  override def schemaRead(): SchemaRead = ktl.schemaRead()
//
//  override def schemaWrite(): SchemaWrite = ktl.schemaWrite()
//
//  override def locks(): Locks = ktl.locks()
//
//  override def cursors(): CursorFactory = ktl.cursors()
//
//  override def procedures(): Procedures = ktl.procedures()
//
//  override def executionStatistics(): ExecutionStatistics = ktl.executionStatistics()
//
//  override def closeTransaction(): Long = {
//    println("I'm closed")
//    ktl.closeTransaction()
//  }
//
//  override def isOpen: Boolean = ktl.isOpen
//
//  override def getReasonIfTerminated: Optional[Status] = ktl.getReasonIfTerminated
//
//  override def isTerminated: Boolean = ktl.isTerminated
//
//  override def markForTermination(status: Status): Unit = ktl.markForTermination(status)
//
//  //Dynamicgraph
//  //**********************************
//  //Dynamicgraph Method
//  //*********************************************************
//  def setVersion(startVersion: Long): Unit = {
//    this.startVersion = startVersion
//    //this.endVersion = endVersion
//  }
//
//}
