package cs1501_p4;

public class Edge{
    private Vertex u;
    private Vertex w;
    private String cableType;
    private int cableSpeed;
    private int bandwidth;
    private int length;
    private double latency;

    public Edge(Vertex u, Vertex w, String cableType, int bandwidth, int length){
        if(u == null || w == null || length == 0){
            throw new IllegalArgumentException();
        }
        this.u = u;
        this.w = w;
        this.bandwidth = bandwidth;
        this.length = length;
        this.cableType = cableType;

        if(cableType.equals("optical")){
            this.cableSpeed = 200000000; //meters per second
        }
        else if(cableType.equals("copper")){
            this.cableSpeed =  230000000; //meters per second
        }
    }
    public int bandwidth() {
        return bandwidth;
    }
    public int cableSpeed(){
        return cableSpeed;
    }
    public double getLatency(){
        double temp = (double) length;
        latency = temp/(cableSpeed / 1000000);
        return latency;
    }
    public String getCableType(){
        return cableType;
    }
    /**
     * Returns either endpoint of this edge.
     *
     * @return either endpoint of this edge
     */
    public Vertex either() {
        return u;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex.
     *
     * @param  vertex one endpoint of this edge
     * @return the other endpoint of this edge
     * @throws IllegalArgumentException if the vertex is not one of the
     *         endpoints of this edge
     */
    public Vertex other(Vertex vertex) {
        if      (vertex == u) return w;
        else if (vertex == w) return u;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Compares two edges by weight.
     * Note that {@code compareTo()} is not consistent with {@code equals()},
     * which uses the reference equality implementation inherited from {@code Object}.
     *
     * @param  that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *         the weight of this is less than, equal to, or greater than the
     *         argument edge
     */
   
    public int compareTo(Edge that) {
        return Double.compare(this.bandwidth, that.bandwidth);
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representation of this edge
     */
    public String toString() {
        return String.format("%d-%d %.5f", u, w, bandwidth);
    }


    public Vertex getSource(){
		return u;
	}
    public Vertex getDestination(){
		return w;
	}

}