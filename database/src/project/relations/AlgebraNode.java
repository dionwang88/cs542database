package project.relations;

import java.util.List;

/**
 * Created by wangqian on 11/9/15.
 */
public interface AlgebraNode {
    void open();
    List getNext();
    void close();
}
