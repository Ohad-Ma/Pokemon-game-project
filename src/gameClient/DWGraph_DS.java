package gameClient;

import java.util.*;

import api.directed_weighted_graph;
import api.*;

/**
 * This class represents a directional weighted graph which is implemented by given
 * directed_weighted_graph api.
 * This class contains functions that aid us building a directed weighted graph
 * such as:
 *  - connect : connect a vertex with another (one sided cause of directed graph)
 *  - addNode : add a vertex to a graph
 *  - getV : returns a collection of all vertices
 *  - getE : returns a list of each node's connected vertices
 *  - removeNode : removes a node
 *  - removeEdge : detach an Edge from a graph
 *  and other setters/getters.
 *
 *  This class is built on 4 maps mostly due to ease of access of data
 *  and efficiency (time complexity of O(1)) which suits the requested time complexities,
 *  hashsets and Entry that works like Pair.
 *
 */
public class DWGraph_DS implements directed_weighted_graph {
    private Map<Integer, node_data> vertices; // Map of Vertices
    private Map<Integer, Set<node_data>> adjacent; //Map of outgoing connections of a vertex
    private Map<Integer, Set<node_data>> inboundAdjacentNodes; //Map of incoming connections of a vertex
    private Map<Map.Entry<Integer, Integer>, EdgeData> edges; //Map of Edges and their data
    private int MC; //Modification

    //Constructor
    public DWGraph_DS() {
        this.adjacent = new HashMap<>();
        this.vertices = new HashMap<>();
        this.inboundAdjacentNodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.MC = 0;
    }

    //----------

    /**
     * returns the node_data by the node_id,
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return this.vertices.get(key);
    }

    /**
     * returns the data of the edge (src,dest), null if none.
     * Note: this method should run in O(1) time.
     * @param src
     * @param dest
     * @return edge_data
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (getNode(src) == null || getNode(dest) == null) {
            return null;
        }
        return this.edges.get(Map.entry(src, dest));
    }

    /**
     * Connects an edge with weight w between node src to node dest.
     * * Note: this method should run in O(1) time.
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (w<0) {
            return;
        }
        if (src == dest || getNode(src) == null || getNode(dest) == null) {
            // No nodes exists or they equal
            return;
        }
        if(getEdge(src, dest) != null && getEdge(src, dest).getWeight() == w ) {
            this.adjacent.get(src).add(getNode(dest)); //Adds a vertex to the outgoing list the src vertex.
            this.inboundAdjacentNodes.get(dest).add(getNode(src)); //Adds the src vertex to the incoming list the dest vertex.
            this.edges.put(Map.entry(src, dest), new EdgeData(src, dest, w)); //Place a new edge in the edge map.
            return;
        }
        this.adjacent.get(src).add(getNode(dest)); //Adds a vertex to the outgoing list the src vertex.
        this.inboundAdjacentNodes.get(dest).add(getNode(src)); //Adds the src vertex to the incoming list the dest vertex.
        this.edges.put(Map.entry(src, dest), new EdgeData(src, dest, w)); //Place a new edge in the edge map.
        this.MC++;
    }

    /**
     * adds a new node to the graph with the given node_data.
     * Note: this method should run in O(1) time.
     * @param n
     */
    @Override
    public void addNode(node_data n) {
        if (n == null) { //given vertex does not exist.
            return;
        }
        /*
         * - adds the vertex to vertices map
         * - adds the vertex to the outgoing connections while initializing a new LinkedHashSet
         *   to "prepare the ground" for another vertices.
         *
         * - adds the vertex to the incoming connections while initializing a new LinkedHashSet
         *   to "prepare the ground" for another vertices.
         */
        this.vertices.put(n.getKey(), n);
        this.adjacent.put(n.getKey(), new LinkedHashSet<>());
        this.inboundAdjacentNodes.put(n.getKey(), new LinkedHashSet<>());
        this.MC++;
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     * Note: this method should run in O(1) time.
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_data> getV() {
        return vertices.values();
    }

    /**
     * This method returns a pointer (shallow copy) for the
     * collection representing all the edges getting out of
     * the given node (all the edges starting (source) at the given node).
     * Note: this method should run in O(k) time, k being the collection size.
     * @return Collection<edge_data>
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        if (this.getNode(node_id) == null) {
            // return null;
            return Collections.emptyList();
        }

        List<edge_data> edgeDatas = new ArrayList<>();
        for (node_data node : this.adjacent.get(node_id)) {
            /*
             * "walk over" all the outgoing connections until reaching a wall of
             * bricks and adding each node the a new list "edgeDatas".
            */
            edgeDatas.add(getEdge(node_id, node.getKey()));
        }
        return edgeDatas;
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * This method should run in O(k), V.degree=k, as all the edges should be removed.
     * @return the data of the removed node (null if none).
     * @param key
     */
    @Override
    public node_data removeNode(int key) {
        node_data nodeToBeDeleted = getNode(key); //the vertex that have to be removed - if does not exists contains null.
        if (nodeToBeDeleted != null) {
            /*
             * In other to prevent NullPointerException we have to make a new List
             * that store the values as Pairs (Map.Entry) and store each outgoing connections
             * and incoming connections maps from each vertex.
             */
            List<Map.Entry<Integer, Integer>> list = new ArrayList<>();
            for (node_data node : this.adjacent.get(key)) {
                list.add(Map.entry(key, node.getKey()));
            }
            for (node_data node : this.inboundAdjacentNodes.get(key)) {
                list.add(Map.entry(node.getKey(), key));
            }

            for (Map.Entry<Integer, Integer> edge : list) {
                // Now we want to remove the vertex from edges list.
                this.removeEdge(edge.getKey(), edge.getValue());
            }

            /*
             * Then remove the vertex from
             * outgoing connections map (adjacent map) and from vertices map.
             */
            this.adjacent.remove(key);
            this.vertices.remove(key);
            this.MC++;
        }
        return nodeToBeDeleted;
    }

