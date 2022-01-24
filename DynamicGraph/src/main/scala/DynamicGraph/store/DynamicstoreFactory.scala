package DynamicGraph.store

import java.io.IOException
import java.nio.file.OpenOption

import org.neo4j.io.fs.FileSystemAbstraction
import org.neo4j.io.layout.DatabaseLayout
import org.neo4j.io.pagecache.PageCache
import org.neo4j.io.pagecache.tracing.cursor.context.VersionContextSupplier
import org.neo4j.kernel.configuration.Config
import org.neo4j.kernel.impl.store.format.RecordFormatSelector
import org.neo4j.kernel.impl.store.{NeoStores, StoreFactory, StoreType, UnderlyingStorageException}
import org.neo4j.kernel.impl.store.id.IdGeneratorFactory
import org.neo4j.logging.LogProvider

class DynamicstoreFactory(databaseLayout: DatabaseLayout,
                          config: Config,
                          idGeneratorFactory: IdGeneratorFactory,
                          pageCache: PageCache,
                          fileSystemAbstraction: FileSystemAbstraction,
                          logProvider: LogProvider,
                          versionContextSupplier: VersionContextSupplier) extends StoreFactory(
  databaseLayout,config,idGeneratorFactory,pageCache,fileSystemAbstraction,logProvider,versionContextSupplier){
  /*override def openAllNeoStores(createStoreIfNotExists: Boolean): NeoStores = {
    openAllNeoStores(createStoreIfNotExists,StoreType.)
  }*/
  val recordFormats = RecordFormatSelector.selectForStoreOrConfig(config, databaseLayout, fileSystemAbstraction, pageCache, logProvider)
  val openOptions:Array[OpenOption]  = Array.empty

  override def openAllNeoStores(createStoreIfNotExists: Boolean): NeoStores = {
    dyopenNeoStores(createStoreIfNotExists,StoreType.values())
  }

  def dyopenNeoStores(createStoreIfNotExists: Boolean, storeTypes: Array[StoreType]): NeoStores = {
    if (createStoreIfNotExists) try this.fileSystemAbstraction.mkdirs(this.databaseLayout.databaseDirectory)
    catch {
      case var4: IOException =>
        throw new UnderlyingStorageException("Could not create database directory: " + this.databaseLayout.databaseDirectory, var4)
    }

    new DynamicNeoStores(this.databaseLayout, this.config, this.idGeneratorFactory, this.pageCache, this.logProvider, this.fileSystemAbstraction, this.versionContextSupplier, this.recordFormats, createStoreIfNotExists, storeTypes, this.openOptions)

  }

}
