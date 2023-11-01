package cs1501_p4;
import java.util.*;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

public class NetAnalysis implements NetAnalysis_Inter{

    private int V;
    private int E;
    private Vertex[] adj;
	private ArrayList<Edge> Edges;
	private boolean copperOnly = true;
	private ArrayList<STE> MinSpanningTreeWithLowestLatency;


    //Initialize graph from file  
    public NetAnalysis(String file){
		if(file == null){ 
			return;
		}
		
		File graphFile = new File(file);
		Scanner sc; 
		try{
			sc = new Scanner(graphFile);
		} 
		catch(FileNotFoundException e){
			System.out.println("File not found");
			return;
		}

		if(sc.hasNextLine()){ 
			V = Integer.parseInt(sc.nextLine());
		}
		else{
			return;
		}

		adj = new Vertex[V];
		for(int i = 0; i < adj.length; i++){
			adj[i] = new Vertex(i); 
		}
		
		Edges = new ArrayList<Edge>();

		while(sc.hasNextLine()){ //Run through the file and generate all the edges of the graph
			String line = sc.nextLine();
			String[] lineContents = line.split(" ");
			if(lineContents.length != 5){  
				continue;
			}

			//Separate the contents of the line into their respective variables to add them to edges in the graph
			Vertex vertexU = adj[Integer.parseInt(lineContents[0])];
			Vertex vertexW = adj[Integer.parseInt(lineContents[1])];
			String cableType = lineContents[2];
			int bandwidth = Integer.parseInt(lineContents[3]); //Maximum amount of data that can travel along this edge
			int length = Integer.parseInt(lineContents[4]); //Length of the edge

			//Create two new edges between the two vertices and make sure they map back and forth to eachother (they're full duplex)
			Edge edgeUtoW = new Edge(vertexU, vertexW, cableType, bandwidth, length);
			Edge edgeWtoU = new Edge(vertexW, vertexU, cableType, bandwidth, length);

			vertexU.getConnections().add(edgeUtoW);
			vertexW.getConnections().add(edgeWtoU);
			Edges.add(edgeUtoW); //for minimum spanning trees
			E++;

			if(cableType.equals("optical")){ 
				copperOnly = false; 
			}
		}
	}

    /**
	 * Find the lowest latency path from vertex `u` to vertex `w` in the graph
	 *
	 * @param	u Starting vertex
	 * @param	w Destination vertex
	 *
	 * @return	ArrayList<Integer> A list of the vertex id's representing the
	 * 			path (should start with `u` and end with `w`)
	 * 			Return `null` if no path exists
	 */
	public ArrayList<Integer> lowestLatencyPath(int u, int w){
        ArrayList<Integer> lowestLatency = new ArrayList<Integer>();
		Vertex startVertex = null;
		Vertex endVertex = null;

		if (u < 0 || u > V || w >= V){
			return null; 
		}

		// if going to itself, just have itself in the chain
		if (u == w){
			lowestLatency.add(u);
			lowestLatency.add(w);

			return lowestLatency; 
		}

		else{	 
			startVertex = adj[u];
			endVertex = adj[w];
		}

		//Calculate the shortest path between the two vertices
		ArrayList<Integer> startArray = new ArrayList<Integer>();
		startArray.add(u);
		lowestLatency = shortestPathBetweenTwoVertices(startVertex, endVertex, startArray, 0.0).getPath();
		if(lowestLatency == null){return null;}	
		return lowestLatency;
	}


