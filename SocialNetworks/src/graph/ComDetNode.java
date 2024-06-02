package graph;

import java.util.HashSet;
import java.util.Objects;

public class ComDetNode {
	
	/*the node object has an integer value and a set of nodes as its
	neighbors*/
	private Integer value;
	private HashSet<ComDetNode> neighbors;
	
	//constructor
	public ComDetNode(Integer value) {
		this.value = value;
		this.neighbors = new HashSet<>();
	}

	//getters and setters
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public HashSet<ComDetNode> getNeighbors() {
		return neighbors;
	}
	
	public void addNeighbor (ComDetNode nbr) {
		neighbors.add(nbr);
	}

	/*since many methods will be removing and adding these nodes to 
	 * sets and maps, these methods are overridden to ensure removal
	 * and addition can be carried out without the object hashCodes needing
	 * to match exactly*/
	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComDetNode other = (ComDetNode) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "" + value + "";
	}

}
