package cn.DynamicGraph.store.record

import org.neo4j.kernel.impl.store.id.IdSequence

object StandardDynamicVersionRecordAllocator{
  def allocateRecord(id: Long): DynamicVersionRecord ={
    val record: DynamicVersionRecord = new DynamicVersionRecord(id)
    record.setCreated()
    record.setInUse(true)
    record
  }
}

class StandardDynamicVersionRecordAllocator(idGenerator: IdSequence, dataSize: Int) extends DynamicVersionRecordAllocator{
  override def getRecordDataSize: Int = this.dataSize

  override def nextRecord: DynamicVersionRecord = StandardDynamicVersionRecordAllocator.allocateRecord(this.idGenerator.nextId())
}
