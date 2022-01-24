package DynamicGraph.store

import java.io.File
import java.nio.file.OpenOption

import DynamicGraph.store.versionStore.DynamicVersionArrayStore
import org.neo4j.graphdb.config.Setting
import org.neo4j.graphdb.factory.GraphDatabaseSettings
import org.neo4j.io.fs.FileSystemAbstraction
import org.neo4j.io.layout.DatabaseLayout
import org.neo4j.io.pagecache.PageCache
import org.neo4j.io.pagecache.tracing.cursor.context.VersionContextSupplier
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.{CommonAbstractStore, NeoStores, StoreHeader, StoreType}
import org.neo4j.kernel.impl.store.format.RecordFormats
import org.neo4j.kernel.impl.store.id.{IdGeneratorFactory, IdType}
import org.neo4j.kernel.impl.store.record.AbstractBaseRecord
import org.neo4j.logging.LogProvider

import scala.collection.JavaConverters._
class DynamicNeoStores(layout: DatabaseLayout,
                       config: Config, idGeneratorFactory: IdGeneratorFactory,
                       pageCache: PageCache,
                       logProvider: LogProvider,
                       fileSystemAbstraction: FileSystemAbstraction,
                       versionContextSupplier: VersionContextSupplier,
                       recordFormats: RecordFormats,
                       createIfNotExist: Boolean,
                       storeTypes: Array[StoreType],
                       openOptions: Array[OpenOption]) extends
  NeoStores(layout,config,idGeneratorFactory,pageCache,logProvider,fileSystemAbstraction,versionContextSupplier,recordFormats,createIfNotExist,storeTypes,openOptions){

  private def initialize[T <: CommonAbstractStore[_ <:AbstractBaseRecord,_ <:StoreHeader]](store: T): T = {
    store.initialise(this.createIfNotExist)
    store
  }

  // createDynamicArrayStore(storeFile: File, idFile: File, idType: IdType, blockSize: Int)
  private def createDynamicVersionArrayStore(storeFile: File, idFile: File, idType: IdType, blockSize: Int) ={
    if(blockSize <= 0) throw new IllegalArgumentException("Block size of dynamic version array store should be positive integer.")
    else this.initialize(new DynamicVersionArrayStore(storeFile,idFile,this.config,idType,this.idGeneratorFactory,this.pageCache,this.logProvider,blockSize,this.recordFormats,this.openOptions:_*))
  }

  /*def createDynamicVersionArrayStore(storeFile: File, idFile: File, idType: IdType, blockSizeProperty:Int): CommonAbstractStore[_ <: AbstractBaseRecord, _ <: StoreHeader] = {
    this.createDynamicVersionArrayStore(storeFile,idFile,idType,blockSizeProperty)
  }*/

  def createNodeVersionLabelStore(): CommonAbstractStore[_ <: AbstractBaseRecord, _ <: StoreHeader] ={
    this.createDynamicVersionArrayStore(this.layout.nodeVersionLabelStore(),this.layout.idNodeLabelStore(),IdType.NODE_LABELS,this.config.get(GraphDatabaseSettings.label_block_size))
  }

}





































