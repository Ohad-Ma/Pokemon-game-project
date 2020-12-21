import api.*;
import gameClient.CL_Pokemon;
import gameClient.DWGraph_Algo;
import gameClient.DWGraph_DS;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CL_PokemonTest {


    /*
     * make a pokemon with a specific value
     * verify the pokemon indeed holds the value
     * make the pokemon hold a new value
     * verify the old value does not exist
     * verify new value exists
     */
    @Test
    void setGetValueTest() {
        CL_Pokemon pkmn = new CL_Pokemon(null, 1, 5, 0,null);
        assertEquals(5, pkmn.getValue());
        pkmn = new CL_Pokemon(null, 1, 9, 0, null);
        assertNotEquals(5, pkmn.getValue());
        assertEquals(9, pkmn.getValue());
    }

    /*
     * Make a graph
     * load one of the given json files (saves dirty work of making new random graph)
     * make a random point
     * make a pokemon with a specific value that receives the point step above
     * verify the pokemon indeed holds the the edge
     * make the pokemon hold a new edge
     * verify the old value does not exist
     * verify new value exists
     * set another edge that exists on the graph
     * verify the pokemon holds it
     * make the pokemon get an edge that does not exists on the graph
     * verify it hold null and the edge does not exists as well
     */
    @Test
    void setGetEdgeTest() {
        dw_graph_algorithms algo = new DWGraph_Algo();
        directed_weighted_graph graph = new DWGraph_DS();
        algo.init(graph);
        algo.load("data\\A4.json");
        Point3D point = new Point3D(graph.getNode(9).getLocation().x(),graph.getNode(9).getLocation().y(),0);
        CL_Pokemon pkmn = new CL_Pokemon(point,1,2,0, graph.getEdge(6,15));
        assertEquals(graph.getEdge(6,15), pkmn.get_edge());
        pkmn.set_edge(graph.getEdge(1,2));
        assertNotEquals(graph.getEdge(6,15), pkmn.get_edge());
        assertEquals(graph.getEdge(1,2), pkmn.get_edge());
        pkmn.set_edge(graph.getEdge(3,2));
        assertEquals(graph.getEdge(3,2), pkmn.get_edge());
        pkmn.set_edge(graph.getEdge(3,10));
        assertNull(pkmn.get_edge());
        assertNull(graph.getEdge(3,10));
    }

/*
 * Make a graph
 * load one of the given json files (saves dirty work of making new random graph)
 * make 3 random points: two that exist one that's a null
 * make a pokemon with a specific value that receives the first point from step above
 * verify the pokemon indeed holds the the point
 * verify the pokemon doesn't hold the second point
 * make the pokemon hold a new point
 * verify the old value does not exist
 * verify new value exists
 * make the pokemon hold the null point
 * verify the pokemon holds it
 */
    @Test
    void setGetLocationTest() {
        dw_graph_algorithms algo = new DWGraph_Algo();
        directed_weighted_graph graph = new DWGraph_DS();
        algo.init(graph);
        algo.load("data\\A4.json");
        Point3D point = new Point3D(graph.getNode(9).getLocation().x(),graph.getNode(9).getLocation().y(),0);
        Point3D point2 = new Point3D(graph.getNode(38).getLocation().x(),graph.getNode(38).getLocation().y(),0);
        Point3D point3 = null;
        CL_Pokemon pkmn = new CL_Pokemon(point,1,2,0, graph.getEdge(6,15));
        assertEquals(point, pkmn.getLocation());
        assertNotEquals(point2, pkmn.getLocation());
        pkmn = new CL_Pokemon(point2, 1, 2, 0, graph.getEdge(6, 15));
        assertEquals(point2, pkmn.getLocation());
        assertNotEquals(point, pkmn.getLocation());
        pkmn = new CL_Pokemon(point3, 1, 2, 0, graph.getEdge(6, 15));
        assertNull(pkmn.getLocation());
    }

    /*
    * make a new pokemon
    * verify its type
    * change the pokemon type
    * verify the pokemon does not hold old type
    * verify the pokemon holds the new type
     */
    @Test
    void typeTest() {
        CL_Pokemon pkmn = new CL_Pokemon(null,1,2,0, null);
        assertEquals(1, pkmn.getType());
        pkmn = new CL_Pokemon(null,-1,2,0, null);
        assertNotEquals(1, pkmn.getType());
        assertEquals(-1, pkmn.getType());
    }

    /*
     * Make a new pokemon
     * set its pok to true
     * verify it's true
     * change it to false
     * verify it's false
     */
    @Test
    void setGetPok() {
        CL_Pokemon pkmn = new CL_Pokemon(null,1,2,0,null);
        pkmn.setpok(true);
        assertTrue(pkmn.getpok());
        pkmn.setpok(false);
        assertFalse(pkmn.getpok());
    }
}