	private ReturnValues shortestPathBetweenTwoVertices(Vertex u, Vertex w, ArrayList<Integer> path, double length){
		if(u == null || w == null || path == null || length < 0.0){ 
			return null; 
		}
		if(u == w){ //reached end
			return new ReturnValues(path, length);
		}

		ArrayList<Edge> temp = u.getConnections();
		for(Edge edge : temp){
			if(edge.other(u) == w){
				ArrayList<Integer> resList = new ArrayList<Integer>();
				resList.add(u.getID());
				resList.add(w.getID());
				return new ReturnValues(resList, 0);
			}
		}

		ArrayList<Edge> currEdges = u.getConnections(); //Grab all of the edges at this node

		double minLength = -1.0; //Length of the minimum length path
		ArrayList<Integer> minLengthPath = new ArrayList<Integer>();
		ArrayList<Integer> newPath = new ArrayList<Integer>();

		for(Edge edge : currEdges){ //Iterate over all of the edges
			Vertex edgeDestination = edge.getDestination();

			if(!path.contains(edgeDestination.getID())){ 
				for(int id : path){
					newPath.add(id);
				} 
				newPath.add(edgeDestination.getID());
				double newLength = 0.0;
				if(edge.getCableType().equals("copper")){
					newLength = length + edge.getLatency();

				} else if(edge.getCableType().equals("optical")){
					newLength = length + edge.getLatency();

				} else{
					return null; //Invalid wire material
				}

				ReturnValues pathData = shortestPathBetweenTwoVertices(edgeDestination, w, newPath, newLength); //Recursively traverse to the next path
				if(pathData == null){ 
					continue; //If there is no path data for the edge, go to the next edge
				}
			
				double pathLength = pathData.getLength(); 

				//Set the new minimum path length
				if(minLength == -1 || pathLength < minLength){ 
					minLength = pathLength;
					minLengthPath = pathData.getPath();
				}
			}
		}

		if(minLength > -1.0){ //A path to reach the destination from the current vertex exists		
			return new ReturnValues(minLengthPath, minLength);
		}
		return null; //not at the destination and no edges from the current vertex are valid
	}
	
	/**
	 * Find the bandwidth available along a given path through the graph
	 * (the minimum bandwidth of any edge in the path). Should throw an
	 * `IllegalArgumentException` if the specified path is not valid for
	 * the graph.
	 *
	 * @param	ArrayList<Integer> A list of the vertex id's representing the
	 * 			path
	 *
	 * @return	int The bandwidth available along the specified path
	 */
	public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException{
		int minBandwidth = 0;
		for(int n = 0, m = 1; n < p.size()-1; n++, m++){
			if(hasPath(p.get(n), p.get(m))){
				continue;
			}
			else{
				throw new IllegalArgumentException("Path does not exist");
			}
		}
		for(int i = 0, j=1; i < p.size()-1; i++, j++){
			int newBandwidth = bandwidthAtEdge(adj[p.get(i)], adj[p.get(j)]);
			if(minBandwidth == 0 || newBandwidth<minBandwidth){
				minBandwidth = newBandwidth;
			}
		}
		return minBandwidth;
	}

	private boolean hasPath(int s, int t) {
		Vertex start = adj[s];
		Vertex end = adj[t];
		ArrayList<Edge> curr = start.getConnections();
		for(Edge edge : curr){
			if(edge.other(start)==end){
				return true;
			}
		}
		return false;
	}

	private int bandwidthAtEdge(Vertex curr, Vertex dest){
		int bandwidth = 0;
		if(curr == null || dest == null) return -1; //Any invalid input will return null promptly

		if(curr == dest){ //The destination vertex has been reached!  Return the necessary path data
			return 0;
		}
		ArrayList<Edge> temp = curr.getConnections();
		for(Edge edge : temp){
			if(edge.other(curr) == dest){
				bandwidth = edge.bandwidth();
			}
		}
		return bandwidth;
	}

	/**
	 * Return `true` if the graph is connected considering only copper links
	 * `false` otherwise
	 *
	 * @return	boolean Whether the graph is copper-only connected
	 */
	public boolean copperOnlyConnected(){
		if(copperOnly){ //The graph consists of only copper wires
			return true;
		} 
		else{
			for(int i = 0; i < V; i++){ //Iterate through every vertex and check to make sure it has at least one copper connection
				ArrayList<Edge> vertexEdges = adj[i].getConnections();
				boolean hasCopperConnection = false;
				for(int j = 0; j < V; j++){
					if(vertexEdges.get(j).getCableType().equals("copper")){ //a copper wire from here exists
						hasCopperConnection = true;
						break;
					}
				}

				if(!hasCopperConnection){ //If vertex does not have any copper connections, the graph is not copper connect
					return false;
				}
			}
		}
		return true;
	}	

