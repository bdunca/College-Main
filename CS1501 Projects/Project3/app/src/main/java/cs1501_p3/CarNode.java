package cs1501_p3;


public class CarNode{
	private Car car;
	private char value;
	private CarNode children;
	private CarNode sibling;
	

	public CarNode(char val){
		value = val;
	}

	public char getVal(){
		return value;
	}

	public CarNode getChildren(){
		return children;
	}

	public CarNode getSibling(){
		return sibling;
	}

	public Car getCar(){
		return car;
	}

	public void setVal(char val){
		value = val;
	}

	public void setChildren(CarNode nextReference){
		children = nextReference;
	}

	public void setSibling(CarNode nextReference){
		sibling = nextReference;
	}

	public void setCar(Car newCar){
		car = newCar;
	}
}

