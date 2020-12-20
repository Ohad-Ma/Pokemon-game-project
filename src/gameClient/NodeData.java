package gameClient;

import api.geo_location;
import api.node_data;

/**
 * This class represent a Vertex object on a graph which
 * implemented by given node_data api.
 * Contains functions that define a vertex on a directed weighted graph such as: setLocation and setWeight (which isn't used)
 */
public class NodeData implements node_data {
	private int tag; //tag of each vertex - help with the algorithm
	private String info; //info of each vertex
	private int key; // key of each vertex
	private geo_location pos; //geo_location of each node - coordinates x,y,z

	//Constructors
	public NodeData() {
		this.tag = 0;
		this.setInfo("");
		this.pos = null;
	}

	public NodeData(int key) {
		this();
		this.key = key;
	}

	public NodeData(int key, double x,double y,double z) {
		this();
		this.pos = new GeoLocation(x, y, z);
		this.key = key;
	}
	//------------

	/**
	 * Returns the key (id) associated with this node.
	 * @return int
	 */
	@Override
	public int getKey() {
		return this.key;
	}

	/** Returns the location of this node, if
	 * none return null.
	 *
	 * @return geo_location
	 */
	@Override
	public geo_location getLocation() {
		return this.pos;
	}

	/** Allows changing this node's location.
	 * @param p - new new location  (position) of this node.
	 */
	@Override
	public void setLocation(geo_location p) {
		this.pos = new GeoLocation(p.x(), p.y(), p.z());
	}

	/**
	 * Returns the weight associated with this node.
	 * @return double
	 */
	@Override
	public double getWeight() {
		return 0;
	}

	/**
	 * Allows changing this node's weight.
	 * @param w - the new weight
	 */
	@Override
	public void setWeight(double w) {}

	/**
	 * Returns the remark (meta data) associated with this node.
	 * @return
	 */
	@Override
	public String getInfo() {
		return "info: " + this.info;
	}

	/**
	 * Allows changing the remark (meta data) associated with this node.
	 * @param s
	 */
	@Override
	public void setInfo(String s) {
		this.info = s;
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
	 * Allows setting the "tag" value for temporal marking an node - common
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
	@Override
	public void setTag(int t) {

	}

	@Override
	public String toString() {
		return String.format("%d", this.getKey());
	}

	/**
	 * Equality of addresses of gameClient.NodeData objects
	 * @param o
	 * @return boolean
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (o == this)
			return true;
		if (!(o instanceof NodeData))
			return false;
		NodeData other = (NodeData) o;
		return this.key == other.key && this.info.equals(other.info) && this.tag == other.tag;
	}

	/**
	 * Prevents collisions on the hashmap of outer class gameClient.DWGraph_DS
	 * @return int
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.key;
		hash = 31 * hash + this.info.hashCode();
		hash = 31 * hash + Double.hashCode(this.tag);
		return hash;
	}
}
