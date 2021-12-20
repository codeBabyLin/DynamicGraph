package DynamicGraph

import java.util

import org.neo4j.graphdb.{ConstraintViolationException, NotFoundException, TransactionTerminatedException}
import org.neo4j.internal.kernel.api.{NodeCursor, PropertyCursor, TokenRead}
import org.neo4j.internal.kernel.api.exceptions.explicitindex.AutoIndexingKernelException
import org.neo4j.internal.kernel.api.exceptions.{EntityNotFoundException, InvalidTransactionTypeKernelException, KernelException, PropertyKeyIdNotFoundKernelException}
import org.neo4j.internal.kernel.api.exceptions.schema.{ConstraintValidationException, IllegalTokenNameException}
import org.neo4j.kernel.api.exceptions.Status
import org.neo4j.kernel.api.exceptions.Status.Transaction
import org.neo4j.kernel.api.{KernelTransaction, SilentTokenNameLookup, Statement}
import org.neo4j.kernel.impl.api.KernelTransactionImplementation
import org.neo4j.kernel.impl.core.{EmbeddedProxySPI, NodeProxy}
import org.neo4j.storageengine.api.EntityType
import org.neo4j.values.storable.Values

class DynamicNodeProxyM1(spi:EmbeddedProxySPI, nodeId: Long) extends NodeProxy(spi,nodeId){
  private def singleNode(transaction: KernelTransaction, nodes: NodeCursor): Unit = {
    transaction.dataRead.singleNode(this.nodeId, nodes)
    if (!nodes.next) throw new NotFoundException(new EntityNotFoundException(EntityType.NODE, this.nodeId))
  }
  private def safeAcquireTransaction = {
    val transaction = this.spi.kernelTransaction
    if (transaction.isTerminated) {
      val terminationReason = transaction.getReasonIfTerminated.orElse(Transaction.Terminated).asInstanceOf[Status]
      throw new TransactionTerminatedException(terminationReason)
    }
    else transaction
  }

  def setVersionProperty(key: String,value: Any, version: String): Unit ={
    setProperty(version.concat(key),value)
  }

  def getVersionProterty(key: String,version: String): AnyRef ={
    getProperty(version.concat(key))
  }

  def getVersionAllProperties(version: String): util.Map[String, AnyRef] = {
    val transaction = this.safeAcquireTransaction
    val properties = new util.HashMap[String, AnyRef]

    try {
      val nodes = transaction.ambientNodeCursor
      val propertyCursor = transaction.ambientPropertyCursor
      val token = transaction.tokenRead
      this.singleNode(transaction, nodes)
      nodes.properties(propertyCursor)
      val index = version.length
      while (propertyCursor.next) {
        val keyname = token.propertyKeyName(propertyCursor.propertyKey)
        if(keyname.startsWith(version)) properties.put(keyname.substring(index), propertyCursor.propertyValue.asObjectCopy)
      }
      properties
    } catch {
      case var6: PropertyKeyIdNotFoundKernelException =>
        throw new IllegalStateException("Property key retrieved through kernel API should exist.", var6)
    }
  }

  

  override def removeProperty(key: String): AnyRef = super.removeProperty(key)
  override def getAllProperties: util.Map[String, AnyRef] = super.getAllProperties
  override def getProperty(key: String): AnyRef = super.getProperty(key)

  override def setProperty(key: String, value: Any): Unit = {
    val transaction: KernelTransaction = this.spi.kernelTransaction
    //val transaction = new DynamicKernelTransactionImplementation(this.spi.kernelTransaction.asInstanceOf[KernelTransactionImplementation])
    var propertyKeyId = 0
    try propertyKeyId = transaction.tokenWrite().propertyKeyGetOrCreateForName(key)
    catch {
      case var23: IllegalTokenNameException =>
        throw new IllegalArgumentException(String.format("Invalid property key '%s'.", key), var23)
    }

    try {
      val ignore = transaction.acquireStatement()
      var var6: Throwable = null
      try transaction.dataWrite().nodeSetProperty(this.nodeId, propertyKeyId, Values.of(value, false))
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

}
