package project;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
/**
 * 
 * @author wangqian
 * 1. multi-threads can read the data simultaneously; do not lock the data
 * 2. when one writing thread starting write data at any time, other reading/writing thread should wait until that writing thread release the lock, 
 * 3. 
 * 4. 
 */
public class DbLocker {
	

	Logger logger = (Logger) LogManager.getLogger();
	
	private Map<Thread, Integer> readingThreads =
		       new HashMap<Thread, Integer>();

	private int writeAccesses    = 0;
	private int writeRequests    = 0;
	private Thread writingThread = null;


		  public synchronized void ReadLock() throws InterruptedException{
		    Thread callingThread = Thread.currentThread();
		    while(! canGrantReadAccess(callingThread)){
		      wait();
		    }
		    logger.info(Thread.currentThread() + " is locking readlock.");

		    readingThreads.put(callingThread,
		     (getReadAccessCount(callingThread) + 1));
		  }

		  private boolean canGrantReadAccess(Thread callingThread){
		    if( isWriter(callingThread) ) return true;
		    if( hasWriter()             ) return false;
		    if( isReader(callingThread) ) return true;
		    if( hasWriteRequests()      ) return false;
		    return true;
		  }


		  public synchronized void ReadUnlock(){
		    Thread callingThread = Thread.currentThread();
		    if(!isReader(callingThread)){
		      throw new IllegalMonitorStateException("Calling Thread does not" +
		        " hold a read lock on this ReadWriteLock");
		    }
		    int accessCount = getReadAccessCount(callingThread);
		    if(accessCount == 1){ readingThreads.remove(callingThread); }
		    else { readingThreads.put(callingThread, (accessCount -1)); }
		    logger.info(callingThread + " is unlocking readlock.");
		    notifyAll();
		  }

		  public synchronized void writeLock() throws InterruptedException{
		    writeRequests++;
		    Thread callingThread = Thread.currentThread();
		    while(! canGrantWriteAccess(callingThread)){
		      wait();
		    }
		    logger.info(Thread.currentThread() + " is locking writelock.");
		    writeRequests--;
		    writeAccesses++;
		    writingThread = callingThread;
		  }

		  public synchronized void writeUnlock() throws InterruptedException{
		    if(!isWriter(Thread.currentThread())){
		      throw new IllegalMonitorStateException("Calling Thread does not" +
		        " hold the write lock on this ReadWriteLock");
		    }
		    writeAccesses--;
		    logger.info(Thread.currentThread() + " is unlocking writelock.");
		    if(writeAccesses == 0){
		      writingThread = null;
		    }
		    notifyAll();
		  }

		  private boolean canGrantWriteAccess(Thread callingThread){
		    if(isOnlyReader(callingThread))    return true;
		    if(hasReaders())                   return false;
		    if(writingThread == null)          return true;
		    if(!isWriter(callingThread))       return false;
		    return true;
		  }


		  private int getReadAccessCount(Thread callingThread){
		    Integer accessCount = readingThreads.get(callingThread);
		    if(accessCount == null) return 0;
		    return accessCount.intValue();
		  }


		  private boolean hasReaders(){
		    return readingThreads.size() > 0;
		  }

		  private boolean isReader(Thread callingThread){
		    return readingThreads.get(callingThread) != null;
		  }

		  private boolean isOnlyReader(Thread callingThread){
		    return readingThreads.size() == 1 &&
		           readingThreads.get(callingThread) != null;
		  }

		  private boolean hasWriter(){
		    return writingThread != null;
		  }

		  private boolean isWriter(Thread callingThread){
		    return writingThread == callingThread;
		  }

		  private boolean hasWriteRequests(){
		      return this.writeRequests > 0;
		  }

}