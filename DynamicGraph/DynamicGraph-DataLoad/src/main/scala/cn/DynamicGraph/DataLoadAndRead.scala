package cn.DynamicGraph

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Calendar, Objects}
import java.util.function.Consumer

import cn.DynamicGraph.Common.DGVersion
import org.neo4j.graphdb.{GraphDatabaseService, Label, Node}
//import org.neo4j.graphdb.{GraphDatabaseService, Label, Node}
//import cn.DynamicGraph.graphdb.VersionGraphDatabaseService
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

import scala.io.Source
class NewMola(jsonStr: String){
  var code: String = ""
  var status:Int = 0
  var msg: String = ""
  var name: String =""
  var updateDateArray: Array[String] = _
  var confirmedArray: Array[Int] = _
  var curedArray: Array[Int] = _
  var diedArray: Array[Int] = _
  var newConfirmedArray: Array[Int] = _
  var newConfirmedLocalArray: Array[Int] = _

  var confirmedDateMap: Map[String,Int] = _
  var curedDateMap: Map[String,Int] = _
  var diedDateMap: Map[String,Int] = _
  var newConfirmedDateMap: Map[String,Int] = _
  var newConfirmedLocalDateMap: Map[String,Int] = _


  init()

  def setCode(code: String): Unit ={
    this.code = code
  }
  def fixDate(x: String): String = {
    if(x.contains("12.")) s"2021.${x}"
    else s"2022.${x}"

  }
  def init(): Unit ={
    val js = JSON.parseObject(jsonStr)
    //val node: Node = graphDb.findNode(label,"code",code)
    //val data =
    this.status = js.get("status").asInstanceOf[Int]
    this.msg = js.get("msg").asInstanceOf[String]
    val data = js.get("data").asInstanceOf[JSONArray].get(0).asInstanceOf[JSONObject]
    this.name = data.get("name").asInstanceOf[String]
    val data2 = data.get("trend").asInstanceOf[JSONObject]

    val updateDate = data2.get("updateDate")
    val dataList = data2.get("list").asInstanceOf[JSONArray]

    this.updateDateArray = updateDate.asInstanceOf[JSONArray].toArray.map(x => fixDate(x.asInstanceOf[String]))
    dataList.toArray.map(x => {
      val data = x.asInstanceOf[JSONObject]
      val name = data.get("name")
      val realData = data.get("data").asInstanceOf[JSONArray]
      name match{
        case "确诊" => this.confirmedArray = realData.toArray.map(x => x.asInstanceOf[Int])
        case "治愈" => this.curedArray = realData.toArray.map(x => x.asInstanceOf[Int])
        case "死亡" => this.diedArray = realData.toArray.map(x => x.asInstanceOf[Int])
        case "新增确诊" => this.newConfirmedArray = realData.toArray.map(x => x.asInstanceOf[Int])
        case "新增本土" => this.newConfirmedLocalArray = realData.toArray.map(x => x.asInstanceOf[Int])
      }
    })
    // this.updateDateArray.map(fixDate)
    this.confirmedDateMap = this.updateDateArray.zip(this.confirmedArray).toMap
    this.curedDateMap = this.updateDateArray.zip(this.curedArray).toMap
    this.diedDateMap = this.updateDateArray.zip(this.diedArray).toMap
    this.newConfirmedDateMap = this.updateDateArray.zip(this.newConfirmedArray).toMap
    this.newConfirmedLocalDateMap = this.updateDateArray.zip(this.newConfirmedLocalArray).toMap

  }


}
object DataLoadAndRead {

  //override val path: String = "F:\\DynamicGraphStore\\newOral"


  val confirmed: String = "confirmedSum"
  val cured: String = "curedSum"
  val died: String = "diedSum"
  val newConfirmed: String = "newConfirmed"
  val newConfirmedLocal: String = "newConfirmedLocal"
  val dateArray: Array[String] = getDateoff()
  var dateVersion: Map[String, Long] = _

