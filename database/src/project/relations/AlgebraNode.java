package project.relations;

import project.Pair;
import java.util.List;
import java.util.Iterator;
/**
 * Created by wangqian on 11/9/15.
 */
public interface AlgebraNode{

    void open();
    List<Pair<Integer,Integer>> getNext();
    void close();
}
