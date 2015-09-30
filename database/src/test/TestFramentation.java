package test;
import project.DBManager;
public class TestFramentation {

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
		}
		dbmanager.Remove(1);
		dbmanager.Put(4, smalldata);
		dbmanager.Put(5, bigdata); // Failure Expected.
		dbmanager.Remove(2);
		dbmanager.Put(6, bigdata); // Should succeed
		dbmanager.Remove(4);
		dbmanager.Put(7, bigdata); // Should succeed

	}

}
