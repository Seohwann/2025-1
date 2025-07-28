package simpledb.tx.concurrency;

import java.util.*;
import simpledb.file.BlockId;

/**
 * The lock table, which provides methods to lock and unlock blocks.
 * If a transaction requests a lock that causes a conflict with an
 * existing lock, then that transaction is placed on a wait list.
 * There is only one wait list for all blocks.
 * When the last lock on a block is unlocked, then all transactions
 * are removed from the wait list and rescheduled.
 * If one of those transactions discovers that the lock it is waiting for
 * is still locked, it will place itself back on the wait list.
 * @author Edward Sciore
 */
class LockTable {
   private static final long MAX_TIME = 10000; // 10 seconds
   private Map<BlockId, List<Integer>> locks = new HashMap<>();

   
   /**
    * Grant an SLock on the specified block.
    * If an XLock exists when the method is called,
    * then the calling thread will be placed on a wait list
    * until the lock is released.
    * If the thread remains on the wait list for a certain 
    * amount of time (currently 10 seconds),
    * then an exception is thrown.
    * @param blk a reference to the disk block
    */
   public synchronized void sLock(BlockId blk, int txnum) {
      try {
         if (hasXlock(blk, txnum) && Abort(blk, txnum)) {
            throw new LockAbortException(); // 즉시 중단
         }
         long timestamp = System.currentTimeMillis();
         while (hasXlock(blk, txnum) && !waitingTooLong(timestamp)){
            if (Abort(blk, txnum)) { // abort 되어야 한다면 LockAbortException 에러 throw
               throw new LockAbortException();
            }
            wait(MAX_TIME);            
         }
         if (hasXlock(blk, txnum)) // double check
            throw new LockAbortException();
         List<Integer> lockList = locks.getOrDefault(blk, new ArrayList<>());
         lockList.add(txnum);
         locks.put(blk, lockList);
      }
      catch(InterruptedException e) {
         throw new LockAbortException();
      }
   }
   
   /**
    * Grant an XLock on the specified block.
    * If a lock of any type exists when the method is called,
    * then the calling thread will be placed on a wait list
    * until the locks are released.
    * If the thread remains on the wait list for a certain 
    * amount of time (currently 10 seconds),
    * then an exception is thrown.
    * @param blk a reference to the disk block
    */
   synchronized void xLock(BlockId blk, int txnum) {
      try {
         if (hasOtherLocks(blk, txnum) && Abort(blk, txnum)) {
            throw new LockAbortException(); // 즉시 중단
         }
         long timestamp = System.currentTimeMillis();
         while (hasOtherLocks(blk, txnum) && !waitingTooLong(timestamp)){
            if (Abort(blk, txnum)) { // abort 되어야 한다면 LockAbortException 에러 throw
               throw new LockAbortException();
            }
            wait(MAX_TIME);            
         }
         if (hasOtherLocks(blk, txnum)) // double check
            throw new LockAbortException();
         List<Integer> lockList = new ArrayList<>();
         lockList.add(-txnum);
         locks.put(blk, lockList);
      }
      catch(InterruptedException e) {
         throw new LockAbortException();
      }
   }
   
   /**
    * Release a lock on the specified block.
    * If this lock is the last lock on that block,
    * then the waiting transactions are notified.
    * @param blk a reference to the disk block
    */
   synchronized void unlock(BlockId blk, int txnum) {
      List<Integer> lockList = locks.get(blk);
      if (lockList != null) {
         lockList.remove(Integer.valueOf(txnum));
         if (lockList.isEmpty()) { // lock을 쥐고 있는 트랜잭션이 모두 없어짐 -> wait중인 트랜잭션을 깨움
            locks.remove(blk);
            notifyAll();
         }
      }
   }
   
   private boolean hasXlock(BlockId blk, int txnum) {
      List<Integer> lockList = locks.get(blk);
      if (lockList == null || lockList.isEmpty()) {
         return false;
      }
      boolean flag = true;
      if (lockList.get(0) < 0) { // xLock 확인
         int id = lockList.get(0);
         if (id == txnum || id == -txnum) { // tx 자기 자신이 lock을 갖고 있는지 확인
            flag = false;
         }
         else{
            flag = true;
         }      
      } else {
         flag = false;
      }
      return flag;
   }
   
   private boolean hasOtherLocks(BlockId blk, int txnum) {
      List<Integer> lockList = locks.get(blk);
      if (lockList == null || lockList.isEmpty()) {
         return false;
      }
      boolean flag = true;
      for (Integer id : lockList) {
         if (id == txnum || id == -txnum) {  // tx 자기 자신이 lock을 갖고 있는지 확인
            flag = false;
            continue;
         }
         else{
            flag = true;
            break;
         }      
      }
      return flag;
   }
   
   private boolean waitingTooLong(long starttime) {
      return System.currentTimeMillis() - starttime > MAX_TIME;
   }
   
   private boolean Abort(BlockId blk, int txnum) {
      List<Integer> lockList = locks.get(blk);
      if (lockList != null) {
         int abslocktx;
         for (int locktx : lockList) {
            if (locktx >= 0)
               abslocktx = locktx;
            else
               abslocktx = -locktx;
            if (abslocktx != txnum){
               if (abslocktx < txnum) // txnum을 시간 순서로 오름차순으로 부여했기에 절댓값끼리의 비교를 통해 계산 가능
                  return true; // 더 오래된 트랜잭션이 잠금을 가지면 중단(abort)
            }
         }
      }
      return false; // 대기(wait)
   }
}
