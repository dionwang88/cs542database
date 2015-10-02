package test;
import project.DBManager;
public class TestFragmentation {

	public static void main(String[] args) {
		DBManager dbmanager = DBManager.getInstance();
		byte[] bigdata = new byte[1024 * 1000];
		for(int i=0;i<bigdata.length;i++){
			bigdata[i] = (byte) ((i)%127);
		}
		byte[] smalldata = new byte[512 * 1000];
		for(int i=0;i<smalldata.length;i++){
			smalldata[i] = (byte) ((i)%127);
		}
		dbmanager.clear();
		for (int key = 0 ; key < 4; key ++ ) {
		dbmanager.Put(key, bigdata);
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		}
		dbmanager.Remove(1);
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		dbmanager.Put(4, smalldata);
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		System.out.println("Try to save data key is:" + 5 + ", size is:" + bigdata.length);
		dbmanager.Put(5, bigdata); // Failure Expected.
		dbmanager.Remove(2);
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		dbmanager.Put(6, bigdata); // Should succeed
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		dbmanager.Remove(4);
		System.out.println("Current Space left is " + dbmanager.getfreespace());
		dbmanager.Put(7, bigdata); // Should succeed
		System.out.println("Current Space left is " + dbmanager.getfreespace());

	}

}
