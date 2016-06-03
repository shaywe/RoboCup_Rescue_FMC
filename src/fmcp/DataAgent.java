package fmcp;
import java.util.Queue;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

public class DataAgent extends AbstractData{
	Queue<DataVictim> missions;
	
	
	public DataAgent(EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		super(id, Hp, damage, position, buriedness, location);
	}
	


	
	
	
	//////////////////////////////////////////////////////
	
	
	
	
	//////////////////////////////////////////////////////

	public boolean isTransporting () {
		return this.status == Status.TRANSPORTING_VICTIM;
	}
	
	public void setTransporting () {
		this.status = Status.TRANSPORTING_VICTIM;
	}
	
	
	
}
