package cn.DynamicGraph

import java.{lang, util}
import java.util.ArrayList

import cn.DynamicGraph.serialization.Value2Byte
import org.neo4j.graphdb.Label
import org.neo4j.internal.kernel.api.LabelSet
import org.neo4j.internal.kernel.api.exceptions.LabelNotFoundKernelException
import org.neo4j.internal.kernel.api.exceptions.schema.TooManyLabelsException
import org.neo4j.kernel.api.Statement
import org.neo4j.kernel.impl.newapi.Labels

import scala.collection.mutable
//import java.util.Map

import org.neo4j.graphdb.{ConstraintViolationException, NotFoundException, TransactionTerminatedException}
import org.neo4j.internal.kernel.api.{NodeCursor, PropertyCursor, Read, TokenRead}
import org.neo4j.internal.kernel.api.exceptions.explicitindex.AutoIndexingKernelException
import org.neo4j.internal.kernel.api.exceptions.schema.{ConstraintValidationException, IllegalTokenNameException}
import org.neo4j.internal.kernel.api.exceptions.{EntityNotFoundException, InvalidTransactionTypeKernelException, KernelException, PropertyKeyIdNotFoundKernelException}
import org.neo4j.kernel.api.{KernelTransaction, SilentTokenNameLookup}
import org.neo4j.kernel.api.exceptions.Status
import org.neo4j.kernel.api.exceptions.Status.Transaction
import org.neo4j.kernel.impl.api.KernelTransactionImplementation
import org.neo4j.kernel.impl.core.{EmbeddedProxySPI, NodeProxy}
import org.neo4j.storageengine.api.EntityType
import org.neo4j.values.storable.Values
import org.neo4j.kernel.impl.newapi.DefaultNodeCursor
import collection.JavaConverters._
class DynamicNodeProxyM2 (spi:EmbeddedProxySPI, nodeId: Long) extends NodeProxy(spi,nodeId){

  private def singleNode(transaction: KernelTransaction, nodes: NodeCursor): Unit = {
    transaction.dataRead.singleNode(this.nodeId, nodes)
    if (!nodes.next) throw new NotFoundException(new EntityNotFoundException(EntityType.NODE, this.nodeId))
  }
  private def safeAcquireTransaction: KernelTransaction = {
    val transaction = this.spi.kernelTransaction
    if (transaction.isTerminated) {
      val terminationReason = transaction.getReasonIfTerminated.orElse(Transaction.Terminated).asInstanceOf[Status]
      throw new TransactionTerminatedException(terminationReason)
    }
    else transaction
  }

  def setVersionProperty(key: String,value: Any, version: Long): Unit ={
    var value1: Any = null
    //var flag = true
    try{
      value1 = this.getProperty(key)
    }
    catch {
      case e: NotFoundException =>
    }
    finally {
      var prop: Map[Int, Any] = Map.empty
      if (value1!=null) prop = Value2Byte.read(value1.asInstanceOf[Array[Byte]])
      prop += version.toInt -> value
      this.setProperty(key, Value2Byte.write(prop))
    }
  }

  def getVersionProterty(key: String,version: Long): Any ={
    val temp = this.getProperty(key)
    val value = temp.asInstanceOf[Array[Byte]]
    val pmap = Value2Byte.read(value)
    pmap(version.toInt)
    //Value2Byte.read(this.getProperty(key).asInstanceOf[Array[Byte]])(version.toInt)
  }

  def setNodeVersion(version: Long): Unit ={
    val transaction = this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation]

    //println(transaction.getLastTransactionIdWhenStarted)

    //val transaction:DynamicKernelTransactionImplementation = new DynamicKernelTransactionImplementation(this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation])
    //var propertyKeyId = 0
    //try propertyKeyId = transaction.tokenWrite().propertyKeyGetOrCreateForName(key)
    //catch {
    //  case var23: IllegalTokenNameException =>
    //    throw new IllegalArgumentException(String.format("Invalid property key '%s'.", key), var23)
    //}
    //transaction.DynamicDataWrite().

