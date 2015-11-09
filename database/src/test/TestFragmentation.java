package test;
import project.DBManager;
import project.dbtool.DBTool;

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
		DBTool.showWrapped(dbmanager);
		for (int key = 0 ; key < 4; key ++ ) {
			dbmanager.Put(key, bigdata);
			DBTool.showWrapped(dbmanager);
		}
		dbmanager.Remove(1);
		DBTool.showWrapped(dbmanager);

		dbmanager.Put(4, smalldata);
		DBTool.showWrapped(dbmanager);

		dbmanager.Put(5, bigdata); // Failure Expected.
		DBTool.showWrapped(dbmanager);

		dbmanager.Remove(2);
		DBTool.showWrapped(dbmanager);

		dbmanager.Put(6, bigdata); // Should succeed
		DBTool.showWrapped(dbmanager);

		dbmanager.Remove(4);
		DBTool.showWrapped(dbmanager);

		dbmanager.Put(7, bigdata); // Should succeed
		DBTool.showWrapped(dbmanager);
	}

}
