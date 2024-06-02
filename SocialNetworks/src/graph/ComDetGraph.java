package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ComDetGraph implements Graph {
	
	//data structures to store nodes and edges
	private HashMap<Integer, ComDetNode> nodeMap;
	private HashSet<ComDetEdge> edges;
	
	public ComDetGraph () {
		this.nodeMap = new HashMap<>();
		this.edges = new HashSet<>();
	}
	
	//methods to expose properties of the graph
	public int getNumVertices() {
		return nodeMap.values().size();
	}
	
	public int getNumEdges() {
		return edges.size();
	}
	
	public HashMap<Integer, ComDetNode> getNodeMap() {
		return nodeMap;
	}
	
	public HashSet<ComDetEdge> getEdges() {
		return edges;
	}

	//methods to add vertices and edges
	@Override
	public void addVertex(int num) {
		ComDetNode n = nodeMap.get(num);
		if (n == null) {
			n = new ComDetNode(num);
			nodeMap.put(num, n);
		}
	}

	@Override
	public void addEdge(int i1, int i2) {
		ComDetNode n1 = nodeMap.get(i1);
		ComDetNode n2 = nodeMap.get(i2);
		
		if (n1 == null) {
			throw new IllegalArgumentException("Node with value " + i1 + 
					" does not exist in graph");
		} 
		if (n2 == null) {
			throw new IllegalArgumentException("Node with value " + i2 + 
					" does not exist in graph");
		}
		
		ComDetEdge edge = new ComDetEdge(n1, n2, 1);
		edges.add(edge);
		n1.addNeighbor(n2);
		n2.addNeighbor(n1);
	}

	@Override
	public Graph getEgonet(int center) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Graph> getSCCs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
		return null;
	}

}
