package cn.DynamicGraph.store.versionStore

import java.io.File
import java.nio.ByteBuffer
import java.nio.file.OpenOption
import java.time.{LocalDate, LocalDateTime, LocalTime, OffsetTime, ZonedDateTime}
import java.util
import java.util.Collection

//import DynamicGraph.store.record.DynamicVersionRecordFormat
import cn.DynamicGraph.store.record.{DynamicVersionRecord, DynamicVersionRecordAllocator, DynamicVersionRecordFormat}
import org.neo4j.helpers.collection.Pair
import org.neo4j.io.pagecache.PageCache
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.GeometryType.GeometryHeader
import org.neo4j.kernel.impl.store.TemporalType.TemporalHeader
import org.neo4j.kernel.impl.store.{DynamicArrayStore, DynamicRecordAllocator, GeometryType, PropertyStore, PropertyType, RecordStore, ShortArray, TemporalType}
import org.neo4j.kernel.impl.store.format.{Capability, RecordFormats, UnsupportedFormatCapabilityException}
import org.neo4j.kernel.impl.store.id.{IdGeneratorFactory, IdType}
import org.neo4j.kernel.impl.store.record.DynamicRecord
import org.neo4j.kernel.impl.util.Bits
import org.neo4j.logging.LogProvider
import org.neo4j.values.storable.{ArrayValue, CoordinateReferenceSystem, DurationValue, PointValue, Value, Values}



object DynamicVersionArrayStore{
  def encodeFromNumbers(array: Any, offsetBytes: Int): Array[Byte] = {
    val types = ShortArray.typeOf(array)
    if (types == null) throw new IllegalArgumentException(array + " not a valid array type.")
    else {
      if ((types ne ShortArray.DOUBLE) && (types ne ShortArray.FLOAT)) createBitCompactedArray(types, array, offsetBytes)
      else createUncompactedArray(types, array, offsetBytes)
    }
  }
  private def createBitCompactedArray(types: ShortArray, array: Any, offsetBytes: Int): Array[Byte] = {
    val isPrimitiveByteArray: Boolean = {
      array match{
        case x:Array[Byte] => true
        case _ => false
      }
    }
    val isByteArray: Boolean = {
      array match{
        case x:Array[Byte] => true
        case _ => false
      }
    }
    val arrayLength: Int = array.asInstanceOf[Array[_]].length

    val requiredBits = if (isByteArray) 8
    else types.calculateRequiredBitsForArray(array, arrayLength)
    val totalBits = requiredBits * arrayLength
    var bitsUsedInLastByte = totalBits % 8
    bitsUsedInLastByte = if (bitsUsedInLastByte == 0) 8
    else bitsUsedInLastByte
    if (isByteArray) createBitCompactedByteArray(types, isPrimitiveByteArray, array, bitsUsedInLastByte, requiredBits, offsetBytes)
    else {
      var numberOfBytes = (totalBits - 1) / 8 + 1
      numberOfBytes += 3
      val bits = Bits.bits(numberOfBytes)
      bits.put(types.intValue.toByte)
      bits.put(bitsUsedInLastByte.toByte)
      bits.put(requiredBits.toByte)
      types.writeAll(array, arrayLength, requiredBits, bits)
      bits.asBytes(offsetBytes)
    }

  }
  private def createBitCompactedByteArray(types: ShortArray, isPrimitiveByteArray: Boolean,
                                          array: Any, bitsUsedInLastByte: Int,
                                          requiredBits: Int, offsetBytes: Int): Array[Byte] = {
    val arrayLength = array.asInstanceOf[Array[_]].length
    val bytes = new Array[Byte](3 + arrayLength + offsetBytes)
    bytes(offsetBytes + 0) = types.intValue.toByte
    bytes(offsetBytes + 1) = bitsUsedInLastByte.toByte
    bytes(offsetBytes + 2) = requiredBits.toByte
    if (isPrimitiveByteArray) System.arraycopy(array, 0, bytes, 3 + offsetBytes, arrayLength)
    else {
      val source: Array[Byte] = array.asInstanceOf[Array[Byte]]
      source.foreach(x =>{
        bytes.update(3+offsetBytes+source.indexOf(x),x)
      })
    }
    bytes
  }

