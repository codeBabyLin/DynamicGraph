package DynamicGraph.store

import java.util

import org.neo4j.kernel.impl.store.{NodeLabelsField, record}
import org.neo4j.kernel.impl.store.record.{DynamicRecord, NodeRecord, PrimitiveRecord, PropertyRecord, Record}

import scala.collection.JavaConverters._
class DynamicNodeRecord(id: Long) extends NodeRecord(id){

  var nextRel: Long = 0
  var labels: Long = 0
  var dynamicLabelRecords: Iterable[DynamicRecord] = Iterable.empty
  var islight: Boolean = false
  var dense: Boolean = false

  override def initialize(inUse: Boolean, nextProp: Long, dense: Boolean, nextRel: Long, labels: Long): DynamicNodeRecord ={
    super.initialize(inUse,nextProp)
    this.nextRel = nextRel
    this.dense = dense
    this.labels = labels
    this.dynamicLabelRecords = Iterable.empty
    this.islight = true
    this
  }

  override def clear(): Unit ={
    this.initialize(false,Record.NO_NEXT_PROPERTY.intValue(),false, Record.NO_NEXT_RELATIONSHIP.intValue(),Record.NO_LABELS_FIELD.intValue())
  }
  override def setIdTo(propertyRecord: PropertyRecord): Unit = propertyRecord.setNodeId(this.getId)
  override def getNextRel: Long ={
    this.nextRel
  }
  override def setNextRel(nextRel: Long): Unit ={
    this.nextRel = nextRel
  }

  def setLabelField(lables: Long, dynamicRecords: Iterable[DynamicRecord]): Unit ={
    this.labels = lables
    this.dynamicLabelRecords = dynamicRecords
    this.islight = dynamicRecords.isEmpty
  }
  def getLabelFiled: Long = {
    this.labels
  }
  //def isLight(): Boolean = {
  //  this.isLight
  //}
  //overgetD
/*  override def getDynamicLabelRecords: Iterable[DynamicRecord] ={
    this.dynamicLabelRecords
  }*/

  //override def getUsedDynamicLabelRecords: Iterable[DynamicRecord] = this.getDynamicLabelRecords.filter(x=> x.inUse())

  override def isDense: Boolean = {
    this.dense
  }
  override def setDense(dense: Boolean): Unit = {
    this.dense = dense
  }

  override def toString: String = {
    val denseInfo = if(this.dense) s"group=${this.nextRel}" else s"rel=${this.nextRel}"
    val lightHeavyInfo = if(this.islight) s"light" else{
      if(this.dynamicLabelRecords.isEmpty) s"heavy"
      else s"heavy,dynlabels=${this.dynamicLabelRecords}"
    }
    s"Node[${this.getId},used=${this.inUse()},${denseInfo},prop=${this.getNextProp},labels=${NodeLabelsField.parseLabelsField(this)},${lightHeavyInfo},secondaryUnitId=${this.getSecondaryUnitId}]"
  }

  override def clone(): DynamicNodeRecord = {
    val nodeRecord = super.clone()
    val clone = new DynamicNodeRecord(nodeRecord.getId)
    clone.initialize(this.inUse(),this.nextProp,this.dense,this.nextRel,this.labels)
    clone.dynamicLabelRecords = nodeRecord.getDynamicLabelRecords.asScala
    clone.setSecondaryUnitId(this.getSecondaryUnitId)
    clone
  }

}





















