package cs1501_p4;
import java.util.ArrayList;

public class Vertex{
    private ArrayList<Edge> Connections;
	private int ID; 

	public Vertex(int id){
		ID = id;
		Connections = new ArrayList<Edge>();
	}

	public ArrayList<Edge> getConnections(){
		return Connections;
	}

	public int getID(){
		return ID;
	}
}