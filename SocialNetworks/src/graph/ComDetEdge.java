package graph;

public class ComDetEdge {
	
	//starting and ending nodes of the edge
	private ComDetNode start;
	private ComDetNode end;
	
	public ComDetEdge (ComDetNode start, ComDetNode end, int weight) {
		this.start = start;
		this.end = end;
	}
	
	//getter for the end node
	public ComDetNode getEndNode() {
		return end;
	}

	@Override
	public String toString() {
		return start + "->" + end;
	}

}
