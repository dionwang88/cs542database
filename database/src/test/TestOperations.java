package test;
import project.DbLocker;
public class TestOperations {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DbLocker dbl = new DbLocker();
		Threadtest t1 = new Threadtest("A", 9 , dbl);
		Threadtest t2 = new Threadtest("B", 1 , dbl);
		Threadtest t3 = new Threadtest("C", 1 , dbl);
		Threadtest t4 = new Threadtest("D", 1 , dbl);
		Threadtest t5 = new Threadtest("E", 1 , dbl);
		Threadtest t6 = new Threadtest("F", 1 , dbl);
		
		String[] task1 = {"read", "write","write","write"};
		t1.addtask(task1);
		String[] task2 = {"write", "read", "write"};
		t2.addtask(task2);
		t3.addtask(task1);
		t4.addtask(task2);
		t5.addtask(task1);
		t6.addtask(task2);
		Thread T1 = new Thread(t1);
		Thread T2 = new Thread(t2);
		Thread T3 = new Thread(t3);
		Thread T4 = new Thread(t4);
		Thread T5 = new Thread(t5);
		Thread T6 = new Thread(t6);
		T2.start();
		T1.start();
		T3.start();
		T4.start();
		T5.start();
		T6.start();
		
	}

}
