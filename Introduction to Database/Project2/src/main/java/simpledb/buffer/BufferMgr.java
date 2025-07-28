package simpledb.buffer;

import simpledb.file.*;
import simpledb.log.LogMgr;
import java.util.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
public class BufferMgr {
   private Map<BlockId, Buffer> allocatedBuffers; 
   private List<Buffer> lruList;         
   private int numAvailable;   /* the number of available (unpinned) buffer slots */
   private int nextid;
   private static final long MAX_TIME = 10000; /* 10 seconds */
   /**
    * Constructor:  Creates a buffer manager having the specified 
    * number of buffer slots.
    * This constructor depends on a {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} object.
    * @param numbuffs the number of buffer slots to allocate
    */
   public BufferMgr(FileMgr fm, LogMgr lm, int numbuffs) {
      allocatedBuffers = new HashMap<>();
      lruList = new LinkedList<>();
      numAvailable = numbuffs;
      nextid = 0;
      for (int i=0; i<numbuffs; i++){
         Buffer buffer = new Buffer(fm, lm, nextid);
         lruList.add(buffer); // 초기에는 모두 list에 넣어줌
         nextid += 1;
      }
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   public synchronized int available() {
      return numAvailable;
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   public synchronized void flushAll(int txnum) {
      for (Buffer buff : allocatedBuffers.values())
         if (buff.modifyingTx() == txnum)
         buff.flush();
   }
   
   /**
    * Unpins the specified data buffer. If its pin count
    * goes to zero, then notify any waiting threads.
    * @param buff the buffer to be unpinned
    */
   public synchronized void unpin(Buffer buff) { // unpin되지만, lruList에서 위치 변화는 없음.
      buff.unpin(); // pincount 1 감소
      if (!buff.isPinned()) { // pincount == 0의 조건과 같음
         numAvailable++;
         notifyAll();
      }
   }
   
   /**
    * Pins a buffer to the specified block, potentially
    * waiting until a buffer becomes available.
    * If no buffer becomes available within a fixed 
    * time period, then a {@link BufferAbortException} is thrown.
    * @param blk a reference to a disk block
    * @return the buffer pinned to that block
    */
   public synchronized Buffer pin(BlockId blk) {
      try {
         long timestamp = System.currentTimeMillis();
         Buffer buff = tryToPin(blk);
         while (buff == null && !waitingTooLong(timestamp)) {
            wait(MAX_TIME);
            buff = tryToPin(blk);
         }
         if (buff == null)
            throw new BufferAbortException();
         return buff;
      }
      catch(InterruptedException e) {
         throw new BufferAbortException();
      }
   }  
   
   /**
    * Returns true if starttime is older than 10 seconds
    * @param starttime timestamp 
    * @return true if waited for more than 10 seconds
    */
   private boolean waitingTooLong(long starttime) {
      return System.currentTimeMillis() - starttime > MAX_TIME;
   }
   
   /**
    * Tries to pin a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   private Buffer tryToPin(BlockId blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         Iterator<Map.Entry<BlockId, Buffer>> iterator = allocatedBuffers.entrySet().iterator();
         while (iterator.hasNext()) { // 이미 allocatedBuffers에 value값으로 존재하는 버퍼면 allocatedBuffers에서 제거
            Map.Entry<BlockId, Buffer> entry = iterator.next();
            if (entry.getValue() == buff) {
               iterator.remove();
            }
         }
         buff.assignToBlock(blk);
         allocatedBuffers.put(blk, buff); // key : block으로 buffer를 mapping시킴
         lruList.remove(buff);
         lruList.add(buff);
      }
      else {
         lruList.remove(buff); // 이미 lruList에 존재하는 버퍼면 lruList에서 제거 후 end(newest)에 추가 => lruList 업데이트
         lruList.add(buff);
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Find and return a buffer assigned to the specified block. 
    * @param blk a reference to a disk block
    * @return the found buffer       
    */
   private Buffer findExistingBuffer(BlockId blk) {
      return allocatedBuffers.get(blk);
   }
   
   /**
    * Find and return an unpinned buffer. 
    * @return the unpinned buffer       
    */
   private Buffer chooseUnpinnedBuffer() {
      for (Buffer buff : lruList) {
         if (!buff.isPinned()) {
            return buff; // lruList의 첫 번째 unpinned 버퍼 반환
         }
      }
      return null;
   }
   
   public void printStatus() {
      System.out.println("Allocated Buffers:");
      allocatedBuffers.forEach((blockid, buffer) -> { // Map 데이터 전부에 대해 (blockid, buffer) = key value값으로 받아와 반복함
         String pincheck = buffer.isPinned() ? "pinned" : "unpinned"; // pinned 되어있는지 확인
         System.out.printf("Buffer %d: [%s] %s%n", buffer.getId(), blockid.toString(), pincheck); // 각 버퍼 상태 출력
      });
      System.out.print("Unpinned Buffers in LRU order: ");
      for (Buffer buff : lruList) {
         if (!buff.isPinned()) {
            System.out.print(buff.getId() + " "); // 앞 쪽(최근에 사용하지 않은)요소들부터 차례대로 출력함 => LRU
         }
      }
      // System.out.printf("%nall list :" );
      // for (Buffer buff : lruList)
      //    System.out.print(buff.getId() + " ");
      System.out.println();
   }
}
