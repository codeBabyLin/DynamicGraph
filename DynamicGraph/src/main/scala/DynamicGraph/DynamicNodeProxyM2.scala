package DynamicGraph

import java.util
//import java.util.Map

import Common.Version
import DynamicGraph.serialization.Value2Byte
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

  def setVersionProperty(key: String,value: Any, version: String): Unit ={
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

  def getVersionProterty(key: String,version: String): Any ={
    val temp = this.getProperty(key)
    val value = temp.asInstanceOf[Array[Byte]]
    val pmap = Value2Byte.read(value)
    pmap(version.toInt)
    //Value2Byte.read(this.getProperty(key).asInstanceOf[Array[Byte]])(version.toInt)
  }

  def setNodeVersion(version: Long): Unit ={
    //val transaction: KernelTransactionImplementation = this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation]
    val transaction:DynamicKernelTransactionImplementation = new DynamicKernelTransactionImplementation(this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation])
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
      try transaction.DynamicDataWrite().nodeSetVersion(nodeId,version)
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


  def getNodeVersion(): Long = {
    val transaction = this.safeAcquireTransaction
    val nodes: NodeCursor = transaction.ambientNodeCursor()
    this.singleNode(transaction,nodes)
    nodes.nodeVersion()

  }

}
