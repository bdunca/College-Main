package cs1501_p3;

import java.io.*;
import java.util.NoSuchElementException;

public class CarsPQ implements CarsPQ_Inter {
	private String vin;
	private String Make;
	private String Model;
	private int Price;
	private int Mileage;
	private String Color;

	private int n;
	private Car[] heap;
	
	private char filter; // m = mileages, p = prices
	private boolean MakeModelHeap; // true = make/models, false = all

	private CarsPQHelper carsManager;
	private vinDLB knownCars;

	public CarsPQ(char Filter, boolean makeModelHeap) {
		n = 0; // Number of items initially in the heap is 0
		filter = Filter;
		MakeModelHeap = makeModelHeap;
		heap = new Car[511];
	}

	public CarsPQ(String cars) {
		n = 0;
		heap = new Car[511];
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cars)))) {
			String line;

			CarsPQHelper carsManager = new CarsPQHelper();
			vinDLB knownCars = new vinDLB();

			while ((line = br.readLine()) != null) {
				String[] split = line.split(":");
				if (split[0].equals("# VIN")) { // ignore labels at beginning of file
					continue;
				}

				vin = split[0];
				Make = split[1];
				Model = split[2];
				Price = Integer.parseInt(split[3]);
				Mileage = Integer.parseInt(split[4]);
				Color = split[5];

				Car newCar = new Car(vin, Make, Model, Price, Mileage, Color);
				//System.out.println(newCar.toString());
				carsManager.insert(newCar);
				knownCars.insert(newCar);
				n++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a new Car to the data structure
	 * Should throw an `IllegalStateException` if there is already car with the
	 * same VIN in the datastructure.
	 *
	 * @param c Car to be added to the data structure
	 */
	public void add(Car c) throws IllegalStateException {
		if (c == null) {
			return;
		}
		n++;
		if (n >= heap.length) {
			resizeHeap();
		}
/*
		if (knownCars.exists(c.getVIN())) {
			throw new IllegalStateException();
		}
*/
		heap[n] = c; // Insert the car
		swim(n); // Place in correct position based on mileage / price
	}

	public void resizeHeap() {
		Car[] newHeap = new Car[n * 2]; // Double size
		for (int i = 0; i <= n; i++) { // Copy to new heap
			newHeap[i] = heap[i];
		}
	}

	public void delete(int i) {
		if (i < 0)
			throw new IndexOutOfBoundsException();
		exch(i, n--); // Place the car at the bottom of the heap
		swim(i); // move the swapped in car to the top
		sink(i); // sink it down to right position
		heap[n + 1] = null; // set deleted car to null
	}

	/**
	 * Retrieve a new Car from the data structure
	 * Should throw a `NoSuchElementException` if there is no car with the
	 * specified VIN in the datastructure.
	 *
	 * @param vin VIN number of the car to be updated
	 */
	public Car get(String vin) throws NoSuchElementException {
		if (!knownCars.exists(vin)) {
			throw new NoSuchElementException();
		}
		Car car = knownCars.getCar(vin);
		return car;
		
	}

	public void update(int index) {
		if (index < 0){
			throw new IndexOutOfBoundsException();
		}
		swim(index); // Move updated car to top
		sink(index); // sink it down to right position
	}

	/**
	 * Update the price attribute of a given car
	 * Should throw a `NoSuchElementException` if there is no car with the
	 * specified VIN in the datastructure.
	 *
	 * @param vin      VIN number of the car to be updated
	 * @param newPrice The updated price value
	 */
	public void updatePrice(String vin, int newPrice) throws NoSuchElementException {
		if (!knownCars.exists(vin)) {
			throw new NoSuchElementException();
		}
		Car car = knownCars.getCar(vin);
		car.setPrice(newPrice);
		carsManager.update(car, true);
	}

	/**
	 * Update the mileage attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the
	 * specified VIN in the datastructure.
	 *
	 * @param vin        VIN number of the car to be updated
	 * @param newMileage The updated mileage value
	 */
	public void updateMileage(String vin, int newMileage) throws NoSuchElementException {
		if (!knownCars.exists(vin)) {
			throw new NoSuchElementException();
		}
		Car car = knownCars.getCar(vin);
		car.setMileage(newMileage);
		carsManager.update(car, false);
	}

	/**
	 * Update the color attribute of a given car
	 * Should throw a `NoSuchElementException` if there is not car with the
	 * specified VIN in the datastructure.
	 *
	 * @param vin      VIN number of the car to be updated
	 * @param newColor The updated color value
	 */
	public void updateColor(String vin, String newColor) throws NoSuchElementException {
		if (!knownCars.exists(vin)) {
			throw new NoSuchElementException();
		}
		Car car = knownCars.getCar(vin);
		car.setColor(newColor);
	}

	/**
	 * Remove a car from the data structure
	 * Should throw a `NoSuchElementException` if there is not car with the
	 * specified VIN in the datastructure.
	 *
	 * @param vin VIN number of the car to be removed
	 */
	public void remove(String vin) throws NoSuchElementException {
		if (!knownCars.exists(vin)) {
			throw new NoSuchElementException();
		}
		Car car = knownCars.getCar(vin);
		/*
		if (car == null) {
			throw new NoSuchElementException();
		}
		*/
		carsManager.remove(car);
		knownCars.remove(vin);
	}

	public Car getMin() {
		if (n == 0)
			return null;
		return heap[1]; // return highest priority
	}

	/**
	 * Get the lowest priced car (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return Car object representing the lowest priced car
	 */
	public Car getLowPrice() {
		Car lowPrice = carsManager.getLowestPrice();
		if (lowPrice == null) {
			return null;
		} else {
			return lowPrice;
		}
	}

	/**
	 * Get the lowest priced car of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param make  The specified make
	 * @param model The specified model
	 * 
	 * @return Car object representing the lowest priced car
	 */
	public Car getLowPrice(String make, String model) {
		Car lowestPriceMM = carsManager.getLowestMMPrice(make, model);

		if (lowestPriceMM == null) {
			return null;
		}
		return lowestPriceMM;
	}

	/**
	 * Get the car with the lowest mileage (across all makes and models)
	 * Should return `null` if the data structure is empty
	 *
	 * @return Car object representing the lowest mileage car
	 */
	public Car getLowMileage() {
		Car lowMileage = carsManager.getLowestMileage();

		if (lowMileage == null) {
			return null;
		} else {
			return lowMileage;
		}
	}

	/**
	 * Get the car with the lowest mileage of a given make and model
	 * Should return `null` if the data structure is empty
	 *
	 * @param make  The specified make
	 * @param model The specified model
	 *
	 * @return Car object representing the lowest mileage car
	 */
	public Car getLowMileage(String make, String model) {
		Car lowestMileageMM = carsManager.getLowestMMMileage(make, model);

		if (lowestMileageMM == null) {
			return null;
		}
		return lowestMileageMM;
	}

	private void setNewCarHeapIndex(int j) {
		if (filter == 'm') {
			if (MakeModelHeap)
				heap[j].setMMMileageIndex(j); // Set the index for the heap for this make/model
			else
				heap[j].setMileageIndex(j); // Set the index for the heap for ALL cars
		} else if (filter == 'p') { // Set the price index
			if (MakeModelHeap)
				heap[j].setMMPriceIndex(j); // Set the index for the heap for this make/model
			else
				heap[j].setPriceIndex(j); // Set the index for the heap for ALL cars
		}
	}

	private void exch(int i, int j) {
		Car swap = heap[i];
		heap[i] = heap[j];
		heap[j] = swap;

		// Allows the cars to be indexable in this current heap, so update the index
		// variables for these cars
		setNewCarHeapIndex(i);
		setNewCarHeapIndex(j);
	}

	private boolean greater(int i, int j) {
		if (filter == 'm')
			return heap[i].getMileage() > heap[j].getMileage(); // Compare mileages of two cars
		else if (filter == 'p')
			return heap[i].getPrice() > heap[j].getPrice(); // Compare prices of two cars
		else
			return false; // If the heap is in any other mode, return false
	}

	private void swim(int k) {
		while (k > 1 && greater(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}

		setNewCarHeapIndex(k);
	}

	private void sink(int k) {
		while (2 * k <= n) {
			int j = 2 * k;
			if (j < n && greater(j, j + 1))
				j++;
			if (!greater(k, j))
				break;
			exch(k, j);
			k = j;
		}

		setNewCarHeapIndex(k);
	}

}