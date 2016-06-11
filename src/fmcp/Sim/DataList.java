package fmcp.Sim;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import commlib.message.RCRSCSMessage;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.entities.StandardWorldModel;
import rescuecore2.worldmodel.EntityID;

public class DataList<T extends DataVictim> {
	
	private Vector<T> vec;
	
	public DataList () {
		vec = new Vector<T>();
	}
	
	public void updateAgentData (T data) {
		
		if (this.contains(data)) { // existing id
			vec.get(vec.indexOf(data)).update(data.getHp(), data.getDamage(), data.getPosition(), data.getBuriedness(),data.getLocation(), true);
		}
		else { // this is a new one
			vec.add(data);
		}
	}
		
	/*
	 * updates agent's data or creates a new record if not exists
	 */
	public void updateAgentData (EntityID id, Pair<Integer, Integer> location) {
		
		if (this.containsEntityId(id)) { // existing id
			get(id).update(-1, -1, null, -1, location, false);
		}
		else { // this is a new one
			System.out.println("ERROR - POSITION MESSAGE FOR AGENT " + id + " BEFORE INIT");
		}
	}
	
	
	public boolean containsEntityId (EntityID id) {
		for (DataVictim vic : this.vec) {
			if (vic.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains (T data) {
		return vec.contains(data);		
	}
	

	// get specific agent
	public T get (EntityID id) {
		for (T vic : this.vec) {
			if (vic.getId().equals(id)) {
				return vic;
			}
		}
		return null;
	}
	
		
	// get specific agent
	public T get (int index) {
		return vec.elementAt(index);
	}
	
	// returns the number of object in this data structure
	
	public int size () {
		return vec.size();
	}
	
	
    
	// filter list for buried victims
	public DataList<DataVictim> getAllBuried () {
		DataList<DataVictim> list = new DataList<DataVictim>();
		for (DataVictim entity : vec) {
			if (entity.isBuried()) {
				list.updateAgentData(entity);
			}
		}
		return list;
	}
	
	// gets all agents with either statuses
	public DataList<T> getAgentsWithStatus (Status...status) {
		DataList<T> ans = new DataList<T>();
		for (T entity : vec) {
			for (Status s : status) {
				if (entity.getStatus() == s) {
					ans.updateAgentData(entity);;
					break;
				}
			}
			
		}
		
		return ans;
	}
	
	////////////////////////////////////////////
	////////////////////////////////////////////
	
	public static DataList<DataVictim> merge  (DataList<DataVictim> A, DataList<DataVictim> B) {
		DataList<DataVictim> list = new DataList<DataVictim>();
		for (DataVictim data : A.getVector()) {
			list.updateAgentData(data);
		}
		for (DataVictim data : B.getVector()) {
			list.updateAgentData(data);
		}
		return list;
	}
	
	public Vector<T> getVector () {
		return this.vec;
	}
	
	/**
	 * 
	 * @param A first entity
	 * @param B second entity
	 * @return the rectangular distance between the given entities
	 */
	private static int getRectDistance (DataVictim A, DataVictim B) {
		return Math.abs(A.getLocation().first() - B.getLocation().first())
				+ Math.abs(A.getLocation().second() - B.getLocation().second());
	}
	
	/**
	 * 
	 * @param A victim
	 * @param refuges center's refuges list
	 * @return the rectangular distance between the given victim and the closest refuge
	 */
	private static int getRectDistance (DataVictim A, StandardWorldModel centerModel) {
		int minDistance = Integer.MAX_VALUE;
		for (StandardEntity ref : centerModel.getEntitiesOfType(StandardEntityURN.REFUGE)) {
			int tmp = Math.abs(A.getLocation().first() - ref.getLocation(centerModel).first())
					+ Math.abs(A.getLocation().second() - ref.getLocation(centerModel).second());
			if (tmp < minDistance) {
				minDistance = tmp;
			}
		}
		return minDistance;
	}
	
	
	/**
	 * 
	 * @param rescue Agent
	 * @param  victim
	 * @return the time that will take the rescue agent to reach the victim
	 * rounding time up for worst case scenario
	 */
	public static int timeToVictim (DataAgent rescueAgent, DataVictim victim) {
		return (int) (getRectDistance(rescueAgent, victim) / rescueAgent.getVelocity()) + 1;
	}
	
	/**
	 * 
	 * @param victim
	 * @param  
	 * @return the time that will take the rescue agent to reach the refuge from victim's location
	 * rounding time up for worst case scenario
	 */
	public static int timeToRefuge (DataVictim victim, StandardWorldModel centerModel, DataAgent rescueAgent) {
		return (int) (getRectDistance(victim, centerModel) / rescueAgent.getVelocity()) + 1;
	}
	
	
	
	
	
}
	