package gameClient;

import api.edge_data;

/**
 * This class represents an Edge object on a directed weighted graph
 * which implemented by given edge_data api.
 * Contains functions that help us define an Edge on a weighted directed graph such as: setWeight,setTag
 * and many getters/setters.
 */
public class EdgeData implements edge_data {
	private int src; //source vertex
	private int dest; //destination vertex
	private double w; //weight of each edge
	private int tag; //tag of each edge, will help later with the algorithm
	private String info; //info of each edge

	//Constructors--
	public EdgeData(){
		this.src = 0;
		this.dest = 0;
		this.w = 0;
		this.info = "";
		this.tag = 0;
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
		this(other.src, other.dest, other.w, "", 0);
	}

	//-------------

	/**
	 * Equality of addresses of gameClient.EdgeData objects
	 * @param o
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof edge_data))
			return false;
		edge_data other = (edge_data) o;
		return this.src == other.getSrc() && this.getDest() == other.getDest();
	}

	/**
	 * Prevents collisions on the hashmap of outer class gameClient.DWGraph_DS
	 * @return int
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.src;
		hash = 31 * hash + this.dest;
		hash = 31 * hash + this.dest;
		return hash;
	}

	/**
	 * The id of the source node of this edge.
	 * @return int
	 */
	@Override
	public int getSrc() {
		return this.src;
	}

	/**
	 * The id of the destination node of this edge
	 * @return int
	 */
	@Override
	public int getDest() {
		return this.dest;
	}

	/**
	 * @return the weight of this edge (positive value).
	 */
	@Override
	public double getWeight() {
		return this.w;
	}

	/**
	 * Returns the remark (meta data) associated with this edge.
	 * @return String
	 */
	@Override
	public String getInfo() {
		return this.info;
	}

	/**
	 * Allows changing the remark (meta data) associated with this edge.
	 * @param s
	 */
	@Override
	public void setInfo(String s) {
		this.info=s;
	}

	/**
	 * Temporal data (aka color: e,g, white, gray, black)
	 * which can be used be algorithms
	 * @return int
	 */
	@Override
	public int getTag() {
		return this.tag;
	}

	/**
	 * This method allows setting the "tag" value for temporal marking an edge - common
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
	@Override
	public void setTag(int t) {
		this.tag=t;
	}

	@Override
	public String toString(){
		return String.format("src:%d w:%f dest:%d", this.src,this.w,this.dest);
	}
}
