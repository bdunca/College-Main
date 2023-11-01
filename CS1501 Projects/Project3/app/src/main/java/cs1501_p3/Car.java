package cs1501_p3;

public class Car implements Car_Inter{

    private String vin;
	private String make;
	private String model;
	private int price;
	private int mileage;
	private String color;

	private int PricesHeapIndex = -1; //Index in the heap for the priority queue with ALL prices
	private int MileageHeapIndex = -1; //Index in the heap for the priority queue with ALL mileages
	private int MakeModelPriceHeapIndex = -1; //Index in the heap for the priority queue with this car's specific make/model's prices
	private int MakeModelMileageHeapIndex = -1; //Index in the heap for the priority queue with this car's specific make/model's mileages

    public Car(String VIN, String Make, String Model, int Price, int Mileage, String Color){
        vin = VIN;
    	make = Make;
        model = Model;
        price = Price;
        mileage = Mileage;
        color = Color;
	}
    
    /**
	 * Getter for the VIN attribute
	 *
	 * @return 	String The VIN
	 */
	public String getVIN(){
        return vin;
    }

	/**
	 * Getter for the make attribute
	 *
	 * @return 	String The make
	 */
	public String getMake(){
        return make;
    }

	/**
	 * Getter for the model attribute
	 *
	 * @return 	String The model
	 */
	public String getModel(){
        return model;
    }

	/**
	 * Getter for the price attribute
	 *
	 * @return 	String The price
	 */
	public int getPrice(){
        return price;
    }

	/**
	 * Getter for the mileage attribute
	 *
	 * @return 	String The mileage
	 */
	public int getMileage(){
        return mileage;
    }

	/**
	 * Getter for the color attribute
	 *
	 * @return 	String The color
	 */
	public String getColor(){
        return this.color;
    }

	/**
	 * Setter for the price attribute
	 *
	 * @param 	newPrice The new Price
	 */
	public void setPrice(int newPrice){
        price = newPrice;
    }

	/**
	 * Setter for the mileage attribute
	 *
	 * @param 	newMileage The new Mileage
	 */
	public void setMileage(int newMileage){
        mileage = newMileage;
    }

	/**
	 * Setter for the color attribute
	 *
	 * @param 	newColor The new color
	 */
	public void setColor(String newColor){
        this.color = newColor;
    }

	public int getPricesIndex(){
        return PricesHeapIndex;
    }

    public int getMileageIndex(){
        return MileageHeapIndex;
    }

    public int getMMPriceIndex(){
        return MakeModelPriceHeapIndex;
    }

    public int getMMMileageIndex(){
        return MakeModelMileageHeapIndex;
    }

	public void setPriceIndex(int index){
        PricesHeapIndex = index;
    }

    public void setMileageIndex(int index){
        MileageHeapIndex = index;
    }

    public void setMMPriceIndex(int index){
        MakeModelPriceHeapIndex = index;
    }

    public void setMMMileageIndex(int index){
        MakeModelMileageHeapIndex = index;
	}

	public String toString(){
        String output = "vin: " + vin + "\nMake: " + make + "\nModel: " + model + "\nColor: " + color + "\nMileage: " + mileage + "\nPrice: " + price + "\n";
        return output;
    }
}