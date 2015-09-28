package test;
import project.DbLocker;
public class TestOperations {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DbLocker dbl = new DbLocker();
		Threadtest t1 = new Threadtest("A", 9 , dbl);
		Threadtest t2 = new Threadtest("B", 1 , dbl);
		String[] task1 = {"read", "write","write","write"};
		t1.addtask(task1);
		String[] task2 = {"write", "read", "write"};
		t2.addtask(task2);
		Thread T1 = new Thread(t1);
		Thread T2 = new Thread(t2);
		T2.start();
		T1.start();
	}

}
