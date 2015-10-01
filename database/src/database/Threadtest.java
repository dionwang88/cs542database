package database;
import java.util.LinkedList;
import project.DbLocker;
import java.util.Iterator;
public class Threadtest implements Runnable{
	String name;
	int num;
	LinkedList<String> todolist;
	private DbLocker locker;
	public void run(){
		Iterator<String> iterator = todolist.iterator();
		synchronized(todolist) {
			while (iterator.hasNext()) {
				String str = iterator.next();
				if (todolist.contains(str)) {
					iterator.remove();
				}
				if (str == "write") {
					write();
				} else if (str == "read") {
					read();
				} else {
					continue;
				}
			}
		}
		
	}
	public void addtask(String[] tasks){
		for (String task : tasks) {
			todolist.add(task);
		}
	}
	public Threadtest(String name, int num, DbLocker locker){
		this.name = name;
		this.num = num;
		todolist = new LinkedList<String>();
		this.locker = locker;
	}
	private void write(){
		try {
			locker.writeLock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Thread " + name + " is writing data:" + num);
		try {
			locker.writeUnlock();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void read(){
		try {
			locker.ReadLock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Thread " + name + " is reading.");
		locker.ReadUnlock();
	}
}
