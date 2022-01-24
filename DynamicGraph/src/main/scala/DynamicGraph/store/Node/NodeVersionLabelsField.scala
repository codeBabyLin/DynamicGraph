package DynamicGraph.store.Node

import java.util
import java.util.Collection

import DynamicGraph.store.record.{DynamicVersionRecord, DynamicVersionRecordAllocator}
import org.neo4j.collection.PrimitiveLongCollections
import org.neo4j.kernel.impl.store.NodeLabels
import org.neo4j.kernel.impl.store.record.{DynamicRecord, NodeRecord}
import org.neo4j.kernel.impl.util.Bits

trait NodeVersionLabels{
  def get(nodeVersionStore: NodeVersionStore): Array[Long]
  def getIfLoaded: Array[Long]
  def put(labes: Array[Long],nodeVersionStore: NodeVersionStore,dynamicVersionRecordAllocator: DynamicVersionRecordAllocator):util.Collection[DynamicVersionRecord]
  def add(label: Long, nodeVersionStore: NodeVersionStore, dynamicVersionRecordAllocator: DynamicVersionRecordAllocator): util.Collection[DynamicVersionRecord]
  def remove(label: Long, nodeVersionStore: NodeVersionStore)
  def isInlined(): Boolean

}

object InlineNodeVersionLabels{

  def tryInlineInNodeRecord(node: NodeRecord, ids: Array[Long], changedDynamicRecords: util.Collection[DynamicRecord]): Boolean = {
    if(ids.length > 7)
  }

  def inlineValues(values: Array[Long], maxBitsPerLabel: Int, target: Bits):Boolean = {
    val limit: Long = 1L << maxBitsPerLabel
    if(values.exists(x=> java.lang.Long.highestOneBit(x) >= limit)) false
    else{
      values.foreach(x=> target.put(x,maxBitsPerLabel))
      true
    }
  }
  def parseInlined(labelField: Long): Array[Long] ={
    val numberOfLabels: Byte = labelCount(labelField)
    if(numberOfLabels == 0) PrimitiveLongCollections.EMPTY_LONG_ARRAY
    else {
      val existingLabelsField: Long = NodeVersionLabelsField.parseLabelsBody(labelField)
      val bitsPerLabel: Byte = (36/numberOfLabels).toByte
      val bits: Bits = Bits.bitsFromLongs(Array(existingLabelsField))
      val result: Array[Long] = new Array[Long](numberOfLabels)
      result.map(x => bits.getLong(bitsPerLabel))
    }
  }
  def labelCount(labelField: Long): Byte = ((labelField & 1030792151040L) >>> 36).toInt.toByte
  def combineLabelCountAndLabelStorage(labelCount: Byte, labelBits: Long): Long = (labelCount << 36).toLong | labelBits

}

class InlineNodeVersionLabels(node: NodeRecord) extends NodeVersionLabels {
  val LABEL_BITS: Int = 36
  //var node: NodeRecord = _



  override def get(nodeVersionStore: NodeVersionStore): Array[Long] = ???

  override def getIfLoaded: Array[Long] = ???

  override def put(labes: Array[Long], nodeVersionStore: NodeVersionStore, dynamicVersionRecordAllocator: DynamicVersionRecordAllocator): util.Collection[DynamicVersionRecord] = ???

  override def add(label: Long, nodeVersionStore: NodeVersionStore, dynamicVersionRecordAllocator: DynamicVersionRecordAllocator): util.Collection[DynamicVersionRecord] = ???

  override def remove(label: Long, nodeVersionStore: NodeVersionStore): Unit = ???

  override def isInlined(): Boolean = true

  override def toString: String = s"Inline(${this.node.getLabelField},${util.Arrays.toString(this.getIfLoaded)})"
}



object NodeVersionLabelsField{

  def get(node: NodeRecord,nodeVersionStore: NodeVersionStore): Unit ={
    val labelField: Long = node.getLabelField
  }
  def fieldPointsToDynamicRecordOfLabels(labelField: Long):Boolean = (labelField & 549755813888L) != 0L
  def parseLabelsBody(labelField: Long):Long = labelField & 68719476735L
  def firstDynamicLabelRecordId(labelField: Long): Long = {
    assert(fieldPointsToDynamicRecordOfLabels(labelField))
    parseLabelsBody(labelField)
  }



  def parseLabelsField (node: NodeRecord): NodeLabels ={
    val labelField = node.getLabelField
  }
  def isSane(labelIds: Array[Long]): Boolean = {
    var issane: Boolean = true
    var prev:Long = -1L

    labelIds.foreach(x => {
      if (x > prev) issane = false
      prev = x
    })
    issane
  }
}

class NodeVersionLabelsField {

}
