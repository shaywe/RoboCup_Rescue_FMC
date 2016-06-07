package fmcp.Sim;
import java.util.Queue;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

public class DataAgent extends DataVictim{
	private final double velocityMean = 0.7;
	private final double velocityStd = 0.1;
	protected final double velocity;
	private Queue<DataVictim> missions;
	
	
	public DataAgent(EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		super(id, Hp, damage, position, buriedness, location);
		//values from configuration files
		// taking worst case with ~0.95 (normal distribution)
		velocity = velocityMean - (3 * velocityStd); //values from configuration files
	}
	
	
	//////////////////////////////////////////////////////
	
	
	
	//////////////////////////////////////////////////////
	public int timeToFinishTransport () {
		if (isTransporting()) {
			return ...
		}
		else {
			return 0;
		}
	}
	public boolean isTransporting () {
		return this.status == Status.TRANSPORTING_VICTIM;
	}
	
	public void setTransporting () {
		this.status = Status.TRANSPORTING_VICTIM;
	}
	
	// velocity
	public double getVelocity () {
		return this.velocity;
	}
	
	
}
