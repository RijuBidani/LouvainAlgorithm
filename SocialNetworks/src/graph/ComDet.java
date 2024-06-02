package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import util.GraphLoader;

public class ComDet {
	
	/*method to create a singleton partition of the graph i.e. store every
	 * node in its own separate community*/
	public Partition singletonPartition (ComDetGraph g) {
		Partition partition = new Partition();
		for (ComDetNode node : g.getNodeMap().values()) {
			/*placing nodes and community IDs in one of the maps in the 
			 * Partition object*/
			partition.getNodeIDMap().put(node, node.getValue());
			
			//a node set to store just the current node
			HashSet<ComDetNode> newNodeSet = new HashSet<>();
			newNodeSet.add(node);
			
			//storing the community IDs along with corresponding node sets
			partition.getIDNodeSetMap().put(node.getValue(), newNodeSet);
		}
		return partition;
	}
	
	//function to calculate modularity for the community C
	private double q (ComDetGraph g, Partition partition, int C, double gamma) {
		
		int numEdges = g.getNumEdges();
		double q_C = 0;
		double int_comp = 0;
		double out_comp = 0;
		
		/*calculating the internal edges of the community and the total number
		of edges for each node in the community*/
		for (ComDetNode n : partition.getIDNodeSetMap().get(C)) {
			for (ComDetNode nbr : n.getNeighbors()) {
				if (partition.getIDNodeSetMap().get(C).contains(nbr)) {
					++int_comp;
				}
				++out_comp;
			}
		}
		
		//normalization and other arithmetic operations
		double nEC = (double) (2 * numEdges);
		int_comp /= nEC;
		out_comp *= out_comp;
		out_comp /= (Math.pow((2 * numEdges), 2));
		out_comp *= gamma;
		q_C = int_comp - out_comp;
		
		return q_C;
		
	}
	
	/*Calculating change in modularity on moving node i from community C to 
	 * community D*/
	private double del_Q 
	(ComDetGraph g, Partition partition, ComDetNode i, int C, int D, double gamma) {
		
		int numEdges = g.getNumEdges();
		double step1output = 0.0;
		double step2output = 0.0;
		int deg_i_orig = i.getNeighbors().size();
		int deg_i_C = deg_i_orig;
		int deg_i_D = deg_i_orig;
		int sum_deg_C = 0;
		int sum_deg_D = 0;
		double del_Q = 0;
		
		//calculating degrees of node i in communities C and D
		for (ComDetNode nbr : i.getNeighbors()) {
			if (!partition.getIDNodeSetMap().get(C).contains(nbr)) {
				--deg_i_C;
			} 
		}
		
		for (ComDetNode nbr : i.getNeighbors()) {
			if (!partition.getIDNodeSetMap().get(D).contains(nbr)) {
				--deg_i_D;
			} 
		}
		
		//calculating sums of degrees of nodes in communities C and D
		for (ComDetNode n : partition.getIDNodeSetMap().get(C)) {
			sum_deg_C += n.getNeighbors().size();
		}
		
		for (ComDetNode n : partition.getIDNodeSetMap().get(D)) {
			sum_deg_D += n.getNeighbors().size();
		}
		
		/*if node i is being moved from community C to D, its contribution to
		 * the sum of degrees of nodes in D must be considered*/
		if (C != D) {
			sum_deg_D += i.getNeighbors().size();
		}
		
		//normalization and other arithmetic
		step1output = (((double) -1 / numEdges) * (deg_i_C)) + 
				      ((deg_i_orig / (2 * Math.pow(numEdges, 2))) 
				      * sum_deg_C * gamma);
		
		step2output = (((double) 1 / numEdges) * (deg_i_D)) - 
			      ((deg_i_orig / (2 * Math.pow(numEdges, 2))) 
			      * sum_deg_D * gamma);
		
		del_Q = step1output + step2output;
		
		return del_Q;
	}
	
	/*Calculating change in modularity on moving node i from community C to
	the empty community*/
	private double del_Q_empty 
	(ComDetGraph g, Partition partition, ComDetNode i, int C, double gamma) {
		
		int numEdges = g.getNumEdges();
		int deg_i_orig = i.getNeighbors().size();
		int deg_i_C = deg_i_orig;
		int sum_deg_C = 0;
		double del_Q = 0;
		
		//degree of i in C and sum of degrees of C
		for (ComDetNode nbr : i.getNeighbors()) {
			if (!partition.getIDNodeSetMap().get(C).contains(nbr)) {
				--deg_i_C;
			} 
		}
		
		for (ComDetNode n : partition.getIDNodeSetMap().get(C)) {
			sum_deg_C += n.getNeighbors().size();
		}
		
		//change in modularity for moving i out of C
		double del_Q_1 = (((double) -1 / numEdges) * (deg_i_C)) + 
				 ((deg_i_orig / (2 * Math.pow(numEdges, 2))) *
				        sum_deg_C * gamma);
		
		//change in modularity for moving i into the empty community
		double del_Q_2_1 = ((double) -gamma / 2);
		double del_Q_2_2 = ((double) deg_i_orig / numEdges);
		double del_Q_2 = del_Q_2_1 * Math.pow(del_Q_2_2, 2);
		del_Q = del_Q_1 + del_Q_2;
		
		return del_Q;
	}
	
