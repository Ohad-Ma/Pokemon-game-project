package gameClient;

import api.*;
import api.node_data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * This class represents a Graph-Theory Algorithms on a directed weighted graph that was implemented in gameClient.DWGraph_DS,
 * and also implements the given dw_graph_algorithms api.
 * This class and the algorithm is based on a MinHeap data-structure and contain it as an inner class for easier understanding.
 *
 * This class contains all the requested methods:
 *              - init,
 *              - getGraph
 *              - copy
 *              - isConnected
 *              - shortestPathDist,
 *              - shortestPath
 *              - other two private functions and an inner class ( each has an explanation)
 *
 * The Dijkstra algorithm I used:
 * (taken from https://ssaurel.medium.com/calculate-shortest-paths-in-java-by-implementing-dijkstras-algorithm-5c1db06b6541)
 *
 * function Dijkstra(Graph,source):
 *
 *   	create vertex set Q
 *
 *   	for each vertex v in Graph:		//Initialization
 *   		dist[v] <- INFINITY		//Unknown distance from source to V
 *   		prev[v] <- UNDEFINED		//Previous node in optimal path from source
 *   		add v to Q			//All nodes initially in Q (unvisited nodes)
 *
 *      dist[source] <- 0			//Distance from source to source
 *
 *   	while Q is not empty:
 *   		u <- vertex in Q with min dist[u] //Source node will be selected first
 *   		remove u from Q
 *
 *
 *   		for each neighbor v of u:        // where v is still in Q
 *   			alt <- dist[u] + length(u,v)
 *   			if alt < dist[v]:	 // A shorter path to v has been found
 *   			dist[v] <- alt
 *   			prev[v] <- u
 *
 *   	return dist[], prev[]
 */
public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph ;

public  DWGraph_Algo(){
    this.graph=null;
}
    /**
     * Init the graph on which this set of algorithms operates on.
     * @param g
     */
    @Override
    public void init(directed_weighted_graph g) {
        graph = g;
    }

    /**
     * Return the underlying graph of which this class works.
     * @return directed_weighted_graph
     */
    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     * @return directed_weighted_graph
     */
    @Override
    public directed_weighted_graph copy() {
        /*
         * Make a new graph graphCopy then walk over it and
         * add each vertex of the wanted graph to the new one.
         */
        directed_weighted_graph graphCopy = new DWGraph_DS();
        for (node_data currNode : this.graph.getV()) {
            graphCopy.addNode(currNode);
        }
        for (node_data currNode : this.graph.getV()) {
            /*
             * Walk over the wanted graph edges and connected them in the new graph (graphCopy).
             */
            for (edge_data edge : this.graph.getE(currNode.getKey())) {
                this.graph.connect(edge.getSrc(), edge.getDest(), edge.getWeight());
            }
        }
        return graphCopy;
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     * @return boolean
     * Explanation:
     * First we check whether the graph is empty or if there's one vertex. If happen -> returns true.
     * Then we get all the distances with dijkstra algorithm and we walk with a node all over the vertices.
     * If one of the distances that found in dijkstra is MAX_VALUE (or infinity) the it will return false
     * because it's obvious that the graph isn't connected if one of the nodes isn't reachable.
     * If the above doesn't happen the graph is connected.
     */
    @Override
    public boolean isConnected() {
        Collection<node_data> vertices = graph.getV();
        if (vertices.isEmpty() || vertices.size() == 1) {
            return true;
        }
        node_data src = vertices.iterator().next();
        Map<node_data, Double> allDistances = dijkstra(src.getKey()).getValue();
        for (node_data node : graph.getV()) {
            if (allDistances.get(node) == Double.MAX_VALUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     * @param src - start node
     * @param dest - end (target) node
     * @return double
     * Explanation:
     * We use Map.Entry which is like Pairs to store all the shortest paths using dijkstra algorithm
     * Then if there is no such path we return -1  or return the distance we got.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> allShortestsPaths = dijkstra(src);
        Map<node_data, Double> distances = allShortestsPaths.getValue();
        return distances.get(graph.getNode(dest));
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     * @param src - start node
     * @param dest - end (target) node
     * @return List<node_info>
     * Explanation:
     * We store all the source's paths using Map.Entry (which is like Pairs) then use a private method to get
     * the the path by entering the destination node and its parents.
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if (!(this.graph.getV().contains(this.graph.getNode(src))) ||
                !(this.graph.getV().contains(this.graph.getNode(dest)))) {
            return null;
        }
        Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> allShortestsPaths = dijkstra(src);
        Map<node_data, node_data> parents = allShortestsPaths.getKey();
        return computePath(graph.getNode(dest), parents);
    }

    /**
     * A private function that returns a list with the shortest path.
     * First of all we make a list of the shortest path we find by checking if the destination node isn't null, while he
     * isn't null then the list will get the current node which was pointed at the destination node and the
     * current node will turn into destination's node parent. Since we went from the end we will have to reverse
     * the list we intend to return.
     * @param destNode
     * @param parents
     * @return List<node_info>
     */
    private List<node_data> computePath(node_data destNode, Map<node_data, node_data> parents) {
        List<node_data> shortestPath = new ArrayList<>();
        node_data currNode = destNode;
        while (currNode != null) {
            shortestPath.add(currNode);
            currNode = parents.get(currNode);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    /**
     * An inner class that represents a MinHeap data structure,
     * the class implemented by a TreeMap in order to sort the priorities.
     */
    class MinHeap {
        Map<node_data, Double> elementToPriority;
        TreeMap<Double, Set<node_data>> prioritiesToElements; // We intentionally use TreeSet for sorting.

        //Default constructor
        MinHeap() {
            elementToPriority = new HashMap<>();
            prioritiesToElements = new TreeMap<>();
        }

        /**
         * Add a node to the Heap only if the priority already doesn't exist.
         * Runs in O(1) if the priorities are different then O(nlogn).
         * @param node
         * @param priority
         */
        public void add(node_data node, double priority) {
            elementToPriority.put(node, priority);
            if (!prioritiesToElements.containsKey(priority)) {
                prioritiesToElements.put(priority, new LinkedHashSet<>());
            }
            prioritiesToElements.get(priority).add(node);
        }

        /**
         * Decreases value of a key.
         * Runs in O(1) if the priorities are different then O(nlogn).
         * @param node
         * @param priority
         * @return node_info
         */
        public node_data decreaseKey(node_data node, double priority) {
            this.remove(node);
            this.add(node, priority);
            return node;
        }

        /**
         * Removes a node from the Priority queue.
         * Explanation:
         * We store the priority and the previous priority in a Set, Then we remove the node
         * if the list is empty there is no point of keeping it so we better remove it.
         * Then we return a Pair of a node and its previous priority.
         * @param node
         * @return Map.Entry<node_info, Double>
         */
        public Map.Entry<node_data, Double> remove(node_data node) {
            double previousPriority = elementToPriority.get(node);
            Set<node_data> elements = this.prioritiesToElements.get(previousPriority);
            elements.remove(node);
            if (elements.isEmpty()) {
                prioritiesToElements.remove(previousPriority);
            }
            elementToPriority.remove(node);
            return new SimpleEntry<>(node, previousPriority);
        }

        /**
         * Check whether the priority queue is empty.
         * (It check in both TreeMap & HashMap)
         * @return boolean
         */
        public boolean isEmpty() {
            return this.prioritiesToElements.isEmpty() && elementToPriority.isEmpty();
        }

        /**
         * Remove Min / Extract Min from the Heap.
         * Explanation:
         * If the list is not empty we get the lowest priority value in the priority queue then with an
         * iterator we get the first node (maximum) in the priority and remove it. Else we will get a null
         * because the list is empty.
         * @return Map.Entry<node_info, Double>
         */
        public Map.Entry<node_data, Double> removeMin() {
            if (!isEmpty()) {
                Map.Entry<Double, Set<node_data>> lowestPriority = this.prioritiesToElements.firstEntry();
                node_data firstNodeInPriority = lowestPriority.getValue().iterator().next();
                return remove(firstNodeInPriority);
            }
            return null;
        }
    }
    /**
     * Dijkstra algorithm based on a MinHeap as a priority queue.
     * If there are n different priorities then the time complexity of the sort is O(nlogn) on the other hand
     * in case that can be atleast V different priorities (in case each node has different priority) then the
     * sort is O(Vlog(V)).
     *
     * Explanation:
     * Like I mentioned in the header I used an algorithm.
     * First we make a map of parents and their distance and make a new heap
     * then we store in that heap,parents and distances all the edges while all the distances of all the nodes
     * in the graph is Infinity (Double.MAX_VALUE) then we update the source node we start with.
     * if the heap isn't empty get the distance of current and the path and put them in the
     * distances map.
     * Then we go all over the neighbors of the current and check for the weight of the edge searching
     * for the smallest one and decreasing each key and we keep going like that.
     * Then - finally! we get the shortest path in a map entry
     * @param src
     * @return Map.Entry<Map<node_info, node_info>, Map<node_info, Double>>
     */
    private Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> dijkstra(int src) {
        Map<node_data, node_data> parents = new HashMap<>();
        Map<node_data, Double> distances = new HashMap<>();
        MinHeap heap = new MinHeap();
        for (node_data node : this.graph.getV()) {
            parents.put(node, null);
            heap.add(node, Double.MAX_VALUE);
            distances.put(node, Double.MAX_VALUE);
        }
        heap.decreaseKey(graph.getNode(src), 0);

        while (!heap.isEmpty()) {
            Map.Entry<node_data, Double> entry = heap.removeMin();
            node_data current = entry.getKey();
            double pathDistance = entry.getValue();
            distances.put(current, pathDistance);

            for (edge_data edge : graph.getE(current.getKey())) {
                double edgeWeight = edge.getWeight();
                double currentDistance = pathDistance + edgeWeight;

                node_data destNode = graph.getNode(edge.getDest());
                if (currentDistance < distances.get(destNode)) {
                    parents.put(destNode, current);
                    distances.put(destNode, currentDistance);
                    heap.decreaseKey(destNode, currentDistance);
                }
            }
        }
        return new SimpleEntry<>(parents, distances);
    }
    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
     @Override
        public boolean save(String file) {
            try {
                JSONObject object = new JSONObject();
                JSONArray nodesArray = new JSONArray();
                for (node_data node : this.graph.getV()) {
                    //walks over the vertices in the graph turning em into json objects
                    JSONObject nodeObject = new JSONObject();
                    nodeObject.put("pos", node.getLocation());
                    nodeObject.put("id", node.getKey());
                    nodesArray.put(nodeObject);
                }
                object.put("Nodes", nodesArray);

                JSONArray edgesArray = new JSONArray();
                for (node_data node : this.graph.getV()) {
                    for (edge_data edge : this.graph.getE(node.getKey())) {
                        //walks over the edges in the graph turning em into json objects
                        JSONObject edgeObject = new JSONObject();
                        edgeObject.put("src", edge.getSrc());
                        edgeObject.put("dest", edge.getDest());
                        edgeObject.put("w", edge.getWeight());
                        edgesArray.put(edgeObject);
                    }
                }
                object.put("Edges", edgesArray);

                String filename = !file.contains(".json") ? file + ".json" : file;
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                object.write(writer);
                writer.flush();
                writer.close();

            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
                return false;
            }
            return true;
        }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        try {
            String filename = !file.contains(".json") ? file : file;
            String objectContent = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject graphObject = new JSONObject(objectContent);

            JSONArray nodesArray = graphObject.getJSONArray("Nodes");
            for (int i = 0; i < nodesArray.length(); ++i) {
                JSONObject nodeObject = nodesArray.getJSONObject(i);
                node_data n = new NodeData(nodeObject.getInt("id")); //Loading the ids in the JSON file to this graph
                String[] posArray = nodeObject.getString("pos").split(","); // pos is received as a String with ','
                //so I wanted to turn it into something like: pos: x,y,z -> arr = {x,y,z} -> arr[0] = x, arr[1] = y, arr[2] = z
                n.setLocation(new GeoLocation( //here each data of the array above (posArray) has an int so I load these ints to the vertex's location
                        Double.parseDouble(posArray[0]),
                        Double.parseDouble(posArray[1]),
                        Double.parseDouble(posArray[2])
                ));
                this.graph.addNode(n);
            }

            JSONArray edgesArray = graphObject.getJSONArray("Edges");
            for (int i = 0; i < edgesArray.length(); ++i) { //Walks all over the Edges in the JSON file and load em into my graph
                JSONObject edgeObject = edgesArray.getJSONObject(i);
                this.graph.connect(edgeObject.getInt("src"), edgeObject.getInt("dest"),
                        edgeObject.getDouble("w"));
            }
        } catch (IOException | JSONException ex) { //File does not exists or one of the lines doesn't (pos/id/edges)
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
}