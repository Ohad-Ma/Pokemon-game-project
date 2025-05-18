package gameClient;

import api.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph;

    public DWGraph_Algo() {
        this.graph = null;
    }

    @Override
    public void init(directed_weighted_graph g) {
        graph = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph graphCopy = new DWGraph_DS();
        for (node_data node : this.graph.getV()) {
            graphCopy.addNode(new NodeData(node)); // Deep copy of node
        }
        for (node_data node : this.graph.getV()) {
            for (edge_data edge : this.graph.getE(node.getKey())) {
                graphCopy.connect(edge.getSrc(), edge.getDest(), edge.getWeight());
            }
        }
        return graphCopy;
    }

    @Override
    public boolean isConnected() {
        if (graph.getV().isEmpty() || graph.nodeSize() == 1) return true;

        for (node_data node : graph.getV()) {
            Map<node_data, Double> dist = dijkstra(node.getKey()).getValue();
            for (double d : dist.values()) {
                if (d == Double.MAX_VALUE) return false;
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> allPaths = dijkstra(src);
        Map<node_data, Double> distances = allPaths.getValue();
        double dist = distances.getOrDefault(graph.getNode(dest), Double.MAX_VALUE);
        return dist == Double.MAX_VALUE ? -1 : dist;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if (!graph.getV().contains(graph.getNode(src)) || !graph.getV().contains(graph.getNode(dest))) {
            return null;
        }
        Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> allPaths = dijkstra(src);
        Map<node_data, node_data> parents = allPaths.getKey();
        List<node_data> path = computePath(graph.getNode(dest), parents);
        return path.isEmpty() ? null : path;
    }

    private List<node_data> computePath(node_data dest, Map<node_data, node_data> parents) {
        List<node_data> path = new ArrayList<>();
        while (dest != null) {
            path.add(dest);
            dest = parents.get(dest);
        }
        Collections.reverse(path);
        return path;
    }

    private class MinHeap {
        Map<node_data, Double> elementToPriority = new HashMap<>();
        TreeMap<Double, Set<node_data>> prioritiesToElements = new TreeMap<>();

        public void add(node_data node, double priority) {
            elementToPriority.put(node, priority);
            prioritiesToElements.computeIfAbsent(priority, k -> new LinkedHashSet<>()).add(node);
        }

        public node_data decreaseKey(node_data node, double priority) {
            remove(node);
            add(node, priority);
            return node;
        }

        public Map.Entry<node_data, Double> remove(node_data node) {
            double prevPriority = elementToPriority.get(node);
            Set<node_data> nodes = prioritiesToElements.get(prevPriority);
            nodes.remove(node);
            if (nodes.isEmpty()) prioritiesToElements.remove(prevPriority);
            elementToPriority.remove(node);
            return new AbstractMap.SimpleEntry<>(node, prevPriority);
        }

        public boolean isEmpty() {
            return elementToPriority.isEmpty();
        }

        public Map.Entry<node_data, Double> removeMin() {
            if (isEmpty()) return null;
            Map.Entry<Double, Set<node_data>> first = prioritiesToElements.firstEntry();
            node_data node = first.getValue().iterator().next();
            return remove(node);
        }
    }

    private Map.Entry<Map<node_data, node_data>, Map<node_data, Double>> dijkstra(int src) {
        Map<node_data, node_data> parents = new HashMap<>();
        Map<node_data, Double> distances = new HashMap<>();
        MinHeap heap = new MinHeap();

        for (node_data node : graph.getV()) {
            parents.put(node, null);
            distances.put(node, Double.MAX_VALUE);
            heap.add(node, Double.MAX_VALUE);
        }
        heap.decreaseKey(graph.getNode(src), 0);

        while (!heap.isEmpty()) {
            Map.Entry<node_data, Double> curr = heap.removeMin();
            node_data current = curr.getKey();
            double dist = curr.getValue();
            distances.put(current, dist);

            for (edge_data edge : graph.getE(current.getKey())) {
                node_data neighbor = graph.getNode(edge.getDest());
                double newDist = dist + edge.getWeight();
                if (newDist < distances.get(neighbor)) {
                    parents.put(neighbor, current);
                    distances.put(neighbor, newDist);
                    heap.decreaseKey(neighbor, newDist);
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(parents, distances);
    }

    @Override
    public boolean save(String file) {
        try {
            JSONObject object = new JSONObject();
            JSONArray nodesArray = new JSONArray();
            for (node_data node : this.graph.getV()) {
                JSONObject nodeObject = new JSONObject();
                geo_location loc = node.getLocation();
                String pos = loc.x() + "," + loc.y() + "," + loc.z();
                nodeObject.put("pos", pos);
                nodeObject.put("id", node.getKey());
                nodesArray.put(nodeObject);
            }
            object.put("Nodes", nodesArray);

            JSONArray edgesArray = new JSONArray();
            for (node_data node : this.graph.getV()) {
                for (edge_data edge : this.graph.getE(node.getKey())) {
                    JSONObject edgeObject = new JSONObject();
                    edgeObject.put("src", edge.getSrc());
                    edgeObject.put("dest", edge.getDest());
                    edgeObject.put("w", edge.getWeight());
                    edgesArray.put(edgeObject);
                }
            }
            object.put("Edges", edgesArray);

            String filename = file.endsWith(".json") ? file : file + ".json";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            object.write(writer);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean load(String file) {
        try {
            this.graph = new DWGraph_DS(); // FIX: initialize the graph
            String filename = file.endsWith(".json") ? file : file + ".json";
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject jsonObject = new JSONObject(content);

            JSONArray nodesArray = jsonObject.getJSONArray("Nodes");
            for (int i = 0; i < nodesArray.length(); i++) {
                JSONObject nodeObj = nodesArray.getJSONObject(i);
                int id = nodeObj.getInt("id");
                String[] posArr = nodeObj.getString("pos").split(",");
                geo_location loc = new GeoLocation(
                        Double.parseDouble(posArr[0]),
                        Double.parseDouble(posArr[1]),
                        Double.parseDouble(posArr[2])
                );
                NodeData node = new NodeData(id);
                node.setLocation(loc);
                graph.addNode(node);
            }

            JSONArray edgesArray = jsonObject.getJSONArray("Edges");
            for (int i = 0; i < edgesArray.length(); i++) {
                JSONObject edgeObj = edgesArray.getJSONObject(i);
                graph.connect(
                        edgeObj.getInt("src"),
                        edgeObj.getInt("dest"),
                        edgeObj.getDouble("w")
                );
            }
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
