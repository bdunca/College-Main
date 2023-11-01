package cs1501_p3;

public class CarsPQHelper{
    private static CarsPQ Prices;           //PQ for every car by price
    private static CarsPQ Mileages;         //PQ for every car by mileage
    private static PQDLB MakeModelPrices;   //DLB of PQs for prices for make and model
    private static PQDLB MakeModelMileages; //DLB of PQs for mileages for make and model

    public CarsPQHelper(){
        Prices = new CarsPQ('p', false);    //false = all cars, p = price
        Mileages = new CarsPQ('m', false);  //m = mileage
        MakeModelPrices = new PQDLB();
        MakeModelMileages = new PQDLB();
    }

    public void insert(Car car){
        String make = car.getMake();
        String model = car.getModel();
        String makeModel = make + model;

        Prices.add(car);
        Mileages.add(car);
        CarsPQ mmprices = MakeModelPrices.getPQ(makeModel); 
        CarsPQ mmmileages = MakeModelMileages.getPQ(makeModel); 
        if(mmprices == null){ //does not yet exist
            CarsPQ newPQ = new CarsPQ('p', true); //true = specific make/model
            MakeModelPrices.insert(makeModel, newPQ); 
            mmprices = newPQ;
        }
        if(mmmileages == null){ 
            CarsPQ newPQ = new CarsPQ('m', true); 
            MakeModelMileages.insert(makeModel, newPQ); 
            mmmileages = newPQ;
        }
        mmprices.add(car);
        mmmileages.add(car);
    }

    //Get the car with the lowest price
    public Car getLowestPrice(){
        return Prices.getMin();
    }

    //Get the car with the lowest mileage
    public Car getLowestMileage(){
        return Mileages.getMin();
    }

    //Get the car with the lowest price for a specific make + model
    public Car getLowestMMPrice(String make, String model){
        String makeModel = make + model;
        CarsPQ mmprices = MakeModelPrices.getPQ(makeModel);

        if(mmprices == null){
            return null;
        }
        return mmprices.getMin(); 
    }

    //Get the car with the lowest mileage for a specific make + model
    public Car getLowestMMMileage(String make, String model){
        String makeModel = make + model;
        CarsPQ mmmileages = MakeModelMileages.getPQ(makeModel);

        if(mmmileages == null){
            return null;
        }
        return mmmileages.getMin();
    }

    //Remove a car from the pqs
    public void remove(Car car){
        //Get car's index in each priority queue
        int pricesIndex = car.getPricesIndex();
        int mileagesIndex = car.getMileageIndex();
        int mmpricesIndex = car.getMMPriceIndex();
        int mmmileagesIndex = car.getMMMileageIndex();
        String make = car.getMake();
        String model = car.getModel();

        Prices.delete(pricesIndex);
        Mileages.delete(mileagesIndex);

        String makeModel = make + model;
        CarsPQ mmprices = MakeModelPrices.getPQ(makeModel); 
        CarsPQ mmmileages = MakeModelMileages.getPQ(makeModel); 
        if(mmprices != null){ 
            mmprices.delete(mmpricesIndex); 
        }
        if(mmmileages != null){ 
            mmmileages.delete(mmmileagesIndex);
        } 
    }

    //Update car's mileage / price in its heaps
    public void update(Car car, boolean updatedPrice){ 
        String make = car.getMake();
        String model = car.getModel();
        String makeModel = make + model;

        if(updatedPrice){ //Updated Price, so only update the car's price heaps
            int pricesIndex = car.getPricesIndex();
            int pricesMMIndex = car.getMMPriceIndex();
            Prices.update(pricesIndex);

            CarsPQ mmprices = MakeModelPrices.getPQ(makeModel);
            if(mmprices != null){
                mmprices.update(pricesMMIndex);
            } 
        } 
        else{ 
            int mileageIndex = car.getMileageIndex();
            int mileageMMIndex = car.getMMMileageIndex();
            Mileages.update(mileageIndex);

            CarsPQ mmmileages = MakeModelMileages.getPQ(makeModel);
            if(mmmileages != null){ 
                mmmileages.update(mileageMMIndex);
             } 
        }
    }
}
