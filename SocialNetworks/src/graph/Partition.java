package graph;

import java.util.HashMap;
import java.util.HashSet;

public class Partition {
	
	/*data structures to ensure quick retrieval of community ID for a given node
	 * and quick retrieval of set of nodes in a community for a given community
	 * ID*/
	private HashMap<ComDetNode, Integer> nodeIDMap;
	private HashMap<Integer, HashSet<ComDetNode>> iDNodeSetMap;
	
	public Partition() {
		nodeIDMap = new HashMap<>();
		iDNodeSetMap = new HashMap<>();
	}
	
	//getters
	public HashMap<ComDetNode, Integer> getNodeIDMap() {
		return nodeIDMap;
	}
	
	public HashMap<Integer, HashSet<ComDetNode>> getIDNodeSetMap() {
		return iDNodeSetMap;
	}
	
	//getters for community ID and node set
	public int getComID (ComDetNode node) {
		return nodeIDMap.get(node);
	}
	
	public HashSet<ComDetNode> getNodeSet (int id) {
		return iDNodeSetMap.get(id);
	}

}
