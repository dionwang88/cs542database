package project.relations;

import project.Pair;
import java.util.List;

public interface AlgebraNode{
    void open();
    List<Pair<Integer,Integer>> getNext();
    void close();
}
