package nuber.students;

public class Passenger extends Person
{
	
	public Passenger(String name, int maxSleep) {
		super(name, maxSleep);
		System.out.println("New Passenger --> " + this.toString());
	}

	public int getTravelTime()
	{
		return (int)(Math.random() * maxSleep);
	}

}
