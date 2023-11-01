package cs1501_p4;
import java.util.ArrayList;

public class ReturnValues {
    private ArrayList<Integer> path;
    private double length;

    // getters and setters here

    public ReturnValues(ArrayList<Integer> p, double l){
        this.path = p;
        this.length = l;
    }
    
    public void setPath(ArrayList<Integer> p){
        path = p;
    }
    public void setLength(int l){
        length = l;
    }

    public ArrayList<Integer> getPath(){
        return path;
    }
    public double getLength(){
        return length;
    }

}
