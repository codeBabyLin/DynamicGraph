import cn.DynamicGraph.Common.BaseSerializer;
import cn.DynamicGraph.Common.Serialization;
import cn.DynamicGraph.kernel.impl.store.DbVersionStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;
import org.neo4j.kernel.impl.factory.GraphDatabaseFacade;

import java.util.HashMap;
import java.util.Map;

public class myTest {

    @Test
    public void test(){
        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

        ByteBuf byteBuf = allocator.buffer();
        int ak = 128;

        byteBuf.writeByte(ak);

        int mk = byteBuf.readByte();
        System.out.println(mk);

        //val allocator = ByteBufAllocator.DEFAULT;
        //val byteBuf = allocator.buffer()
        //val sds: Int = -127
       // byteBuf.writeByte(sds)
        //val hj = byteBuf.readByte().toInt
        //println(hj)
    }
    @Test
    public void testjk(){
        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

        ByteBuf byteBuf = allocator.buffer();
        String str = "中国 第一";
       // str.getBytes();
        BaseSerializer._writeStringEx(str,byteBuf);
        byteBuf.readByte();
        String str1 = BaseSerializer._readStringEx(byteBuf);
        System.out.println(str1);
    }

    @Test
    public void testgh(){
        //Object jk = "skdlsdkl";
        //System.out.println(jk instanceof Integer);
        Map<Integer,Object> data = new HashMap<>();
        data.put(1,"hah");
        data.put(2,100);
        data.put(3, new boolean[]{true, false, true});
        byte[] bt = Serialization.writeMapToByteArray(data);
        Map<Integer,Object> x = Serialization.readJMapFromByteArray(bt);
        Object nk = x;
        Class zc = DbVersionStore.class;


    }

    @Test
    public void testkl(){
       /* boolean x = ({
                boolean z = true;
                boolean y = false;
                boolean c = true;
                })*/
    }

    @Test
    public void testnm(){
        GraphDatabaseFacade hj;
    }

}
