package fmcp.Algo;

import java.util.*;

import fmcp.Sim.*;


public abstract class Solver {
	
	
	protected DataList<DataAgent> rescueAgents;
	protected DataList<DataVictim>  victims;
	
	protected DataList<DataAgent> agentsAlloc;
	protected DataList<DataVictim>  victimsAlloc;
	
	
	
	public Solver( DataList<DataAgent> ambulanceAgents, DataList<DataVictim>  tasks) {
		super();

		this.rescueAgents = ambulanceAgents;
		this.victims = tasks;
	}



	
	public abstract Vector<Assignment>[] solve ();
	

}
