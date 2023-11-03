package nuber.students;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 * 
 * @author james
 *
 */
public class NuberDispatch {

	/**
	 * The maximum number of idle drivers that can be awaiting a booking 
	 */
	private final int MAX_DRIVERS = 999;
	
	private boolean logEvents = false;
	private BlockingQueue<Driver> idleDriversQueue;
	private HashMap<String, NuberRegion> regions;
	private int awaitingBooking = 0;
	
	/**
	 * Creates a new dispatch objects and instantiates the required regions and any other objects required.
	 * It should be able to handle a variable number of regions based on the HashMap provided.
	 * 
	 * @param regionInfo Map of region names and the max simultaneous bookings they can handle
	 * @param logEvents Whether logEvent should print out events passed to it
	 */
	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
	{
		this.logEvents = logEvents;
		regions = new HashMap<String, NuberRegion> ();
		idleDriversQueue = new ArrayBlockingQueue<Driver>(MAX_DRIVERS);
		System.out.println("\nNew Nuber Dispatch...");
		
		for (String i : regionInfo.keySet()) {
			System.out.println("Creating Nuber region for " + i + "...");
			regions.put(i, new NuberRegion(this, i, regionInfo.get(i)));
		}
		
		System.out.println("Create total " + regions.size() + ((regions.size() > 1) ? " regions" : " region"));
	}
	
	/**
	 * Adds drivers to a queue of idle driver.
	 *  
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @param The driver to add to the queue.
	 * @return Returns true if driver was added to the queue
	 */
	public boolean addDriver(Driver newDriver)
	{
		// Add driver into queue and wait till available
		
		try {
			idleDriversQueue.put(newDriver); 
			System.out.println("Add available driver " + newDriver.name);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Gets a driver from the front of the queue
	 *  
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @return A driver that has been removed from the queue
	 */
	public synchronized Driver getDriver()
	{
		// Retrieves and removes the head of driver queue, 
		// give up if no driver in queue
		// wait for available spot if needed (queue empty)
		
		System.out.println("Searching driver...");
		if (idleDriversQueue.isEmpty())
			System.out.println("No driver available");
	
		else {
			try {
				System.out.println("Found driver");				
				awaitingBooking --;
				return idleDriversQueue.take();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null; 
	}

	/**
	 * Prints out the string
	 * 	    booking + ": " + message
	 * to the standard output only if the logEvents variable passed into the constructor was true
	 * 
	 * @param booking The booking that's responsible for the event occurring
	 * @param message The message to show
	 */
	public void logEvent(Booking booking, String message) {
		
		if (!logEvents) return;
		
		System.out.println(booking + " : " + message);
		
	}

	/**
	 * Books a given passenger into a given Nuber region.
	 * 
	 * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
	 * 
	 * If the region has been asked to shutdown, the booking should be rejected, and null returned.
	 * 
	 * @param passenger The passenger to book
	 * @param region The region to book them into
	 * @return returns a Future<BookingResult> object
	 */
	public Future<BookingResult> bookPassenger(Passenger passenger, String region) {
		// Region not exist
		if (regions.get(region) == null) {
			System.out.println("Region doesn't exist");
			return null;
		}
		
		// Try to book with a registered region
		Future<BookingResult> future = regions.get(region).bookPassenger(passenger);
		if (future == null) {
			System.out.println("Region has been shutdowned");

		} else {
			awaitingBooking ++;
		}
		return future;
	}

	/**
	 * Gets the number of non-completed bookings that are awaiting a driver from dispatch
	 * 
	 * Once a driver is given to a booking, the value in this counter should be reduced by one
	 * 
	 * @return Number of bookings awaiting driver, across ALL regions
	 */
	public int getBookingsAwaitingDriver()
	{
		return awaitingBooking;
	}
	
	/**
	 * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
	 */
	public void shutdown() {
		for (NuberRegion region : regions.values()) {
			region.shutdown();
		}
	}

}
