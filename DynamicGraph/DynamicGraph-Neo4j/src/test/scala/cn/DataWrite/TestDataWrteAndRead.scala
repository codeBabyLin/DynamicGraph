//package cn.DataWrite
//
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.{Calendar, Date}
//import java.util.function.Consumer
//
//import cn.DynamicGraph.BaseTest
//import cn.DynamicGraph.Common.DGVersion
//import jdk.nashorn.internal.parser.JSONParser
//import org.junit.Test
//import org.neo4j.graphdb.{DataLoadAndRead, GraphDatabaseService, Label, Node}
//import org.neo4j.graphdb.factory.GraphDatabaseFactory
//import com.alibaba.fastjson.{JSON, JSONArray, JSONException, JSONObject}
//
//import scala.io.Source
//
//class NewMola(jsonStr: String){
//  var code: String = ""
//  var status:Int = 0
//  var msg: String = ""
//  var name: String =""
//  var updateDateArray: Array[String] = _
//  var confirmedArray: Array[Int] = _
//  var curedArray: Array[Int] = _
//  var diedArray: Array[Int] = _
//  var newConfirmedArray: Array[Int] = _
//  var newConfirmedLocalArray: Array[Int] = _
//
//  var confirmedDateMap: Map[String,Int] = _
//  var curedDateMap: Map[String,Int] = _
//  var diedDateMap: Map[String,Int] = _
//  var newConfirmedDateMap: Map[String,Int] = _
//  var newConfirmedLocalDateMap: Map[String,Int] = _
//
//
//  init()
//
//  def setCode(code: String): Unit ={
//    this.code = code
//  }
//  def fixDate(x: String): String = {
//    if(x.contains("12.")) s"2021.${x}"
//    else s"2022.${x}"
//
//  }
//  def init(): Unit ={
//    val js = JSON.parseObject(jsonStr)
//    //val node: Node = graphDb.findNode(label,"code",code)
//    //val data =
//    this.status = js.get("status").asInstanceOf[Int]
//    this.msg = js.get("msg").asInstanceOf[String]
//    val data = js.get("data").asInstanceOf[JSONArray].get(0).asInstanceOf[JSONObject]
//    this.name = data.get("name").asInstanceOf[String]
//    val data2 = data.get("trend").asInstanceOf[JSONObject]
//
//    val updateDate = data2.get("updateDate")
//    val dataList = data2.get("list").asInstanceOf[JSONArray]
//
//    this.updateDateArray = updateDate.asInstanceOf[JSONArray].toArray.map(x => fixDate(x.asInstanceOf[String]))
//    dataList.toArray.map(x => {
//      val data = x.asInstanceOf[JSONObject]
//      val name = data.get("name")
//      val realData = data.get("data").asInstanceOf[JSONArray]
//      name match{
//        case "确诊" => this.confirmedArray = realData.toArray.map(x => x.asInstanceOf[Int])
//        case "治愈" => this.curedArray = realData.toArray.map(x => x.asInstanceOf[Int])
//        case "死亡" => this.diedArray = realData.toArray.map(x => x.asInstanceOf[Int])
//        case "新增确诊" => this.newConfirmedArray = realData.toArray.map(x => x.asInstanceOf[Int])
//        case "新增本土" => this.newConfirmedLocalArray = realData.toArray.map(x => x.asInstanceOf[Int])
//      }
//    })
//   // this.updateDateArray.map(fixDate)
//    this.confirmedDateMap = this.updateDateArray.zip(this.confirmedArray).toMap
//    this.curedDateMap = this.updateDateArray.zip(this.curedArray).toMap
//    this.diedDateMap = this.updateDateArray.zip(this.diedArray).toMap
//    this.newConfirmedDateMap = this.updateDateArray.zip(this.newConfirmedArray).toMap
//    this.newConfirmedLocalDateMap = this.updateDateArray.zip(this.newConfirmedLocalArray).toMap
//
//  }
//
//
//}
//
//
//class TestDataWrteAndRead extends BaseTest {
//
//  override val path: String = "F:\\DynamicGraphStore\\newOral"
//
//
//  val confirmed: String = "confirmedSum"
//  val cured: String = "curedSum"
//  val died: String = "diedSum"
//  val newConfirmed: String = "newConfirmed"
//  val newConfirmedLocal: String = "newConfirmedLocal"
//  val dateArray: Array[String] = getDateoff()
//
//  def prepareData(graphDb: GraphDatabaseService): Unit ={
//    val dataPath = "F:\\Dydata\\nameData\\provincetr.txt"
//
//    val tx = graphDb.beginTx()
//
//    val file=Source.fromFile(dataPath)
//    val label = new Label {
//      override def name(): String = "Province"
//    }
//    for(line <- file.getLines)
//    {
//      val strList = line.toString.split(",")
//      val code = strList(0)
//      val name = strList(1)
//      val node: Node = graphDb.createNode()
//      node.setProperty("code",code)
//      node.setProperty("name",name)
//      node.addLabel(label)
//
//
//      //println(line)
//    }
//    file.close
//    tx.success()
//
//    tx.close()
//
//
//  }
//  def prepareDataNewMola(graphDb: GraphDatabaseService,dateVersion:Map[String,Long]): Unit ={
//
//
//
//
//
//
//    val label = new Label {
//      override def name(): String = "Province"
//    }
//    val provinceMola: Array[NewMola] = new Array[NewMola](31)
//    val dataPath = "F:\\Dydata\\nameData\\newOral.txt"
//    val file=Source.fromFile(dataPath)
//    var index = 0
//    for(line <- file.getLines)
//    {
//      val strList = line.toString.split(",",2)
//      val code = strList(0)
//      val name = strList(1)
//      val molaData = new NewMola(name)
//      molaData.setCode(code)
//      provinceMola.update(index,molaData)
//      index =index +1
//    }
//
//
//    dateArray.foreach(x => {
//      val xtVersion = DGVersion.setStartVersion(dateVersion(x))
//      val xtDate = x
//      //println(s"date:${xtDate}")
//      val tx  = graphDb.beginTx(xtVersion)
//      provinceMola.foreach({m => {
//        //println(s"name:${m.name},code:${m.code},date:${xtDate}")
//
//        val node = graphDb.findNode(label,"code",m.code)
//        val nodeConfirm = m.confirmedDateMap.getOrElse(xtDate,-1)
//        val nodecured = m.curedDateMap.getOrElse(xtDate,-1)
//        val nodedied = m.diedDateMap.getOrElse(xtDate,-1)
//        val nodenewConfirmed = m.newConfirmedDateMap.getOrElse(xtDate,-1)
//        val nodenewConfirmedLocal = m.newConfirmedLocalDateMap.getOrElse(xtDate,-1)
//        node.setProperty(confirmed,nodeConfirm)
//        node.setProperty(cured,nodecured)
//        node.setProperty(died,nodedied)
//        node.setProperty(newConfirmed,nodenewConfirmed)
//        node.setProperty(newConfirmedLocal,nodenewConfirmedLocal)
//      }})
//      tx.success()
//      tx.close()
//    })
//
//  }
//
//
//  def readDataShow(graphDb: GraphDatabaseService,date: String,version: Long): Unit ={
//    val tx = graphDb.beginTx()
//    graphDb.getAllNodes.stream().forEach(new Consumer[Node] {
//      override def accept(t: Node): Unit = {
//        val str = s"id:${t.getId},code:${t.getProperty("code")},name:${t.getProperty("name")},label:${t.getLabels},version:${t.getNodeVersion}"
//        val tConfirmd = t.getProperty(confirmed,version)
//        val tCured = t.getProperty(cured,version)
//        val tDied = t.getProperty(died,version)
//       val tNewConfirmed = t.getProperty(newConfirmed,version)
//        val tNewConfirmedLocal = t.getProperty(newConfirmedLocal,version)
//        val str1 = s"date:${date},${confirmed}:${tConfirmd},${cured}:${tCured},${died}:${tDied},${newConfirmed}:${tNewConfirmed},${newConfirmedLocal}:${tNewConfirmedLocal}"
//        //val str1 = s"date:${date},${newConfirmedLocal}:${tNewConfirmedLocal}"
//        //val str1 = s"date:${date},${newConfirmedLocal}:${tConfirmd}"
//        println(str)
//        println(str1)
//      }
//    })
//    tx.success()
//    tx.close()
//  }
//
//  def getDateoff():Array[String] =  {
//    val simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd")
//
//    val date1 = simpleDateFormat.parse("2021.12.21")
//    val date2 = simpleDateFormat.parse("2022.3.22")
//    val calender: Calendar = Calendar.getInstance()
//    val calender2: Calendar = Calendar.getInstance()
//    calender.setTime(date1)
//    calender2.setTime(date2)
//
//    var dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH) + 1}.${calender.get(Calendar.DATE)}"
//    val endateStr = "2022.3.22"
//    val dateArray: Array[String] = new Array[String](91)
//    var dayCount: Int = 0
//
//    while (!calender.equals(calender2)) {
//      dateStr = s"${calender.get(Calendar.YEAR)}.${calender.get(Calendar.MONTH) + 1}.${calender.get(Calendar.DATE)}"
//      //val str = calender.getTime.toString
//      //println(dateStr)
//      dateArray.update(dayCount, dateStr)
//      dayCount = dayCount + 1
//      calender.add(Calendar.DATE, 1)
//    }
//    dateArray.take(dayCount)
//  }
//  @Test
//  def testDWR(): Unit ={
//    //val path = "F:\\DynamicGraphStore"
//    val dataBaseDir = new File(path,"data")
//    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
//    registerShutdownHook(graphDb)
//
//
//    //println(dateVersion("2022.1.1"))
//
//   // graphDb.getNextVersions()
//    prepareData(graphDb)
//    //readDataShow(graphDb)
//    val versions:Array[Long] = graphDb.getNextVersions(dateArray.size)
//
//    val dateVersion = dateArray.toSeq.zip(versions.toSeq).toMap
//    prepareDataNewMola(graphDb,dateVersion)
//
//    val date: String = "2022.2.26"
//
//    readDataShow(graphDb,date,dateVersion(date))
//
//  }
//
//  @Test
//  def testLR(): Unit ={
//    val dataBaseDir = new File(path,"data")
//    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dataBaseDir)
//    registerShutdownHook(graphDb)
//    DataLoadAndRead.prepareData2(graphDb)
//    val date: String = "2022.2.26"
//    val str = DataLoadAndRead.getData(graphDb,date)
//    println(str)
//  }
//
//
//}