    /**
     * Deletes the edge from the graph,
     * Note: this method should run in O(1) time.
     * @param src
     * @param dest
     * @return the data of the removed edge (null if none).
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        if (getEdge(src, dest) == null) {
            //If there is no edge.
            return null;
        }
        if ((getNode(src) == null || getNode(dest) == null)) {
            //Both source,destination vertices does not exist.
            return null;
        }
        if (!this.adjacent.get(src).contains(getNode(dest))) {
            //There is no connection in the outgoing connections map (adjacent)
            return null;
        }
        this.MC++;
        this.adjacent.get(dest).remove(getNode(dest));
        this.inboundAdjacentNodes.get(dest).remove(getNode(src));
        return this.edges.remove(Map.entry(src, dest));
    }

    /** Returns the number of vertices (nodes) in the graph.
     * Note: this method should run in O(1) time.
     * @return int
     */
    @Override
    public int nodeSize() {
        return this.vertices.keySet().size();
    }

    /**
     * Returns the number of edges (assume directional graph).
     * Note: this method should run in O(1) time.
     * @return int
     */
    @Override
    public int edgeSize() {
        // Assuming the number of edges
        return this.edges.size();
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     * @return int
     */
    @Override
    public int getMC() {
        return this.MC;
    }

    /**
     * Addresses equality of directed weighted graph objects
     * @param o1
     * @return boolean
     */
    @Override
    public boolean equals(Object o1) {
        if (o1 == null) {
            return false;
        }
        if (o1 == this) {
            return true;
        }
        if (!(o1 instanceof DWGraph_DS)) {
            return false;
        }
        DWGraph_DS other = (DWGraph_DS) o1;
        return vertices.equals(other.vertices) && adjacent.equals(other.adjacent)
                && inboundAdjacentNodes.equals(other.inboundAdjacentNodes) && edges.equals(other.edges)
                && this.MC == other.MC;

    }

    /**
     * Prevents a collisions of the Maps in the class
     * @return int
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.vertices.hashCode();
        hash = 31 * hash + this.adjacent.hashCode();
        hash = 31 * hash + this.inboundAdjacentNodes.hashCode();
        hash = 31 * hash + this.edges.hashCode();
        hash = 31 * hash + MC;
        return hash;
    }

}
