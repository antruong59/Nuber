package nuber.students;

public class Driver extends Person {

	private Passenger passenger;
	private int driverTripTravelTime = 0;
	
	public Driver(String driverName, int maxSleep)
	{
		super(driverName, maxSleep);
		System.out.println("New Driver --> " + this.toString());
	}
	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
	public void pickUpPassenger(Passenger newPassenger) throws InterruptedException
	{
		passenger = newPassenger;
		System.out.println("Passenger " + passenger.name + " was assigned to " + this.name);
		int delay = delay(0, maxSleep);
		driverTripTravelTime = delay;
		System.out.println("Delay "+ delay + " --> Driver " + this.name + " picked up Passenger " + passenger.name);
		
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination() throws InterruptedException {
		int tripTravelTime = passenger.getTravelTime();
		System.out.println(this.name + " + " + passenger.name + " travel time = " + tripTravelTime);
		Thread.sleep(tripTravelTime);
		driverTripTravelTime += tripTravelTime;
		System.out.println(this.name + " + " + passenger.name + " trip has arrived");
	}
	
	private int delay(int minDelay, int maxDelay) {
		int actualDelay = 0;
		try {
			int range = (maxDelay - minDelay) + 1;
			// thread to sleep for random milliseconds
			actualDelay = (int)(Math.random() * range) + minDelay;
			//System.out.println("Actual delay is "+actualDelay);
			Thread.sleep(actualDelay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actualDelay;
	}
	
}
