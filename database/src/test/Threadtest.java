package test;
import java.util.LinkedList;
import java.util.Iterator;

import project.DBManager;
public class Threadtest implements Runnable{
	int num;
	byte[] data;
	LinkedList<String> todolist;
	DBManager db;
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
				} else if (str == "remove") {
					remove();
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
	public Threadtest(int num, byte[] data, DBManager db){
		this.num = num;
		this.data = data;
		todolist = new LinkedList<String>();
		this.db = db;
	}
	private void write(){
		System.out.println(Thread.currentThread() + " is waiting to write data:" + Threadtest.byteArrayToInt(data));
		db.Put(num, data);
	}
	private void remove(){
		System.out.println(Thread.currentThread() + " is waiting to remove data.");
		db.Remove(num);
	}
	private void read(){
		System.out.println(Thread.currentThread() + " is waiting to read data.");
		byte[] data = db.Get(num);
		if (data == null) System.out.println("To Client: " + Thread.currentThread() + " got no data.");
		else System.out.println(Thread.currentThread() + " got " + Threadtest.byteArrayToInt(data));
	}
public static int byteArrayToInt(byte[] b) 
{
    int value = 0;
    for (int i = 0; i < 4; i++) {
        int shift = (4 - 1 - i) * 8;
        value += (b[i] & 0x000000FF) << shift;
    }
    return value;
}
}
