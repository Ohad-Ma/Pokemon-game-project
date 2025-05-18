package gameClient;

import api.geo_location;
import api.node_data;

/**
 * This class represents a Vertex object in a graph, implementing the node_data API.
 * It provides information and metadata used in graph algorithms and visualization.
 */
public class NodeData implements node_data {
    private int tag;               // Useful for algorithms (e.g., color marking)
    private String info;          // Optional metadata
    private int key;              // Unique identifier for the node
    private geo_location pos;     // Node's position in space
    private double weight;        // Weight for algorithms (e.g., Dijkstra)

    // Constructors
    public NodeData() {
        this.tag = 0;
        this.info = "";
        this.pos = null;
        this.weight = 0;
    }

    public NodeData(int key) {
        this();
        this.key = key;
    }

    public NodeData(int key, double x, double y, double z) {
        this();
        this.key = key;
        this.pos = new GeoLocation(x, y, z);
    }

    public NodeData(int key, geo_location pos) {
        this();
        this.key = key;
        this.pos = new GeoLocation(pos.x(), pos.y(), pos.z());
    }

    // Interface implementation
    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public geo_location getLocation() {
        return this.pos;
    }

    @Override
    public void setLocation(geo_location p) {
        if (p != null) {
            this.pos = new GeoLocation(p.x(), p.y(), p.z());
        } else {
            this.pos = null;
        }
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    @Override
    public String toString() {
        return String.format("Node[%d]", this.getKey());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof NodeData)) return false;
        if (o == this) return true;
        NodeData other = (NodeData) o;
        return this.key == other.key &&
               ((this.info == null && other.info == null) || (this.info != null && this.info.equals(other.info))) &&
               this.tag == other.tag;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.key;
        hash = 31 * hash + (this.info == null ? 0 : this.info.hashCode());
        hash = 31 * hash + this.tag;
        return hash;
    }
}
