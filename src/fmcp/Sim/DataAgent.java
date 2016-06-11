package fmcp.Sim;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

public class DataAgent extends DataVictim{
	private final double velocityMean = 0.7;
	private final double velocityStd = 0.1;
	protected final double velocity;
	private TreeMap<Double, DataVictim> missions;
	
	
	public DataAgent(int time, EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		super(time, id, Hp, damage, position, buriedness, location);
		//values from configuration files
		// taking worst case withaddTask ~0.95 (normal distribution)
		velocity = velocityMean - (3 * velocityStd); //values from configuration files
	}
	
	
	//////////////////////////////////////////////////////
	
	
	
	//////////////////////////////////////////////////////
	public List<EntityID> getTasksByOrder() {
		List<EntityID> ans= new Vector<EntityID>();
		for (DataVictim val: missions.values()) {
			ans.add(val.getId());
		}
		Collections.reverse(ans);
		return ans;
	}
	
	public int timeToFinishTransport (StandardWorldModel centerModel) {
		if (isTransporting()) {
			return DataList.timeToRefuge(this, centerModel, this);
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


	public void addTask(Double priority, DataVictim victim) {
		missions.put(priority, victim);
	}
	
	public void clearTasks() {
		missions.clear();
	}
	
	public boolean hasMoreTasks () {
		return !missions.isEmpty();
	}
	
	
}
