package DynamicGraph.store

import java.util.{Objects, Optional}

import org.neo4j.io.layout.DatabaseFile
import org.neo4j.util.Preconditions


object DynamicDatabaseFile extends Enumeration{
  type DynamicDatabaseFile = Value

  val Dynamic_Node_LABEL_STORE = Value("neostore.nodestore.db.dylabels")

  def checkExists(day: String) = this.values.exists(_.toString == day)

  def showAll = this.values.foreach(println)

  //var names: List[String]


/*  def getNames: List[String] = this.names
  def hasIdFile: Boolean = this.hasIdFile

  def getName: String = {
    Preconditions.checkState(this.names.size == 1, "Database file has more then one file names.")
    this.names(0)
  }*/

/*  def fileOf(name: String): Option[DynamicDatabaseFile] = {
    Objects.requireNonNull(name)
    val databaseFiles = values
    if (databaseFiles.contains(Value(name))) Value(name)
    else None


  }*/

}
