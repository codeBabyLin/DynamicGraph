package DynamicGraph.store

import java.io.File
import java.nio.file.OpenOption
import java.util

import org.neo4j.io.pagecache.PageCache
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.format.RecordFormats
import org.neo4j.kernel.impl.store.{DynamicArrayStore, NodeStore, RecordStore}
import org.neo4j.kernel.impl.store.id.IdGeneratorFactory
import org.neo4j.kernel.impl.store.record.{NodeRecord, RecordLoad}
import org.neo4j.logging.LogProvider

class DynamicNodeStore(file: File,
                       idFile: File,
                       config: Config,
                       idGeneratorFactory: IdGeneratorFactory,
                       pageCache: PageCache,
                       logProvider: LogProvider,
                       dynamicLabelStore: DynamicArrayStore,
                       recordFormats: RecordFormats,
                       openOptions: Array[OpenOption]) extends NodeStore(file,idFile,config,idGeneratorFactory,pageCache,logProvider,dynamicLabelStore,recordFormats,openOptions.toSeq:_*){

  override def accept[FAILURE <: Exception](processor: RecordStore.Processor[FAILURE], record: NodeRecord): Unit = super.accept(processor, record)

  override def getRecords(firstId: Long, mode: RecordLoad): util.List[NodeRecord] = super.getRecords(firstId, mode)

}
