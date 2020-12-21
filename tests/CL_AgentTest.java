import api.directed_weighted_graph;
import api.node_data;
import gameClient.CL_Agent;
import gameClient.DWGraph_DS;
import gameClient.NodeData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CL_AgentTest {

    /*
     * Make Agent JSON file
     * Make CL_Agent
     * add couple of nodes to a graph
     * set src node and set of the agent
     * verify it changed
     * update the agent to the Agent JSON file
     * verify it updated
     *
     */
    @Test
    public void getSrcNode() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":3," +
                "\"value\":0.0," +
                "\"src\":5," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"31.20140321671526,36.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 10; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph,6);
        assertEquals(6, agent.getSrcNode());
        agent.update(Agent2Json);
        assertEquals(5, agent.getSrcNode());
    }

    /*
     * Make Agent JSON file
     * Make CL_Agent
     * add couple of nodes to a graph
     * set src node and set of the agent
     * verify it changed
     * update the agent to the Agent JSON file
     * verify it updated using agent.update
     *
     */
    @Test
    public void currNodeSetTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":3," +
                "\"value\":0.0," +
                "\"src\":3," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"31.20140321671526,36.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 7; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph,1);
        agent.setCurrNode(5);
        assertEquals(5, agent.getSrcNode());
        agent = new CL_Agent(graph, 2);
        assertEquals(2, agent.getSrcNode());
        agent.update(Agent2Json);
        assertEquals(3, agent.getSrcNode());
    }

    /*
     * Make Agent JSON file
     * Make CL_Agent
     * add couple of nodes to a graph
     * set src node and set of the agent
     * verify it changed
     * update the agent to the Agent JSON file
     * verify speed and src changes
     *
     */
    @Test
    public void updateTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":3," +
                "\"value\":0.0," +
                "\"src\":3," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"31.20140321671526,36.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 10; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph,2);
        agent.setCurrNode(1);
        agent.setSpeed(2);
        assertEquals(1, agent.getSrcNode());
        assertEquals(2, agent.getSpeed());
        agent.update(Agent2Json);
        assertEquals(Agent2Json, agent.toString());
    }

    /*
     * Make Agent JSON file
     * Make CL_Agent
     * add couple of nodes
     * connect some
     * set the next node of the agent
     * verify the agent's node changed
     * update CL_Agent as the first JSON agent
     * verify the set of the next node against the edge
     * verify existence of the edge
     * verify the agent's next target
     */
    @Test
    public void nextNodeSetTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":3," +
                "\"value\":0.0," +
                "\"src\":3," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"32.201403216715264,6.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 10; i++){
            graph.addNode(new NodeData(i));
        }
        graph.connect(2, 5, 1);
        graph.connect(5,1,7);
        graph.connect(3, 4, 2);
        graph.connect(3, 5, 2);
        graph.connect(4, 2, 3);
        graph.connect(8, 1, 2);
        graph.connect(1, 3, 5);
        agent = new CL_Agent(graph,2);
        agent.setCurrNode(1);
        assertEquals(1, agent.getSrcNode());
        agent.update(Agent2Json);
        assertEquals(graph.getEdge(3,5) != null, agent.setNextNode(5));
        assertEquals(5, agent.getNextNode());
    }

    /*
     * Make Agent JSON file which gets a value of 6
     * Make CL_Agent
     * add couple of nodes
     * verify that the CL_Agent id is -1
     * update CL_Agent as the first JSON agent
     * verify the id
     */
    @Test
    public void idTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":3," +
                "\"value\":0.0," +
                "\"src\":4," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"32.201403216715264,6.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 7; i++){
            graph.addNode(new NodeData(i));
        }

        agent = new CL_Agent(graph, 1);
        assertEquals(-1, agent.getID());
        agent.update(Agent2Json);
        assertEquals(3, agent.getID());
    }

    /*
     * Make Agent JSON file which gets a value of 6
     * Make CL_Agent
     * add couple of nodes
     * verify that the CL_Agent location is null
     * update CL_Agent as the first JSON agent
     * verify the location
     */
    @Test
    public void locationTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":0," +
                "\"value\":0.0," +
                "\"src\":4," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"32.201403216715264,6.105967832285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 10; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph, 6);
        assertNull(agent.getLocation());
        agent.update(Agent2Json);
        assertEquals("32.20140321671526,6.105967832285614,0.0", agent.getLocation().toString());

    }

    /*
     * Make Agent JSON file which gets a value of 6
     * Make CL_Agent
     * add couple of nodes
     * update CL_Agent as the first JSON agent
     * verify the speed
     */
    @Test
    public void speedTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":0," +
                "\"value\":0.0," +
                "\"src\":3," +
                "\"dest\":3," +
                "\"speed\":4," +
                "\"pos\":\"38.201403996715264,31.105969032285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 10; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph,3);
        agent.update(Agent2Json);
        assertEquals(4, agent.getSpeed());
    }

    /*
     * Make Agent JSON file which gets a value of 6
     * Make CL_Agent
     * add couple of nodes
     * update CL_Agent as the first JSON agent
     * verify the value
     */
    @Test
    public void valueTest() {
        String Agent2Json = "{\"Agent\":{" +
                "\"id\":0," +
                "\"value\":6," +
                "\"src\":7," +
                "\"dest\":-1," +
                "\"speed\":1.0," +
                "\"pos\":\"31.201403996715264,36.105969032285614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 9; i++){
            graph.addNode(new NodeData(i));
        }
        agent = new CL_Agent(graph, 1);
        agent.update(Agent2Json);
        assertEquals(6, agent.getValue());
    }

    /*
     *  Make Agent JSON file
     *  Make CL_Agent
     *  add 8 vertices to a graph
     *  connect some
     *  verify that the next node is indeed reachable
     *  set next node a node that isn't connected to the path
     *  verify the next node is indeed -1
     */
    @Test
    public void nextNodeTest() {
        String Agent2Json = "{\"Agent\":" +
                "{\"id\":0," +
                "\"value\":0.0," +
                "\"src\":3," +
                "\"dest\":5," +
                "\"speed\":2.0," +
                "\"pos\":\"31.201403996715264,35.105999032585614,0.0\"}}";
        CL_Agent agent;
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 1; i < 9; i++){
            graph.addNode(new NodeData(i));
        }
        graph.connect(1, 2, 1);
        graph.connect(2,1,2);
        graph.connect(3, 5, 3);
        graph.connect(4, 1, 5);
        graph.connect(9, 1, 6);
        graph.connect(9, 8, 6);
        agent = new CL_Agent(graph,4);
        agent.update(Agent2Json);
        assertEquals(5, agent.getNextNode());
        agent.setNextNode(9);
        assertEquals(-1, agent.getNextNode());
    }
}