  private def createUncompactedArray(types: ShortArray, array: Any, offsetBytes: Int): scala.Array[Byte] = {
    val arrayLength: Int = array.asInstanceOf[Array[_]].length
    val bytesPerElement:Int = types.getMaxBits / 8
    val len: Int = 3 + bytesPerElement * arrayLength + offsetBytes
    val bytes:scala.Array[Byte] = new scala.Array[Byte](len)
    bytes(offsetBytes + 0) = types.intValue.toByte
    bytes(offsetBytes + 1) = 8
    bytes(offsetBytes + 2) =types.getMaxBits.toByte
    types.writeAll(array, bytes, 3 + offsetBytes)
    bytes
  }
  def allocateFromNumbers(target: util.Collection[DynamicVersionRecord], array: Any, recordAllocator: DynamicVersionRecordAllocator):Unit = {
    val bytes: Array[Byte] = encodeFromNumbers(array,0)
    AbstractDynamicVersionStore.allocateRecordsFromBytes(target,bytes,recordAllocator)
  }

  private def allocateFromCompositeType(target: util.Collection[DynamicVersionRecord], bytes: Array[Byte],
                                        recordAllocator: DynamicVersionRecordAllocator, allowsStorage: Boolean, storageCapability: Capability):Unit = {
    if(allowsStorage) AbstractDynamicVersionStore.allocateRecordsFromBytes(target,bytes,recordAllocator)
    else throw new UnsupportedFormatCapabilityException(storageCapability)
  }
  private def allocateFromString(target: util.Collection[DynamicVersionRecord], array: Array[String], recordAllocator: DynamicVersionRecordAllocator): Unit = {
    val stringAsBytes: Array[Array[Byte]] = new Array[Array[Byte]](array.length)
    var totalBytesRequired = 5
    array.foreach(x => {
      val bytes: Array[Byte] = PropertyStore.encodeString(x)
      totalBytesRequired += 4 + bytes.length
      stringAsBytes.update(array.indexOf(x),bytes)
    })
    val buf: ByteBuffer = ByteBuffer.allocate(totalBytesRequired)
    buf.put(PropertyType.STRING.byteValue())
    buf.putInt(array.length)
    stringAsBytes.foreach(x => {
      buf.putInt(x.length)
      buf.put(x)
    })
    AbstractDynamicVersionStore.allocateRecordsFromBytes(target,buf.array(),recordAllocator)
  }

