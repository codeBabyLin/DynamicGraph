package DynamicGraph.store.Node

import java.io.File
import java.nio.file.OpenOption
import java.util

import DynamicGraph.store.record.DynamicVersionRecord
import DynamicGraph.store.versionStore.DynamicVersionArrayStore
import org.neo4j.io.pagecache.PageCache
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.format.RecordFormats
import org.neo4j.kernel.impl.store.id.{IdGeneratorFactory, IdType}
import org.neo4j.kernel.impl.store.record.{DynamicRecord, NodeRecord, RecordLoad}
import org.neo4j.kernel.impl.store.{CommonAbstractStore, DynamicArrayStore, NoStoreHeader, NoStoreHeaderFormat, NodeLabelsField, NodeStore, PropertyType, RecordStore}
import org.neo4j.kernel.impl.util.Bits
import org.neo4j.logging.LogProvider
import scala.collection.JavaConverters._
object NodeVersionStore{
  def readOwnerFromDynamicLabelsRecord (record: DynamicVersionRecord): Long ={
    val data: Array[Byte] = record.getData
    val header: Array[Byte] = PropertyType.ARRAY.readDynamicRecordHeader(data)
    val array: Array[Byte] = util.Arrays.copyOfRange(data,header.length,data.length)
    val requiredBits: Int = header(2)
    if(requiredBits == 0) -1
    else{
      val bits: Bits = Bits.bitsFromBytes(array)
      bits.getLong(requiredBits)
    }
  }
}

class NodeVersionStore(file: File,
                       idFile: File,
                       config: Config,
                       idGeneratorFactory: IdGeneratorFactory,
                       pageCache: PageCache,
                       logProvider: LogProvider,
                       dynamicVersionLabelStore: DynamicVersionArrayStore,
                       recordFormats: RecordFormats,
                       openOptions: Array[OpenOption])
  extends CommonAbstractStore[NodeRecord,NoStoreHeader](file,idFile,config,IdType.NODE,idGeneratorFactory,pageCache,logProvider,"NodeVersionStore",
    recordFormats.node(),NoStoreHeaderFormat.NO_STORE_HEADER_FORMAT,recordFormats.storeVersion(),openOptions:_*) with NodeStore{
  final val TYPE_DESCRIPTOR: String = "NodeVersionStore"
  //final val dynamicVersionLabelStore: DynamicVersionArrayStore = dynamicVersionLabelStore
  override def accept[FAILURE <: Exception](processor: RecordStore.Processor[FAILURE], record: NodeRecord): Unit = {
    processor.processNode(this,record)
  }

  override def ensureHeavy(node: NodeRecord): Unit = {
    if(NodeLabelsField.fieldPointsToDynamicRecordOfLabels(node.getLabelField)){
      this.ensureHeavy(node,NodeLabelsField.firstDynamicLabelRecordId(node.getLabelField))
    }
  }

  override def ensureHeavy(node: NodeRecord, firstDynamicLabelRecord: Long): Unit ={
    if(node.isLight){
      node.setLabelField(node.getLabelField,this.dynamicVersionLabelStore.getRecords(firstDynamicLabelRecord,RecordLoad.NORMAL))
    }
  }

  override def updateRecord(record: NodeRecord): Unit = {
    super.updateRecord(record)
    this.updateDynamicLabelRecords(record.getDynamicLabelRecords.asScala)

  }
  def getDynamicVersionLabelStore: DynamicVersionArrayStore = {
    this.dynamicVersionLabelStore
  }

  def updateDynamicLabelRecords(dynamicLabelRecords: Iterable[DynamicVersionRecord]): Unit ={
    dynamicLabelRecords.foreach(this.dynamicVersionLabelStore.updateRecord)
  }

}
