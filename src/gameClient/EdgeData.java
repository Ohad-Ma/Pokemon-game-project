package gameClient;

import api.edge_data;

/**
 * This class represents an Edge object on a directed weighted graph
 * which implements the given edge_data API.
 * It provides methods to define and interact with an edge, including setting/getting weight, tag, and info.
 */
public class EdgeData implements edge_data {
    private int src;       // Source node
    private int dest;      // Destination node
    private double w;      // Edge weight
    private int tag;       // Tag for algorithm use
    private String info;   // Metadata information

    // Constructors
    public EdgeData() {
        this(0, 0, 0, "", 0);
    }

    public EdgeData(int s, int d, double w, String i, int t) {
        this.src = s;
        this.dest = d;
        this.w = w;
        this.info = i;
        this.tag = t;
    }

    public EdgeData(int src, int dst, double w) {
        this(src, dst, w, "", 0);
    }

    public EdgeData(EdgeData other) {
        this(other.src, other.dest, other.w, other.info, other.tag);
    }

    // API methods
    @Override
    public int getSrc() {
        return this.src;
    }

    @Override
    public int getDest() {
        return this.dest;
    }

    @Override
    public double getWeight() {
        return this.w;
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
        return String.format("Edge[src=%d, dest=%d, weight=%.2f, tag=%d, info='%s']",
                src, dest, w, tag, info);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof edge_data)) return false;
        edge_data other = (edge_data) o;
        return this.src == other.getSrc() &&
               this.dest == other.getDest() &&
               Double.compare(this.w, other.getWeight()) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + src;
        hash = 31 * hash + dest;
        hash = 31 * hash + Double.hashCode(w);
        return hash;
    }
}