	/**
	 * Return `true` if the graph would remain connected if any two vertices in
	 * the graph would fail, `false` otherwise
	 *
	 * @return	boolean Whether the graph would remain connected for any two
	 * 			failed vertices
	 */
	public boolean connectedTwoVertFail(){
		for(int i = 0; i < V-1; i++){
			for(int j = i+1; j < V; j++){
				//Traverse the graph ignoring Vertices[i] and Vertices[j]
				//If the path's length ever reaches NumVertices-2, then it is connected
				Vertex startVertex = null;
				Vertex failureOne = adj[i];
				Vertex failureTwo = adj[j];
				boolean[] visited = new boolean[V];

				//Mark that the failed vertices have already been visited so the traversal doesn't visit them
				visited[failureOne.getID()] = true;
				visited[failureTwo.getID()] = true;

				//Set the starting vertex to explore from; it can be any vertex that isn't in the pair of failing vertices
				if(i != 0){ //If we're omitting vertex 0, then make sure we don't start there
					startVertex = adj[0];
				} 
				else{ //Vertex 0 failed, so determine a new vertex that didn't fail
					if(j != V-1){
						startVertex = adj[j+1];
					} else if(j-i != 1){
						startVertex = adj[j-1];
					} else{
						return false;
					}
				}

				//Pass in the visited array and mark the vertices that were traversed across
				findConnWithoutTwoVert(startVertex, failureOne, failureTwo, visited);

				//Check to make sure all vertices were visited; in other words, the graph is still connected despite the failures
				boolean graphIsConnected = true;
				for(int k = 0; k < visited.length; k++){
					if(visited[k] == false){ //A node was not visited, so the graph is not connected
						graphIsConnected = false;
						break;
					}
				}

				if(!graphIsConnected){ //If any two pairs of vertices failing causes the graph to not be connected, return false
					return false;
				}
			}
		}
		return true; //All possible combinations of two vertices failing produced connected graphs
	}

	private void findConnWithoutTwoVert(Vertex curr, Vertex a, Vertex b, boolean[] visited){
		if(curr == null || a == null || b == null || visited == null) return; 

		if(visited[curr.getID()] == true){ //This node has been visited
			return;
		}

		visited[curr.getID()] = true; //Mark that this node has now been visited

		ArrayList<Edge> currEdges = curr.getConnections();

		for(Edge edge : currEdges){ //Perform a DFS to traverse all nodes except for the failed ones in the graph
			Vertex edgeDestination = edge.other(curr); //Get the destination Vertex of this current edge
			if(visited[edgeDestination.getID()] == true) continue; //already traversed to this node, so don't go back to it

			findConnWithoutTwoVert(edgeDestination, a, b, visited); //Recursively traverse the graph
		}
		return;
	}

	/**
	 * Find the lowest average (mean) latency spanning tree for the graph
	 * (i.e., a spanning tree with the lowest average latency per edge). Return
	 * it as an ArrayList of STE edges.
	 *
	 * Note that you do not need to use the STE class to represent your graph
	 * internally, you only need to use it to construct return values for this
	 * method.
	 *
	 * @return	ArrayList<STE> A list of STE objects representing the lowest
	 * 			average latency spanning tree
	 * 			Return `null` if the graph is not connected
	 */
	public ArrayList<STE> lowestAvgLatST(){
		int[] parent = new int[V];
        byte[] rank = new byte[V];
        for (int i = 0; i < V; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

		//sort the edges from min to max weight
		Collections.sort(Edges, (e1, e2) -> e1.compareTo(e2));

		MinSpanningTreeWithLowestLatency = new ArrayList<STE>();
		double weight = 0.0; 

        //Kruskal
		int currEdge = 0; 
        while(currEdge != Edges.size()-1 && MinSpanningTreeWithLowestLatency.size() < V - 1){ 
            Edge e = Edges.get(currEdge);
            int u = e.getSource().getID();
            int w = e.getDestination().getID();
            if(!connected(u, w, parent)){ //not cyclic
                union(u, w, parent, rank); //add the edge (u,w) to the minimum spanning tree's union of all its edges
                MinSpanningTreeWithLowestLatency.add(new STE(u,w));
                weight += e.getLatency();
            }

			currEdge++; //In the next iteration, look at the next edge in the tree
        }

		return MinSpanningTreeWithLowestLatency;
	}
	private boolean connected(int p, int q, int[] parent) {
        return find(p, parent) == find(q, parent);
    }

	private int find(int p, int[] parent) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]]; 
            p = parent[p];
        }
        return p;
    }

	private void union(int p, int q, int[] parent, byte[] rank) {
        int rootP = find(p, parent);
        int rootQ = find(q, parent);
        if (rootP == rootQ) return;

        //Make root of smaller rank point to root of larger rank
        if      (rank[rootP] < rank[rootQ]) parent[rootP] = rootQ;
        else if (rank[rootP] > rank[rootQ]) parent[rootQ] = rootP;
        else {
            parent[rootQ] = rootP;
            rank[rootP]++;
        }
    }
}