	//Modularity for a certain partition of the graph
	private double hLvn (ComDetGraph g, Partition partition, double gamma) {
		
		double h = 0.0;
		
		//sum of modularities for each community
		for (int C : partition.getIDNodeSetMap().keySet()) {
			h += q(g, partition, C, gamma);
		}
		
		return h;
	}
	
	//local move for the Louvain algorithm
	public Partition moveNodes 
	(ComDetGraph g, Partition partition, double gamma) {
		
		double h_old = 0.0;
		
		do {
			
			//calculating modularity of old partition
			h_old = hLvn(g, partition, gamma);
			
			//randomizing order of nodes
			List<ComDetNode> nodes = new ArrayList<>(g.getNodeMap().values());
			Collections.shuffle(nodes, new Random(123));
			
			for (ComDetNode node : nodes) {
				
				//max value of change in modularity
				double max_del_H = Double.NEGATIVE_INFINITY;
				int nC = partition.getNodeIDMap().get(node);
				
				/*map to store the change in modularity for each community
				 we might move this node to*/
				HashMap<Integer, Double> comm_to_del_H = new HashMap<>();
				
				//moving this node into each of its neighbor's communities
				for (ComDetNode nbr : node.getNeighbors()) {
					int C = partition.getNodeIDMap().get(nbr);
					
					/*ensuring communities with the same change in modularity as 
					 a community already contained in the map aren't added*/
					if (!comm_to_del_H.containsKey(C)) {
						
						/*computing change in modularity for this community,
						 * storing it and updating the max value
						 */
						double del_H = del_Q(g, partition, node, nC, C, gamma);
						comm_to_del_H.put(C, del_H);
						max_del_H = Math.max(del_H, max_del_H);
					}
				}
				
				/*computing change in modularity for moving this node into 
				 * the empty community, storing it and updating max*/
				double del_H_empty = del_Q_empty(g, partition, node, nC, gamma);
				comm_to_del_H.put(Integer.MIN_VALUE, del_H_empty);
				max_del_H = Math.max(del_H_empty, max_del_H);
				
				int newCom = Integer.MIN_VALUE;
				
				/*only if the change in modularity is positive will we perform
				 * a local move*/
				if (max_del_H > 0) {
					
					//find community corresponding to max change in modularity
					for (int C : comm_to_del_H.keySet()) {
						if (comm_to_del_H.get(C) == max_del_H) {
							newCom = C;
							break;
						}
					}
					
					//if empty community, set its ID to a new value
					if (newCom == Integer.MIN_VALUE) {
						newCom = g.getNumVertices();
						++newCom;
					}
					
					/*remove node from its old community and add it to its new
					one*/
					partition.getNodeIDMap().put(node, newCom);
					partition.getIDNodeSetMap().get(nC).remove(node);
					if (partition.getIDNodeSetMap().get(nC).isEmpty()) {
						partition.getIDNodeSetMap().remove(nC);
					}
					if (newCom == (g.getNumVertices() + 1)) {
						HashSet<ComDetNode> emptyPlOne = 
								new HashSet<>();
						emptyPlOne.add(node);
						partition.getIDNodeSetMap().
						put(newCom, emptyPlOne);
					} else {
						partition.getIDNodeSetMap().get(newCom).add(node);
					}
				}
			}
			
		} while (hLvn(g, partition, gamma) > h_old);
		
		return partition;
		
	}

	//calculate optimum partition using the Louvain algorithm
	public Partition louvain (ComDetGraph g, Partition partition, double gamma) {
		
		//modularity of initial partition
		double q_prev = hLvn(g, partition, gamma);
		
		//local move
		Partition p_new = moveNodes(g, partition, gamma);
		
		//if improvement in modularity, continue; else return partition
		if ((hLvn(g, p_new, gamma) - q_prev) >= 0.001) {
			louvain(g, p_new, gamma);
		}
		
		return p_new;
		
	}

	public static void main(String[] args) {
		//load graph and calculate final partition using Leiden algorithm
		ComDet cd = new ComDet();
		ComDetGraph g = new ComDetGraph();
		GraphLoader.loadGraph(g, "data/example_graph_changed.txt");
		Partition sP = cd.singletonPartition(g);
		Partition finalPart = cd.louvain(g, sP, 1.0);
		System.out.println(finalPart.getIDNodeSetMap());
		
	}

}
