package test;
import project.index.AttrIndex;
public class TestHashIndex {
	public static void main(String[] args) {
		AttrIndex<String> h = new AttrIndex<String>();
		h.put(1, "A");
		h.put(2, "B");
		h.put(3, "A");
		h.put(4, "D");
		h.put(5, "C");
		h.put(6, "C");
		h.put(7, "F");
		System.out.println(h);
	}

}
