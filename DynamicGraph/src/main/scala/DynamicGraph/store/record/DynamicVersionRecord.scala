package DynamicGraph.store.record

import org.neo4j.kernel.impl.store.record.{AbstractBaseRecord, DynamicRecord}
import org.neo4j.kernel.impl.store.{PropertyStore, PropertyType}

class DynamicVersionRecord(id: Long) extends AbstractBaseRecord(id){
  val NO_DATA: Array[Byte] = new Array[Byte](0)
  private val MAX_BYTES_IN_TO_STRING = 8
  private val MAX_CHARS_IN_TO_STRING = 16
  private var data: Array[Byte] = null
  private var length: Int = 0
  private var nextBlock: Long = 0L
  private var types: Int = 0
  private var version: Int = 0
  private var startRecord: Boolean = false

  def initialize(inUse: Boolean,isStartRecord: Boolean, nextBlock: Long,types: Int,length: Int,version: Int): DynamicVersionRecord = {
    super.initialize(inUse)
    this.startRecord = isStartRecord
    this.nextBlock = nextBlock
    this.types = types
    this.data = NO_DATA
    this.length = length
    this.version = version
    this
  }

  override def clear(): Unit ={
    this.initialize(false,true,-1,-1,0,-1)
  }
  def setVersion(version: Int): Unit ={
    this.version = version
  }
  def getVersion: Int = this.version

  def setStartRecord(startRecord: Boolean): Unit ={
    this.startRecord = startRecord
  }
  def isStartRecord: Boolean = this.startRecord
  def getType: PropertyType = PropertyType.getPropertyTypeOrNull(this.types.toLong << 24)
  def getTypeAsInt(): Int = this.types
  def setType(types: Int): Unit = {
    this.types = types
  }
  def setLength(length: Int): Unit = {
    this.length = length
  }
  def setInUse(inUse: Boolean, types: Int): Unit ={
    this.types = types
    this.setInUse(inUse)
  }

  def setData(data: Array[Byte]): Unit ={
    this.length = data.length
    this.data = data
  }

  def getLength: Int = this.length

  def getData: Array[Byte] = this.data

  def getNextBlock: Long = this.nextBlock

  def setNextBlock(nextBlock: Long): Unit = {
    this.nextBlock = nextBlock
  }


  override def toString: String = {
    val buf = new StringBuilder
    buf.append("DynamicRecord[").append(this.getId).append(",used=").append(this.inUse).append(',').append('(').append(this.length).append("),type=")
    val types = this.getType
    if (types == null) buf.append(this.types)
    else buf.append(types.name)
    buf.append(",data=")
    if ((types eq PropertyType.STRING) && this.data.length <= 16) {
      buf.append('"')
      buf.append(PropertyStore.decodeString(this.data))
      buf.append("\",")
    }
    else {
      buf.append("byte[")
      if (this.data.length <= 8) {
        var i = 0
        while ( {
          i < this.data.length
        }) {
          if (i != 0) buf.append(',')
          buf.append(this.data(i))

          {
            i += 1; i
          }
        }
      }
      else buf.append("size=").append(this.data.length)
      buf.append("],")
    }
    buf.append("start=").append(this.startRecord)
    buf.append(",next=").append(this.nextBlock).append(']')
    buf.toString
  }

  override def clone: DynamicVersionRecord = {
    val clone = new DynamicVersionRecord(this.getId).initialize(this.inUse, this.startRecord, this.nextBlock, this.types, this.length,this.version)
    if (this.data != null) clone.setData(this.data.clone.asInstanceOf[Array[Byte]])
    clone
  }


}
