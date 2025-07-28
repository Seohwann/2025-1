package simpledb.buffer;

import simpledb.server.SimpleDB;
import simpledb.file.*;
import java.util.*;

public class TestBufMgr {
   private static Map<BlockId,Buffer> buffs = new HashMap<>();
   private static BufferMgr bm;
   
   public static void main(String args[]) throws Exception {
      SimpleDB db = new SimpleDB("buffermgrtest", 400, 8); 
      bm = db.bufferMgr();    
      pinBuffer(0); pinBuffer(1); pinBuffer(2); pinBuffer(3);
      pinBuffer(4); pinBuffer(5); pinBuffer(6); pinBuffer(7);
      bm.printStatus();
      unpinBuffer(2); unpinBuffer(0); unpinBuffer(5); unpinBuffer(4);
      bm.printStatus();
      pinBuffer(8); pinBuffer(5); pinBuffer(7);
      bm.printStatus();
   }

// (oldest) 0 1 2 3 4 5 6 7 (latest)
//  not used 2, 0, 5, 4
// 8
// (oldest) 1 2 3 4 5 6 7 8 (latest)
//  not used 2, 5, 4
// 5
// (oldest) 1 2 3 4 6 7 8 5 (latest)
//  not used 2, 4
// 7
// (oldest) 1 2 3 4 6 8 5 7 (latest)
   
   private static void pinBuffer(int i) {
      BlockId blk = new BlockId("test", i);
      Buffer buff = bm.pin(blk);
      buffs.put(blk, buff);
      System.out.println("Pin block " + i);
   }
   
   private static void unpinBuffer(int i) {
      BlockId blk = new BlockId("test", i);
      Buffer buff = buffs.remove(blk);
      bm.unpin(buff);
      System.out.println("Unpin block " + i);
   }
}
