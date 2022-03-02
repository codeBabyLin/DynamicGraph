package cn.DynamicGraph.Common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.eclipse.collections.api.block.procedure.primitive.LongLongProcedure;
import org.eclipse.collections.api.block.procedure.primitive.LongProcedure;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.impl.map.mutable.primitive.LongLongHashMap;

public class Serialization {
    public static LongLongHashMap readMapFromByteArray(byte[] data){
        ByteBufAllocator allocator  = ByteBufAllocator.DEFAULT;
        ByteBuf byteBuf  = Unpooled.copiedBuffer(data);
        LongLongHashMap llMap = new LongLongHashMap();
        int size = byteBuf.readInt();
        for(int i =0 ;i<size;i++){
            llMap.put(byteBuf.readLong(),byteBuf.readLong());
        }
        return llMap;
    }

    public static byte[] writeMapToByteArray(LongLongHashMap llMap){

        ByteBufAllocator allocator  = ByteBufAllocator.DEFAULT;
        ByteBuf byteBuf  = allocator.heapBuffer();
        byteBuf.writeInt(llMap.size());

        //LongLongHashMap llMap = new LongLongHashMap();
        //int size = llMap.size();
        //byteBuf.writeInt(llMap.size());
        LongLongProcedure longLongProcedure = new LongLongProcedure() {
            @Override
            public void value(long l, long l1) {
                byteBuf.writeLong(l);
                byteBuf.writeLong(l1);
            }
        };
        llMap.forEachKeyValue(longLongProcedure);
        byte [] data = new byte[byteBuf.writerIndex()];
        byteBuf.readBytes(data);
        return data;
    }
}
