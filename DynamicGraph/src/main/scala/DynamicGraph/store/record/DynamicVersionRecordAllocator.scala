package DynamicGraph.store.record

trait DynamicVersionRecordAllocator {
  def getRecordDataSize: Int
  def nextRecord: DynamicVersionRecord
}
