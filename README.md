The Louvain Algorithm is a fast, scalable algorithm for detecting communities in graphs. The algorithm generally involves a graph aggregation step; this project finds the communities without creating new objects to store the aggregated graphs. This was created as a capstone project for the Coursera specialization "Object Oriented Java Programming: Data Structures and Beyond".

This project was written in Java and developed using the Eclipse IDE. Here, I apply the Louvain algorithm to detect communities in Facebook data representing friendships between users on a single day at the UC San Diego campus in 2005. 

BASIC GRAPH STRUCTURE:

Two classes, the GraphLoader class in the util package and the Graph interface in the graph package, are used to load the graph and define the basic structure of a graph with integer-valued nodes. The data folder stores the graphs as text files representing undirected, unweighted graphs. Each line in a graph text file consists of two integers separated by a space, representing the two nodes connected by an undirected edge. The example_graph and facebook_1000 text files are smaller graphs to test correctness and efficiency; facebook_ucsd contains the main graph used in the project. 

The ComDetNode, ComDetEdge and ComDetGraph classes represent nodes, edges and the graph itself as user-defined data types and flesh out the graph structure. ComDetGraph implements the Graph interface and provides the implementation of the addVertex() and addEdge() methods used by GraphLoader to load the graph from the text file. The Partition class represents a partition of the graph, as defined in the context of the Louvain algorithm, as a user-defined data type. ComDet defines the methods required for implementing the Louvain algorithm, and possesses a main method for loading the graph and running the Louvain algorithm on it.

The Graph interface declares both the addVertex() and addEdge() methods, as well as methods to find egonets and strongly connected components (used in a previous course assignment), and an exportGraph() method to represent the graph in a readable format. It defines these methods assuming the graph to possess integer-valued nodes.

The GraphLoader class receives an object of the Graph interface and a file name as constructor arguments. These are passed in from the main method of the ComDet class. A set keeps track of previously unseen nodes. If either or both nodes have not been added already, the previously unseen node(s) are added and an edge is added between them.

The ComDetNode class stores an integer as the node value and a set of other ComDetNode objects as neighbors. Since nodes will constantly be moved between sets in the current partition during the runtime of the algorithm, the .hashCode() and .equals() methods are overridden to allow this to happen without errors. The class constructor sets the value of the node and initializes its set of neighbors. Getters and setters are also present as required.

The ComDetEdge class contains the nodes comprising the edge. It is used to store the edges of the graph as a set, making it easier to keep track of the total number of edges in the graph.

The ComDetGraph class contains a HashMap mapping integer values to the corresponding nodes in the graph, and a set containing the edges. It initializes these data structures when an object is constructed. The addVertex() and addEdge() methods are implemented here, as well as some getters and setters.

LOUVAIN ALGORITHM IMPLEMENTATION:

The Partition class is used specifically to represent the concept of a partition as defined in the context of the Louvain algorithm. A partition is a division of the graph into sets of nodes, or communities. While this representation is used in the pseudocode, it is required multiple times during the runtime of the algorithm to efficiently perform two operations: 1) given a node, find the community associated with it, and 2) given a community, find the set of nodes associated with it. Thus, a partition is more efficiently represented by mapping each node to a community ID (an integer), and then mapping that community ID to the set of nodes representing the community. Thus, the Partition class contains two HashMaps which store these mappings. The class also contains getters and setters for each of these maps, as well as methods to perform the two operations mentioned above.

The ComDet class is the class that contains the methods written in the pseudocode for the Louvain algorithm, along with some private helper methods. 

The first step of implementing the Louvain algorithm is creating an initial partition; this partition is generally the singleton partition. In a singleton partition, each node is assigned to a separate community. The method singletonPartition() in the ComDet class carries out this operation. It takes the graph as an argument and iterates over all nodes, giving each node a community ID and mapping its community ID to a set containing only that particular node. 

After the singleton partition is created, the graph and the singleton partition are passed to the louvain() method, along with a parameter named gamma. This parameter is generally referred to as the resolution parameter, and controls the sizes of the communities formed. Here, gamma is set to 1 as in the initial paper introducing the Louvain algorithm. 

Since the Louvain algorithm mathematically defines a community as a set of nodes with more edges internal to it than external, it uses a quality function to measure the quality of a partition. This quality function is called modularity. Modularity is calculated by summing a certain quantity over all communities. This quantity is the difference between the fraction of edges that are internal to the community and the fraction of edges that would be internal to the community if edges were distributed at random in the graph.

The louvain() method calculates the modularity of the initial partition and performs a "local move". In this local move, the nodes are moved from their own communities to other communities if such a movement increases the modularity of the partition. If the improvement in modularity is above a certain threshold (here, 0.001), the method is recursively called to repeat the entire process; otherwise execution terminates and the most recently obtained partition is returned.

The hLvn() method is the method called by louvain() to calculate the modularity of the partition. It takes the same arguments as the louvain() method. It iterates over all communities in the partition, summing up the modularity for each. The modularity per community is calculated by the method q(), which takes all the parameters of the hLvn() method as well as the community ID. q() applies the formula for modularity, which can be expressed in terms of the number of edges internal to the community, the sum of edge count for each node in the community and the total number of edges in the graph. hLvn() and q() are both private helper methods. 

The moveNodes() method is the next method called by louvain() after the modularity of the partition is calculated. moveNodes() iterates over the nodes in random order, examining the change in modularity for moving each node to its neighbors' communities, as well as to the empty community. The private helper methods del_Q and del_Q_empty calculate these changes. After this, if the maximum change in modularity is positive, moveNodes() shifts the node it's currently iterating over to the appropriate community and moves to the next iteration.

When the algorithm finishes running, a map mapping community IDs to sets of nodes is extracted from the returned Partition object in the main() method and printed out. 