  def prepareData(graphDb: GraphDatabaseService,dataPath: String): Unit ={


    val file1:File = new File(dataPath)
    if(!file1.exists()) println("hello  this is class dataLoadandRead!!!!")


    //val dataPath = Objects.requireNonNull(graphDb.getClass.getClassLoader.getResource("WebPage/Data/provincetr.txt")).getPath
    //val dpta = this.getClass.getClassLoader.getResource("WebPage/Data").getPath

   // new File(dpta).listFiles().foreach(u => println(u.getName))

    //val dataPath = this.getClass.getClassLoader.getResource("WebPage/Data/provincetr.txt").getPath
    //val dataPath = "F:\\Dydata\\nameData\\provincetr.txt"

    val tx = graphDb.beginTx()

    val file=Source.fromFile(file1)
    val label = new Label {
      override def name(): String = "Province"
    }
    for(line <- file.getLines)
    {
      val strList = line.toString.split(",")
      val code = strList(0)
      val name = strList(1)
      val node: Node = graphDb.createNode()
      node.setProperty("code",code)
      node.setProperty("name",name)
      node.addLabel(label)


      //println(line)
    }
    file.close
    tx.success()

    tx.close()


  }
  def prepareDataNewMola(graphDb: GraphDatabaseService,dateVersion:Map[String,Long],dataPath: String): Unit ={






    val label = new Label {
      override def name(): String = "Province"
    }
    val provinceMola: Array[NewMola] = new Array[NewMola](31)
    //val dataPath2 =
   // val dataPath =  this.getClass.getClassLoader.getResource("WebPage/Data/newOral.txt").getPath
    //val dataPath =  this.getClass.getClassLoader.getResource("newOral.txt").getPath
    println(s"readData:$dataPath")
    //val dataPath = "F:\\Dydata\\nameData\\newOral.txt"
    val file=Source.fromFile(dataPath)
    var index = 0
    for(line <- file.getLines)
    {
      val strList = line.toString.split(",",2)
      val code = strList(0)
      val name = strList(1)
      val molaData = new NewMola(name)
      molaData.setCode(code)
      provinceMola.update(index,molaData)
      index =index +1
    }


    dateArray.foreach(x => {
      val xtVersion = DGVersion.setStartVersion(dateVersion(x))
      val xtDate = x
      //println(s"date:${xtDate}")
      val tx  = graphDb.beginTx(xtVersion)
      provinceMola.foreach({m => {
        //println(s"name:${m.name},code:${m.code},date:${xtDate}")

        val node = graphDb.findNode(label,"code",m.code)
        val nodeConfirm = m.confirmedDateMap.getOrElse(xtDate,-1)
        val nodecured = m.curedDateMap.getOrElse(xtDate,-1)
        val nodedied = m.diedDateMap.getOrElse(xtDate,-1)
        val nodenewConfirmed = m.newConfirmedDateMap.getOrElse(xtDate,-1)
        val nodenewConfirmedLocal = m.newConfirmedLocalDateMap.getOrElse(xtDate,-1)
        node.setProperty(confirmed,nodeConfirm)
        node.setProperty(cured,nodecured)
        node.setProperty(died,nodedied)
        node.setProperty(newConfirmed,nodenewConfirmed)
        node.setProperty(newConfirmedLocal,nodenewConfirmedLocal)
      }})
      tx.success()
      tx.close()
    })

  }


