package test;

import project.DBManager;

public class TestConcurrency {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager dbmanager = DBManager.getInstance();
		dbmanager.clear();
		byte[] data = new byte[10];
		for(int i=0;i<data.length;i++){
			data[i] = (byte) ((i)%127);
		}
		byte[] data2 = new byte[10];
		for(int i=0;i<data.length;i++){
			data2[i] = (byte) ((5*i)%127);
		}
		dbmanager.Put(11, data);
		System.out.println("Data : " + Threadtest.byteArrayToInt(data) + " , Data 2: " 
				+ Threadtest.byteArrayToInt(data2));
		Threadtest t1 = new Threadtest(11, data, dbmanager);
		Threadtest t2 = new Threadtest(11, data2, dbmanager);
		Threadtest t3 = new Threadtest(11, data, dbmanager);
		String[] task1 = {"read","write"};
		t1.addtask(task1);
		String[] task3 = {"remove","write"};
		t3.addtask(task3);
		String[] task2 = {"write","read"};
		t2.addtask(task2);
		Thread T1 = new Thread(t1);
		Thread T2 = new Thread(t2);
		Thread T3 = new Thread(t3);
		T1.start();
		T2.start();
		T3.start();

	}

}
