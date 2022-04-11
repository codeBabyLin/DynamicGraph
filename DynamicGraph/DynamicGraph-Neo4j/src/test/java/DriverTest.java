//import org.junit.Test;
//import org.neo4j.driver.v1.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DriverTest {
//
//    @Test
//    public void testCoonection() {
//        Driver driver = GraphDatabase.driver("bolt://localhost:7687");
//        Session s = driver.session();
//        Transaction tx = s.beginTransaction();
//
//        String cy = "hello world";
//        //val map: Map[String,Object] = new Map[String,Object]()
//        Map<String,Object> para = new HashMap<>();
//        para.put("version",123);
//        StatementResult res = tx.run(cy,para);
//
//        tx.success();
//        tx.close();
//        //println(res)
//
//
//    }
//
//}