  def readDataShow(graphDb: GraphDatabaseService,date: String,version: Long): Unit ={
    val tx = graphDb.beginTx()
    graphDb.getAllNodes.stream().forEach(new Consumer[Node] {
      override def accept(t: Node): Unit = {
        val str = s"id:${t.getId},code:${t.getProperty("code")},name:${t.getProperty("name")},label:${t.getLabels},version:${t.getNodeVersion}"
        val tConfirmd = t.getProperty(confirmed,version)
        val tCured = t.getProperty(cured,version)
        val tDied = t.getProperty(died,version)
        val tNewConfirmed = t.getProperty(newConfirmed,version)
        val tNewConfirmedLocal = t.getProperty(newConfirmedLocal,version)
        val str1 = s"date:${date},${confirmed}:${tConfirmd},${cured}:${tCured},${died}:${tDied},${newConfirmed}:${tNewConfirmed},${newConfirmedLocal}:${tNewConfirmedLocal}"
        //val str1 = s"date:${date},${newConfirmedLocal}:${tNewConfirmedLocal}"
        //val str1 = s"date:${date},${newConfirmedLocal}:${tConfirmd}"
        println(str)
        println(str1)
      }
    })
    tx.success()
    tx.close()
  }

  def getDateoff():Array[String] =  {
    val simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd")

    val date1 = simpleDateFormat.parse("2021.12.21")
    val date2 = simpleDateFormat.parse("2022.3.22")
    val calender: Calendar = Calendar.getInstance()
    val calender2: Calendar = Calendar.getInstance()
    calender.setTime(date1)
    calender2.setTime(date2)

    var dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH) + 1}.${calender.get(Calendar.DATE)}"
    val endateStr = "2022.3.22"
    val dateArray: Array[String] = new Array[String](91)
    var dayCount: Int = 0

    while (!calender.equals(calender2)) {
      dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH) + 1}.${calender.get(Calendar.DATE)}"
      //val str = calender.getTime.toString
      //println(dateStr)
      dateArray.update(dayCount, dateStr)
      dayCount = dayCount + 1
      calender.add(Calendar.DATE, 1)
    }
    dateArray.take(dayCount)
  }


  def prepareData2(graphDb:GraphDatabaseService,path1: String,path2: String): Unit ={
    prepareData(graphDb,path1)
    val versions:Array[Long] = graphDb.getNextVersions(dateArray.size)

    dateVersion = dateArray.toSeq.zip(versions.toSeq).toMap
    prepareDataNewMola(graphDb,dateVersion,path2)

  }
  def getData(graphDb: GraphDatabaseService,date: String): String ={
    val tx = graphDb.beginTx()
    val version = dateVersion(date)
   // val strAll: Array[String]
   val jsAll: Array[String] = new Array[String](91)
    var cnt = 0
    graphDb.getAllNodes.stream().forEach(new Consumer[Node] {
      override def accept(t: Node): Unit = {
        val str = s"id:${t.getId},code:${t.getProperty("code")},name:${t.getProperty("name")},label:${t.getLabels},version:${t.getNodeVersion}"
        val tConfirmd = t.getProperty(confirmed,version)
        val tCured = t.getProperty(cured,version)
        val tDied = t.getProperty(died,version)
        val tNewConfirmed = t.getProperty(newConfirmed,version)
        val tNewConfirmedLocal = t.getProperty(newConfirmedLocal,version)
        //val str1 = s"{code:${t.getProperty("code")},date:${date},${confirmed}:${tConfirmd},${cured}:${tCured},${died}:${tDied},${newConfirmed}:${tNewConfirmed},${newConfirmedLocal}:${tNewConfirmedLocal}}"
        val str1 = s"code:${t.getProperty("code")},date:${date},${confirmed}:${tConfirmd},${cured}:${tCured},${died}:${tDied},${newConfirmed}:${tNewConfirmed},${newConfirmedLocal}:${tNewConfirmedLocal}"

        jsAll.update(cnt,str1)
        cnt = cnt + 1
        //val str1 = s"date:${date},${newConfirmedLocal}:${tNewConfirmedLocal}"
        //val str1 = s"date:${date},${newConfirmedLocal}:${tConfirmd}"
        //println(str)
        //println(str1)
      }
    })
    tx.success()
    tx.close()
    val str = jsAll.take(cnt).mkString("#")
    str
    //jsAll.take(cnt).mkString("[",",","]")
  }


}