  def allocateRecords(target: util.Collection[DynamicVersionRecord], array: Any, recordAllocator: DynamicVersionRecordAllocator, allowStorePointsAndTemporal: Boolean): Unit = {
    array match{
      case x:Array[_] => x match{
        case value:Array[String] => allocateFromString(target,value,recordAllocator)
        case value:Array[PointValue] => allocateFromCompositeType(target,GeometryType.encodePointArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.POINT_PROPERTIES)
        case value:Array[LocalDate] => allocateFromCompositeType(target,TemporalType.encodeDateArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case value:Array[LocalTime] => allocateFromCompositeType(target,TemporalType.encodeLocalTimeArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case value:Array[LocalDateTime] => allocateFromCompositeType(target,TemporalType.encodeLocalDateTimeArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case value:Array[OffsetTime] => allocateFromCompositeType(target,TemporalType.encodeTimeArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case value:Array[ZonedDateTime] => allocateFromCompositeType(target,TemporalType.encodeDateTimeArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case value:Array[DurationValue] => allocateFromCompositeType(target,TemporalType.encodeDurationArray(value),recordAllocator,allowStorePointsAndTemporal,Capability.TEMPORAL_PROPERTIES)
        case _ => allocateFromNumbers(target,array,recordAllocator)
      }
      case _ => throw new IllegalArgumentException(s"${array} not an array")

    }
  }

 private def geometryHeaderfromArrayHeaderBytes(header: Array[Byte]): GeometryHeader = {

   GeometryHeader.fromArrayHeaderByteBuffer(ByteBuffer.wrap(header))
    //val geometryType = header(1).toInt//Byte.toUnsignedInt(header(1))
   // val dimension = header(2).toInt//Byte.toUnsignedInt(header(2))
   // val crsTableId = header(3).toInt//Byte.toUnsignedInt(header(3))
   // val crsCode = (header(4).toInt << 8) + header(5).toInt//(Byte.toUnsignedInt(header(4)) << 8) + Byte.toUnsignedInt(header(5))
   // new GeometryType.GeometryHeader(geometryType, dimension, crsTableId, crsCode)
  }

  private def temporalHeaderfromArrayHeaderBytes(header: Array[Byte]): TemporalHeader = {
    TemporalHeader.fromArrayHeaderByteBuffer(ByteBuffer.wrap(header))
    //val temporalType = header(0).toInt
    //new TemporalType.TemporalHeader(temporalType)
  }

  def getRightArray (data: (Array[Byte], Array[Byte])): Value = {
    val header = data._1
    val bArray = data._2
    val typeId = header(0)
    var length = 0
    if(typeId != PropertyType.STRING.intValue()){
      if(typeId == PropertyType.GEOMETRY.intValue()){
        val geometryHeader: GeometryHeader = geometryHeaderfromArrayHeaderBytes(header)
        GeometryType.decodeGeometryArray(geometryHeader,bArray)
      }
      else if (typeId == PropertyType.TEMPORAL.intValue()){
        val temporalHeader: TemporalHeader = temporalHeaderfromArrayHeaderBytes(header)
        TemporalType.decodeTemporalArray(temporalHeader,bArray)
      }
      else{
        val typ = ShortArray.typeOf(typeId)
        val bitsUsedInLastByte = header(1)
        val requiredBits = header(2)
        if (requiredBits == 0) return typ.createEmptyArray
        else if ((typ eq ShortArray.BYTE) && requiredBits == 8) return Values.byteArray(bArray)
        else {
          val bits = Bits.bitsFromBytes(bArray)
          length = (bArray.length * 8 - (8 - bitsUsedInLastByte)) / requiredBits
          typ.createArray(length, bits, requiredBits)
        }
      }
    }
    else{
      val headerBuffer = ByteBuffer.wrap(header, 1, header.length - 1)
      val arrayLength = headerBuffer.getInt
      val result = new Array[String](arrayLength)
      val dataBuffer = ByteBuffer.wrap(bArray)
      result.map(x => {
        val bytelength = dataBuffer.getInt
        val stringByteArray: Array[Byte] = new Array[Byte](bytelength)
        dataBuffer.get(stringByteArray)
        PropertyStore.decodeString(stringByteArray)
      })

      Values.stringArray(result:_*)
    }

  }

}

class DynamicVersionArrayStore(file: File, idFile: File,
                               configuration: Config, idType: IdType,
                               idGeneratorFactory: IdGeneratorFactory, pageCache: PageCache,
                               logProvider: LogProvider, dataSizeFromConfiguration: Int,
                               recordFormats: RecordFormats, openOptions: OpenOption*)
  extends AbstractDynamicVersionStore(file,idFile,configuration,idType,idGeneratorFactory,pageCache,logProvider,
    s"VersionArrayPopertyStore",dataSizeFromConfiguration,new DynamicVersionRecordFormat,recordFormats.storeVersion(),openOptions:_*) {
  val NUMBER_HEADER_SIZE = 3
  val STRING_HEADER_SIZE = 5
  val GEOMETRY_HEADER_SIZE = 6
  val TEMPORAL_HEADER_SIZE = 2
  val TYPE_DESCRIPTOR = "ArrayPropertyStore"
  private var allowStorePointsAndTemporal: Boolean = recordFormats.hasCapability(Capability.POINT_PROPERTIES) && recordFormats.hasCapability(Capability.TEMPORAL_PROPERTIES)

  override def accept[FAILURE <: Exception](processor: RecordStore.Processor[FAILURE], record: DynamicVersionRecord): Unit = ???
  def allocateRecordsEX(target: util.Collection[DynamicVersionRecord], array: Any): Unit = {
    DynamicVersionArrayStore.allocateRecords(target,array,this,this.allowStorePointsAndTemporal)
  }

  def getArrayFor(records: Iterable[DynamicVersionRecord]): Any = {
    DynamicVersionArrayStore.getRightArray(this.readFullByteArray(records,PropertyType.ARRAY))
  }
}
























