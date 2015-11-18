package project;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
/**
 *
 * @author XiangHuang
 * 1. Threads can only be granted read access if no other threads has 
 *    write accesses or in request of it.
 * 2. Threads can be granted a write access when no threads are reading
 *    or writing the database.
 */
public class DbLocker {

	Logger logger = (Logger) LogManager.getLogger();

	/**
	 *  Map for keeping track of threads that have single or
	 *  multiple reading accesses.
	 */
	private Map<Thread, Integer> readingThreads =
			new HashMap<>();

	/**
	 * Number of currently writing accesses of one thread. 
	 * Maintained to ensure Write-Write Re-entrance. 
	 */
	private int writeCount = 0;
	//Number of Writing requests
	private int writeRequests = 0;
	//Thread that is writing the data.
	private Thread writingThread = null;

	/**
	 * Grant read access to the current thread.
	 * @throws InterruptedException
	 */
	public synchronized void ReadLock() throws InterruptedException{
		Thread callingThread = Thread.currentThread();
		while(! canGrantReadAccess(callingThread)){
			wait();
		}
		logger.info(callingThread + " is locking readlock.");
		//Updating the readingThreads map.
		readingThreads.put(callingThread,
				(getReadCount(callingThread) + 1));
	}
	/**
	 * Determines whether a thread can read.
	 * Granted if:
	 * 		1.It is the current writing thread
	 * 		2.It is granted read access already to ensure 
	 * 		Read-Read Re-entrance.
	 * Denied if :
	 * 		1.Other thread is writing.
	 * 		2.There are threads waiting to write.
	 * @param callingThread : Thread under consideration.
	 * @return true or false
	 */
	private boolean canGrantReadAccess(Thread callingThread) {
		return isWriter(callingThread) || !hasWriter() && (isReader(callingThread) || !hasWriteRequests());
	}

	/**
	 * Unlocks the ReadLock.
	 */
	public synchronized void ReadUnlock(){
		Thread callingThread = Thread.currentThread();
		if(!isReader(callingThread)){
			throw new IllegalMonitorStateException("Current Thread does not" +
					" hold a read lock.");
		}
		//Updating the readingThreads map.
		int readCount = getReadCount(callingThread);
		if(readCount == 1){
			readingThreads.remove(callingThread);
		} else {
			readingThreads.put(callingThread, (readCount -1));
		}
		logger.info(callingThread + " is unlocking readlock.");
		notifyAll();
	}

	/**
	 * Grant write access to the current thread.
	 * @throws InterruptedException
	 */
	public synchronized void writeLock() throws InterruptedException{
		// Added the number of threads requesting write.
		writeRequests++;
		Thread callingThread = Thread.currentThread();
		//If cannot be granted writeLock, wait.
		while(! canWrite(callingThread)){
			wait();
		}
		logger.info(Thread.currentThread() + " is locking writelock.");
		writeRequests--;
		writeCount++;
		writingThread = callingThread;
	}

	/**
	 * Unlocks the writeLock.
	 * @throws InterruptedException
	 */
	public synchronized void writeUnlock() throws InterruptedException{
		if(!isWriter(Thread.currentThread())){
			throw new IllegalMonitorStateException("Calling Thread does not" +
					" hold the write lock on this ReadWriteLock");
		}
		writeCount--;
		logger.info(Thread.currentThread() + " is unlocking writelock.");
		/*
		 *  Set current writing thread to null so other 
		 *  write threads can be granted access.
		 */
		if(writeCount == 0){
			writingThread = null;
		}
		notifyAll();
	}

	/**
	 * Determines whether a thread can write.
	 * Granted if:
	 * 		1.It is the only thread reading the data.
	 * 		2.No thread is writing.
	 * Denied if :
	 * 		1.This thread is not the current writing thread.
	 * 		2.Has readers other than the current thread.
	 * @param callingThread : Thread under consideration.
	 * @return true of false
	 */
	private boolean canWrite(Thread callingThread) {
		return isOnlyReader(callingThread) || !hasReaders() && (writingThread == null || isWriter(callingThread));
	}

	/**
	 * Returns the number of read accesses of the current thread.
	 * @param callingThread :Thread under consideration
	 * @return read counter
	 */
	private int getReadCount(Thread callingThread){
		Integer Count = readingThreads.get(callingThread);
		if(Count == null) return 0;
		return Count;
	}

	/**
	 * Returns if there are threads currently reading.
	 * @return true or false
	 */
	private boolean hasReaders(){return readingThreads.size() > 0;}

	/**
	 * Returns if the thread is reading.
	 * @param callingThread : Thread under consideration.
	 * @return true or false
	 */
	private boolean isReader(Thread callingThread){
		return readingThreads.get(callingThread) != null;
	}

	/**
	 * Determines if this thread is the only one that is reading.
	 * For Implementation of Read-to-Write Re-entrance.
	 * @param callingThread : Thread under consideration
	 * @return true or false
	 */
	private boolean isOnlyReader(Thread callingThread){
		return readingThreads.size() == 1 &&
				readingThreads.get(callingThread) != null;
	}

	/**
	 * Determines if there are any threads writing.
	 * @return true or false
	 */
	private boolean hasWriter(){
		return writingThread != null;
	}

	/**
	 * Determines if the thread is writing.
	 * @param callingThread : Thread under consideration.
	 * @return true or false
	 */
	private boolean isWriter(Thread callingThread){
		return writingThread == callingThread;
	}

	/**
	 * Determines if there are writing requests.
	 * @return true or false
	 */
	private boolean hasWriteRequests(){
		return this.writeRequests > 0;
	}

}