    try {
      val ignore = transaction.acquireStatement()
      var var6: Throwable = null
      try transaction.getOperations.nodeSetVersion(nodeId,version)
      catch {
        case var22: Throwable =>
          var6 = var22
          throw var22
      } finally if (ignore != null) if (var6 != null) try ignore.close()
      catch {
        case var21: Throwable =>
          var6.addSuppressed(var21)
      }
      else ignore.close()
    } catch {
      case var25: ConstraintValidationException =>
        throw new ConstraintViolationException(var25.getUserMessage(new SilentTokenNameLookup(transaction.tokenRead())), var25)
      case var26: IllegalArgumentException =>
        this.spi.failTransaction()
        throw var26
      case var27: EntityNotFoundException =>
        throw new NotFoundException(var27)
      case var28: InvalidTransactionTypeKernelException =>
        throw new ConstraintViolationException(var28.getMessage, var28)
      case var29: AutoIndexingKernelException =>
        throw new IllegalStateException("Auto indexing encountered a failure while setting property: " + var29.getMessage, var29)
      case var30: KernelException =>
        throw new ConstraintViolationException(var30.getMessage, var30)
    }
  }


  override def getNodeVersion(): Long = {
    val transaction = this.safeAcquireTransaction.asInstanceOf[KernelTransactionImplementation]

    //println(transaction.getLastTransactionIdWhenStarted)
    transaction.getOperations.nodeGetVersion(this.nodeId)

    //val nodes: NodeCursor = transaction.ambientNodeCursor()
    //this.singleNode(transaction,nodes)
    //nodes.nodeVersion()

  }

  def setVersionLabel(label: Label, version: Long): Unit ={
    val transaction = this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation]

    try {
      val ignore = transaction.acquireStatement
      var var4:Throwable = null
      try transaction.getOperations.nodeAddLabel(this.getId, transaction.tokenWrite.labelGetOrCreateForName(label.name),version)
      catch {
        case var18: Throwable =>
          var4 = var18
          throw var18
      } finally if (ignore != null) if (var4 != null) try ignore.close()
      catch {
        case var17: Throwable =>
          var4.addSuppressed(var17)
      }
      else ignore.close()
    } catch {
      case var20: ConstraintValidationException =>
        throw new ConstraintViolationException(var20.getUserMessage(new SilentTokenNameLookup(transaction.tokenRead)), var20)
      case var21: IllegalTokenNameException =>
        throw new ConstraintViolationException(String.format("Invalid label name '%s'.", label.name), var21)
      case var22: TooManyLabelsException =>
        throw new ConstraintViolationException("Unable to add label.", var22)
      case var23: EntityNotFoundException =>
        throw new NotFoundException("No node with id " + this.getId + " found.", var23)
      case var24: KernelException =>
        throw new ConstraintViolationException(var24.getMessage, var24)
    }
  }

  /*def getVersionLabels(version: Long): LabelSet = {
    Labels.from(this.getVersionLabels().filter(x => x._2 == version).keys.asJava)
  }*/
  def getVersionLabels():Map[Label,Long] = {
    val transaction = this.safeAcquireTransaction
    val nodes = transaction.ambientNodeCursor

    try {
      val ignore = this.spi.statement
      var var4 : Throwable = null
      try {
        this.singleNode(transaction, nodes)
        val labelSet = nodes.versionLabels()
        val tokenRead = transaction.tokenRead
        labelSet.asScala.map(x =>(Label.label(tokenRead.nodeLabelName(x._1.toInt)),x._2.toLong)).toMap

      } catch {
        case var18: Throwable =>
          //var4 = var18
          throw var18
      } finally if (ignore != null) if (var4 != null ) try ignore.close()
      catch {
        case var17: Throwable =>
          var4.addSuppressed(var17)
      }
      else ignore.close()
    } catch {
      case var20: LabelNotFoundKernelException =>
        throw new IllegalStateException("Label retrieved through kernel API should exist.", var20)
    }
  }

  def getVersionLabels(version:Long): Array[Label] ={
    val transaction = this.safeAcquireTransaction
    val nodes = transaction.ambientNodeCursor

    try {
      val ignore = this.spi.statement
      var var4 : Throwable = null
      try {
        this.singleNode(transaction, nodes)
        val labelSet = nodes.versionLabels()
        val tokenRead = transaction.tokenRead
        labelSet.asScala.filter(x => x._2 == version).keySet.map(x =>Label.label(tokenRead.nodeLabelName(x.toInt))).toArray

      } catch {
        case var18: Throwable =>
          //var4 = var18
          throw var18
      } finally if (ignore != null) if (var4 != null ) try ignore.close()
      catch {
        case var17: Throwable =>
          var4.addSuppressed(var17)
      }
      else ignore.close()
    } catch {
      case var20: LabelNotFoundKernelException =>
        throw new IllegalStateException("Label retrieved through kernel API should exist.", var20)
    }
  }

 // override def addLabel(label: Label): Unit = this.setVersionLabel(label,this.safeAcquireTransaction.asInstanceOf[KernelTransactionImplementation].getVersion)

  //override def setProperty(key: String, value: Any): Unit = this.setVersionProperty(key,value,this.safeAcquireTransaction.asInstanceOf[KernelTransactionImplementation].getVersion)

}
