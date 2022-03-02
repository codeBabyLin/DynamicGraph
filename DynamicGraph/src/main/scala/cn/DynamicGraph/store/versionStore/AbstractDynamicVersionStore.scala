package cn.DynamicGraph.store.versionStore

import java.io.File
import java.nio.ByteBuffer
import java.nio.file.OpenOption
import java.util

//import DynamicGraph.store.record.StandardDynamicVersionRecordAllocator
import cn.DynamicGraph.store.record.{DynamicVersionRecord, DynamicVersionRecordAllocator, DynamicVersionStoreHeaderFormat, StandardDynamicVersionRecordAllocator}
import org.neo4j.helpers.collection.Iterables
import org.neo4j.io.pagecache.PageCache
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.format.RecordFormat
import org.neo4j.kernel.impl.store.id.{IdGeneratorFactory, IdType}
import org.neo4j.kernel.impl.store.record.DynamicRecord
import org.neo4j.kernel.impl.store.{CommonAbstractStore, DynamicRecordAllocator, IntStoreHeader, PropertyType, RecordStore}
import org.neo4j.logging.LogProvider

import scala.collection.JavaConverters._
object AbstractDynamicVersionStore{
  def allocateRecordsFromBytes(recordList: util.Collection[DynamicVersionRecord],src: Array[Byte],dynamicVersionRecordAllocator: DynamicVersionRecordAllocator): Unit ={
    assert(src != null, s"Null src argument")
    var nextRecord: DynamicVersionRecord = dynamicVersionRecordAllocator.nextRecord
    var srcOffset: Int = 0
    val dataSize = dynamicVersionRecordAllocator.getRecordDataSize
    do {
      val record = nextRecord
      nextRecord.setStartRecord(srcOffset == 0)
      var data: Array[Byte] = new Array[Byte](0)
      if(src.length - srcOffset > dataSize){
        data = new Array[Byte](dataSize)
        System.arraycopy(src,srcOffset,data,0,dataSize)
        nextRecord.setData(data)
        nextRecord = dynamicVersionRecordAllocator.nextRecord
        record.setNextBlock(nextRecord.getId)
        srcOffset += dataSize
      }
      else{
        data = new Array[Byte](src.length - srcOffset)
        System.arraycopy(src,srcOffset,data,0,data.length)
        nextRecord.setData(data)
        nextRecord = null
        record.setNextBlock(-1)
      }
      recordList.add(record)
      assert( record.getData != null)
    }while(nextRecord != null)
  }

  def concatData(records: util.Collection[DynamicVersionRecord],target: Array[Byte]): ByteBuffer ={
    var totalLength: Int = 0
    var newTarget: Array[Byte] = new Array[Byte](target.length)
    System.arraycopy(target,0,newTarget,0,target.length)
    val record: DynamicVersionRecord = null
    records.asScala.map(x => totalLength += x.getLength)
    if(target.length < totalLength) newTarget = new Array[Byte](totalLength)
    val buffer: ByteBuffer = ByteBuffer.wrap(newTarget,0,totalLength)
    records.asScala.map(x => buffer.put(x.getData))
    buffer.position(0)
    buffer
  }

  def readFullByteArrayFromHeavyRecords(records: Iterable[DynamicVersionRecord], propertyType: PropertyType): (Array[Byte],Array[Byte]) = {
    var header: Array[Byte] = null
    val byteList: util.List[Array[Byte]] = new util.ArrayList[Array[Byte]]()
    var totalSize: Int = 0
    //val i: Int = 0
    val record: DynamicVersionRecord = records.head

    header = propertyType.readDynamicRecordHeader(record.getData)
    var offset = header.length
    records.map(x => {
      totalSize += x.getData.length - offset
      byteList.add(x.getData)
    })
    val bArray: Array[Byte] = new Array[Byte](totalSize)
    assert (header != null, s"header should be non-null since records should not be empty: ${Iterables.toString(records.asJava,",")}")
    val sourceOffset = header.length
    offset = 0
    byteList.asScala.foreach(x =>{
      System.arraycopy(x,sourceOffset,bArray,offset,x.length-sourceOffset)
      offset += x.length -sourceOffset
    })
    (header,bArray)
  }

}


abstract class AbstractDynamicVersionStore(file: File,
                                           idFile: File,
                                           conf: Config,
                                           idType: IdType,
                                           idGeneratorFactory: IdGeneratorFactory,
                                           pageCache: PageCache,
                                           logProvider: LogProvider,
                                           typeDescriptor: String,
                                           dataSizeFromConfiguration: Int,
                                           recordFormat: RecordFormat[DynamicVersionRecord], storeVersion: String, openOptions: OpenOption*) extends
  CommonAbstractStore[DynamicVersionRecord,IntStoreHeader](file,idFile,conf,idType,idGeneratorFactory,pageCache,
    logProvider,typeDescriptor,recordFormat, new DynamicVersionStoreHeaderFormat(dataSizeFromConfiguration, recordFormat),storeVersion,openOptions:_*) with DynamicVersionRecordAllocator{


  override def nextRecord: DynamicVersionRecord = StandardDynamicVersionRecordAllocator.allocateRecord(this.nextId())
  def allocateRecordsFromBytes(target: util.Collection[DynamicVersionRecord],src: Array[Byte]): Unit ={
    AbstractDynamicVersionStore.allocateRecordsFromBytes(target,src,this)
  }

  override def toString: String = s"${super.toString}[filename:${this.file.getName},blockSize:${this.getRecordDataSize}]"
  def readFullByteArray(records: Iterable[DynamicVersionRecord], propertyType: PropertyType): (Array[Byte],Array[Byte]) ={
    records.map(this.ensureHeavy)
    AbstractDynamicVersionStore.readFullByteArrayFromHeavyRecords(records,propertyType)
  }

  override def accept[FAILURE <: Exception](processor: RecordStore.Processor[FAILURE], record: DynamicVersionRecord)
}
