package fmcp;
import java.util.TreeMap;
import java.util.Vector;
import commlib.message.RCRSCSMessage;

import rescuecore2.worldmodel.EntityID;

public class DataList {
	
	private TreeMap<EntityID,Integer> convertionMap;
	private Vector<DataVictim> vec;
	
	public DataList () {
		convertionMap = new TreeMap<EntityID,Integer>();
		vec = new Vector<DataVictim>();
	}
	
	
	/*
	 * updates agent's data or creates a new record if not exists
	 */
	public void updateAgentData (EntityID id, int Hp, int damage, EntityID position, int buriedness) {
		
		if (convertionMap.containsKey(id)) { // existing id
			vec.get(convertionMap.get(id)).update(Hp, damage, position, buriedness, true);
		}
		else { // this is a new one
			convertionMap.put(id, vec.size());
			vec.add(new DataVictim(id, Hp, damage, position, buriedness));
		}
	}
	
	/*
	 * updates agent's data or creates a new record if not exists
	 */
	public void updateAgentData (EntityID id, EntityID position) {
		
		if (convertionMap.containsKey(id)) { // existing id
			vec.get(convertionMap.get(id)).update(-1, -1, position, -1, false);
		}
		else { // this is a new one
			System.out.println("ERROR - POSITION MESSAGE FOR AGENT " + id + " BEFORE INIT");
		}
	}
	
	
	// get specific agent
	public DataVictim getAgent (EntityID Id) {
		return vec.get(convertionMap.get(Id));
	}
	
	
	
	// gets all agents with either statuses
	public Vector<DataVictim> getAgentsWithStatus (Status...status) {
		Vector<DataVictim> ans = new Vector<DataVictim>();
		for (DataVictim entity : vec) {
			for (Status s : status) {
				if (entity.getStatus() == s) {
					ans.add(entity);
					break;
				}
			}
			
		}
		
		return null;
	}
	
	
	
}
	