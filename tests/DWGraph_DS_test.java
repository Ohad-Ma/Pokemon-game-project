import api.directed_weighted_graph;
import api.node_data;
import gameClient.DWGraph_DS;
import gameClient.NodeData;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.lang.IllegalArgumentException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DWGraph_DS tests
 *
 * @author Ohad
 */
public class DWGraph_DS_test {
    /**
     Add 20 nodes -> verify size -> verify existence
     */
    @Test
    public void add() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i<20 ;++i){
            graph.addNode(new NodeData(i));
        }

        assertEquals(20, graph.nodeSize());

        for (int i = 0; i<20; ++i){
            assertEquals(i,graph.getNode(i).getKey());
        }

    }

    /**
     Add 20 nodes -> remove 5 -> verify size
     */
    @Test
    public void add_remove_size() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i<20 ;++i){
            graph.addNode(new NodeData(i));
        }

        graph.removeNode(3);
        graph.removeNode(5);
        graph.removeNode(2);
        graph.removeNode(17);
        graph.removeNode(14);

        assertEquals(15, graph.nodeSize());

    }

    /**
     Add 20 nodes -> connect some -> verify edge size
     */
    @Test
    public void edge_connect_size() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i<20 ;++i){
            node_data a = new NodeData(i);
            graph.addNode(a);
        }
        graph.connect(3, 5, 20);
        graph.connect(3, 17, 10);
        graph.connect(2, 10, 5);
        graph.connect(5, 14, 1);

        assertEquals(4, graph.edgeSize());

    }

    /**
     Add nodes -> connect some -> remove some -> verify edge size
     */
    @Test
    public void edge_remove_size() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i<5;++i){
            graph.addNode(new NodeData(i));
        }

        graph.connect(1, 3, 20);
        graph.connect(1, 4, 10);
        graph.connect(2, 3, 5);
        graph.connect(3, 4, 1);


        graph.removeEdge(2, 10);
        graph.removeEdge(5, 14);
        graph.removeEdge(30, 35);

        assertEquals(4, graph.edgeSize());

    }

    /**
     Add 20 nodes -> connect some -> verify they exist
     */
    @Test
    public void edge_verify() {
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i<20 ;++i){
            node_data a = new NodeData(i);
            graph.addNode(a);
        }
        graph.connect(3, 5, 20);
        graph.connect(3, 17, 10);
        graph.connect(2, 10, 5);
        graph.connect(5, 14, 1);


        assertEquals(2, graph.getE(3).size());
        assertEquals(1, graph.getE(2).size());
        assertEquals(1, graph.getE(5).size());


    }

    /**
     * create a graph -> add 4 nodes -> verify they exist on a list given from getV function
     */
    @Test
    void getV() {
        directed_weighted_graph g = new DWGraph_DS();
        node_data a = new NodeData(0);
        node_data b = new NodeData(1);
        node_data c = new NodeData(2);
        node_data d = new NodeData(3);
        g.addNode(a);
        g.addNode(b);
        g.addNode(c);
        g.addNode(d);
        Collection<node_data> v = g.getV();
        Iterator<node_data> iter = v.iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            assertNotNull(n);
        }
    }

    /**
     * MC test, although wasn't instructed to do so. Even tho they ripped points:
     *
     * make a graph
     * add 24 nodes
     * verify the MC has changed
     * remove even nodes
     * verify the MC has changed
     * connect 7 nodes
     * verify the MC changed
     * remove 2 edges
     * verify the MC changed
     */
    @Test
    void MC(){
        directed_weighted_graph g = new DWGraph_DS();
        int times = 0;
        for (int i = 0; i<23 ; ++i) {
            g.addNode(new NodeData(i));
            times++;
        }
        assertEquals(times, g.getMC());

        for (int i = 0; i<23; ++i)
        {
            if(i%2 == 0) {
                g.removeNode(i);
                times++;
            }
        }
        assertEquals(times, g.getMC());
        g.connect(1,3,10);
        times++;
        g.connect(3,5,10);
        times++;
        g.connect(5,7,10);
        times++;
        g.connect(7,9,10);
        times++;
        g.connect(9,11,10);
        times++;
        g.connect(1,5,10);
        times++;
        g.connect(1,11,10);
        times++;

        assertEquals(times, g.getMC());


        g.removeEdge(9, 11);
        times++;
        g.removeEdge(1, 11);
        times++;

        assertEquals(times, g.getMC());
    }

    /**
     * empty cases tests:
     * - make a graph
     * - add a null node
     * - verify there are no nodes
     * - remove a node that does not exist
     * - remove an edge that does not exist
     * - get a list of connection with a node that does not exist
     */
    @Test
    public void empty_cases(){
        directed_weighted_graph g = new DWGraph_DS();
        node_data a = null;
        g.addNode(a);
        assertEquals(0, g.nodeSize());

        assertNull(g.removeNode(9));

        assertNull(g.removeEdge(10, 11));

        assertEquals(Collections.emptyList(), g.getE(1));

    }

    /**
     * 'Connect' method test:
     *  - make a graph -> add 2 nodes
     *  - connect them with negative weight
     *  - verify they didn't connect and the edge is null
     *  - connect exist node with another that does not exist
     *  - verify edge is null
     *  - connect the two nodes from step 1 with positive weight
     *  - verify they exist
     *  - verify the weight existence
     *  - remove the edge
     *  - verify it does not exist
     *  - verify the weight does not exist
     */
    @Test
    public void connect_test(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(3));
        g.addNode(new NodeData(4));
        g.connect(3, 4, -1);

        assertNull(g.getEdge(3, 4));

        g.connect(3, 5, 2);
        assertNull(g.getEdge(3, 5));

        g.connect(3, 4, 2);
        assertNotNull(g.getEdge(3, 4));
        assertEquals(g.getEdge(3, 4).getWeight(), 2);

        g.removeEdge(3, 4);
        assertNull(g.getEdge(3, 4));


        Throwable exception1 = assertThrows(NullPointerException.class,() -> g.getEdge(3, 4).getWeight() );
        assertNull(exception1.getMessage());


    }




}