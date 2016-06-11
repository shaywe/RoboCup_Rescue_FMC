package fmcp.Sim;

public enum Status {
	// active status
	// 
	DIGGING, // when agent reports digging? / center assign with status / delete
	
	//
	TRANSPORTING_VICTIM, // when agent reports transporting? / when center decides? 
	
	
	//////
	//
	SCOUTING, // when agent reports transporting? / when center decides?
	
	
	// passive status
	//
	BURIED, // buriedness > 0
	
	//
	TRANSPORTED_TO_RESCUE, //
	
	//
	DEAD, // hp <= 0
	
	REFUGE, 
	
	REST
}
