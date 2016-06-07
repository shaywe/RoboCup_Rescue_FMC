package fmcp.Algo;

import java.util.*;
import java.util.Map.Entry;

import fmcp.Sim.*;

public class FisherSolver extends Solver {

	public static double minValue = Double.MIN_VALUE;
	protected Utility[][] input;
	protected TaskOrdering taskOrdering;

	

	public FisherSolver(DataList<DataAgent> ambulanceAgents, DataList<DataVictim> tasks,
			 Vector<Assignment>[] allocation) {
		super(ambulanceAgents, tasks);
		this.allocation = allocation;
	}

	// run Fisher algorithm
	public Vector<Assignment>[] solve() {
		setInput();
		FisherDistributed f2 = new FisherDistributed(input);//input with utilities w/o 0
		creatFisherSolution(f2.algorithm());
		return taskPrioritization(allocation);
	}

	/**
	 *Creates input for Fisher algorithm
	 */
	public void setInput () {
		
		
		// getting vectors of agents and tasks
    	DataList<DataVictim> tasks;
    	
    	DataList<DataVictim> victimsTasks = DataList.merge(victims.getAllBuried(), rescueAgents.getAllBuried());
    	DataList<DataAgent> agents = getAllAgents(rescueAgents);
    	
    	Utility utilities [][] = new Utility[agents.size()][victimsTasks.size()];
    	// calculating for every victim time to live
    	for (int i = 0; i < agents.size(); i++) {
    		for (int j = 0; j < victimsTasks.size(); j++) {
    			DataAgent agent = agents.get(i);
    			DataVictim victim = victims.get(j);
    			boolean isTransporting = agent.isTransporting();
    			// if the victim can be saved
    			if (totalRescueTime(agent, victim, isTransporting) < victim.timeToLive()) {
        			// fill Rij
    				utilities[i][j] = victim.utility(agent.timeToFinishTransport());
        		}
    		}
    	}
    	// remove zero rows\columns and insert into agentsAloc/victimsAloc
    	agentsAlloc = new DataList<DataAgent>();
    	victimsAlloc = new DataList<>();
	}
	
	/**create allocation from fisher output
	 * 
	 * @param output
	 * @return
	 */
	protected void creatFisherSolution(Double[][] output) {
			
			for (DataAgent agent : agentsAlloc.getVector()) {
				//clear
			}


			for (int i = 0; i < output.length; i++) {
				for (int j = 0; j < output[0].length; j++) {
					if (output[i][j] != null) {
						agentsAlloc.get(i).addTask();
						allocation[i].add(new Assignment(agent, task, output[i][j]));
					}
					
				}
				
			}
			
		}
	/**
     * gets all agents who are not transporting a victim
     * @param list
     * @return a list with agents either scouting, digging ,in rest, or at refuge
     */
    private DataList<DataAgent> getAllAgents (DataList<DataAgent> list) {
    	return list.getAgentsWithStatus(Status.SCOUTING, Status.DIGGING, Status.REST, Status.REFUGE);
    }
    
    /**
     * total rescue time comprised of:<br>
     * 1. time to victim
     * 2. time to rescue the victim
     * 3. time to refuge
     * @param rescueAgent
     * @param victim
     * @return the total rescue time (worst case estimation)
     */
    private int totalRescueTime (DataAgent rescueAgent, DataVictim victim, boolean isTransporting) {
    	if (isTransporting) {
    		//time to refuge + time to new task + 2 * time to refuge (from new task)
    		return rescueAgent.timeToFinishTransport()
        			+ victim.timeToUnbury()
        			+ 2 * DataList.timeToRefuge(rescueAgent, victim, rescueAgent.getVelocity());//fix
    	}
    	else {
    		return DataList.timeToVictim(rescueAgent, victim)
        			+ victim.timeToUnbury()
        			+ DataList.timeToRefuge(rescueAgent, victim, rescueAgent.getVelocity());//fix
    	}
    	
    }
    
    
	public Utility[][] deleteRowsAndColumnsWithZero (Utility[][] utilities) {
		boolean[] zeroRows = getTrueArray(utilities.length);
		boolean[] zeroColumn = getTrueArray(utilities[0].length);
		
		for (int i = 0; i < zeroRows.length; i++) {
			for (int j = 0; j < zeroColumn.length; j++) {
				if (utilities[i][j].getUtility() != 0) {
					zeroRows[i] = false;
					zeroColumn[j] = false;
				}
			}
		}
		
		Utility[][] ans = new Utility[NumOfNonZero(zeroRows)][NumOfNonZero(zeroColumn)];
		
		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans[0].length; j++) {
				
			}
		}
		
		return ans;
		
	}
	
	private boolean[] getTrueArray (int length) {
		boolean[] ans = new boolean[length];
		for (int i = 0; i < length; i++) {
			ans[i] = true;
		}
		return ans;
	}
	
	private int NumOfNonZero (boolean[] zeroArr) {
		int ans = zeroArr.length;
		for (int i = 0; i < zeroArr.length; i++) {
			if (zeroArr[i]) {
				ans--;
			}
		}
		return ans;
	}




}
