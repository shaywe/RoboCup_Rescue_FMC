package fmcp;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

public class DataVictim extends AbstractData{

	public DataVictim(EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		super(id, Hp, damage, position, buriedness, location);
	}
	


	
	
	
	//////////////////////////////////////////////////////
	
	
	
	
	//////////////////////////////////////////////////////

	public boolean isTransported () {
		return this.status == Status.TRANSPORTED_TO_RESCUE;
	}
	
	public void setTransported () {
		this.status = Status.TRANSPORTED_TO_RESCUE;
	}
	
	
	
}
