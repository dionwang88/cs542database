package project;

/**
 * 
 * @author wangqian
 * 1. multi-threads can read the data simultaneously; do not lock the data
 * 2. when one writing thread starting write data at any time, other reading/writing thread should wait until that writing thread release the lock, 
 * 3. 
 * 4. 
 */
public class DbLocker {

	private int readingNum = 0; 
    private int writingNum = 0;  
      
    private int waitingNum = 0;   
    private boolean writerPriority = true;
	
    
    public DbLocker(){
    	
    }
    public int getwritingNum(){
    	return writingNum;
    }
    
    public int getwaitingNum(){
    	return waitingNum;
    }
    
    public int getreadingNum(){
    	return readingNum;
    }
    
    public synchronized void readLock() throws InterruptedException{  
        while(writingNum > 0 || (waitingNum > 0 && writerPriority)){  
            wait();  
        }  
        readingNum++;  
    }  
    
    public synchronized void readUnlock(){  
        readingNum--;  
        writerPriority = true;
        System.out.println("Writing Num: " + getwritingNum() + " Reading Num : " + getreadingNum() + " Waiting :" + getwaitingNum());
        notifyAll();  
    }
    
    public synchronized void writeLock() throws InterruptedException{  
        waitingNum++;  
        while(readingNum > 0 || writingNum >0 ){  
            try {  
                wait();  
            } finally {  
 
            }  
        }
        waitingNum--;
        writingNum++;  
    }
    
    public synchronized void writeUnlock(){  
        writingNum--;  
        writerPriority = false;
        System.out.println("Writing Num: " + getwritingNum() + " Reading Num : " + getreadingNum() + " Waiting :" + getwaitingNum());
        notifyAll();  
    }
}
