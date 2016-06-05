package fmcp;
import java.util.TreeMap;
import java.util.Vector;
import commlib.message.RCRSCSMessage;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

public class DataList<T extends DataVictim> {
	
	private TreeMap<EntityID,Integer> convertionMap;
	private Vector<T> vec;
	
	public DataList () {
		convertionMap = new TreeMap<EntityID,Integer>();
		vec = new Vector<T>();
	}
	
	public void updateAgentData (T data) {
		
		if (convertionMap.containsKey(data.getId())) { // existing id
			vec.get(convertionMap.get(data.getId())).update(data.getHp(), data.getDamage(), data.getPosition(), data.getBuriedness(),data.getLocation(), true);
		}
		else { // this is a new one
			convertionMap.put(data.getId(), vec.size());
			vec.add(data);
		}
	}
		
		
	
	/*
	 * updates agent's data or creates a new record if not exists
	 */
	public void updateAgentData (EntityID id, Pair<Integer, Integer> location) {
		
		if (convertionMap.containsKey(id)) { // existing id
			vec.get(convertionMap.get(id)).update(-1, -1, null, -1, location, false);
		}
		else { // this is a new one
			System.out.println("ERROR - POSITION MESSAGE FOR AGENT " + id + " BEFORE INIT");
		}
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
	
	private static int getRectDistance (DataVictim A, EntityID B) {
		return Math.abs(A.getLocation().first() - B.)
				+ Math.abs(A.getLocation().second() - B.getLocation().second());
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
	public static int timeToRefuge (DataAgent victim, DataVictim refuge, double agentVelocity) {
		getRectDistance(victim, refuge);
		return null;
	}
	
	
	
	
	
}
	