package fmcp.Algo;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.RecursiveAction;

import fmcp.Sim.*;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardWorldModel;

public class FisherSolver extends Solver {
	private DataList<DataAgent> agentsLeft;
	public static double minValue = Double.MIN_VALUE;
	protected Utility[][] input;
	private StandardWorldModel centerModel;

	public FisherSolver(DataList<DataAgent> ambulanceAgents, DataList<DataVictim> tasks, StandardWorldModel centerModel) {
		super(ambulanceAgents, tasks);
		// move to super??
		this.centerModel = centerModel;
	}

	// run Fisher algorithm
	public void solve() {
		setInput();
		FisherDistributed f2 = new FisherDistributed(input);
		creatFisherSolution(f2.algorithm());
	}

	/**
	 * Creates input for Fisher algorithm
	 */
	public void setInput() {
		// getting vectors of agents and tasks
		DataList<DataVictim> victimsTasks = DataList.merge(victims.getAllBuried(), rescueAgents.getAllBuried());
		DataList<DataAgent> agents = rescueAgents.getAgentsWithStatus(
				Status.SCOUTING, Status.DIGGING, Status.REST, Status.REFUGE, Status.TRANSPORTING_VICTIM);

		Utility utilities[][] = new Utility[agents.size()][victimsTasks.size()];
		boolean[] zeroRows = getTrueArray(utilities.length);
		boolean[] zeroColumn = getTrueArray(utilities[0].length);
		
		// calculating for every victim time to live vs rescue time of the agent
		for (int i = 0; i < agents.size(); i++) {
			for (int j = 0; j < victimsTasks.size(); j++) {
				
				DataAgent agent = agents.get(i);
				DataVictim victim = victims.get(j);
				boolean isTransporting = agent.isTransporting();
				
				// if the victim can be saved
				int totalRescueTime = totalRescueTime(agent, victim, centerModel, isTransporting);
				if (totalRescueTime < victim.timeToLive()) {
					// fill Rij
					utilities[i][j] = victim.utility(agent.timeToFinishTransport(centerModel), totalRescueTime);
				}
				else {
					utilities[i][j] = new Utility(0);
				}
				// placing false if there is positive utility
				if (utilities[i][j].getUtility() != 0) {
					zeroRows[i] = false;
					zeroColumn[j] = false;
				}
			}
		}
		
		// insert into agentsAloc/victimsAloc
		agentsAlloc = new DataList<DataAgent>();
		victimsAlloc = new DataList<DataVictim>();
		agentsLeft = new DataList<DataAgent>();
		for (int i = 0; i < agents.size(); i++) {
			if (!zeroColumn[i]) {
				agentsAlloc.updateAgentData(agents.get(i));
			}
			else { // agents with no task
				agentsLeft.updateAgentData(agents.get(i));
			}
		}
		for (int j = 0; j < victimsTasks.size(); j++) {
			if (!zeroColumn[j]) {
				victimsAlloc.updateAgentData(victimsTasks.get(j));
			}
		}
		
		// insert into agentsAloc/victimsAloc
		Utility[][] ans = new Utility[NumOfNonZero(zeroRows)][NumOfNonZero(zeroColumn)];
		int row = 0;
				
		for (int i = 0; i < utilities.length; i++) {
			if (!zeroRows[i]) {
				int col = 0;
				for (int j = 0; j < utilities.length && zeroColumn[j]; j++) {
					if (!zeroColumn[j]) {
						ans[row][col] = utilities[i][j];
						col++;
					}
				}
				row++;
			}
			
		}
		input = ans;
		
		// 
		for (int i = 0; i < agents.size(); i++) {
			for (int j = 0; j < victimsTasks.size(); j++) {
				
			}
		}
	}
	

	/**
	 * create allocation from fisher output
	 * 
	 * @param output
	 * @return
	 */
	protected void creatFisherSolution(Double[][] output) { //Xij
		// clear all tasks from all agents
		for (DataAgent agent : rescueAgents.getVector()) { //replace with agentsAlloc??
			agent.clearTasks();
		}
		
		// assign agents with tasks
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[0].length; j++) {
				DataVictim victim = victimsAlloc.get(j);
				DataAgent agent = agentsAlloc.get(i);
				
				if (output[i][j] != null) {
					// Xij * Rij
					Double tmp = output[i][j] * input[i][j].getUtility();
					agent.addTask(tmp, victim);
				}

			}

		}
		
		// assign agents with no tasks to try help with digging
		for (DataAgent agent :  agentsLeft.getVector()) {
			int maxUtility = 0;
			
		}
		

	}

	/**
	 * total rescue time comprised of:<br>
	 * 1. time to victim 2. time to rescue the victim 3. time to refuge
	 * 
	 * @param rescueAgent
	 * @param victim
	 * @return the total rescue time (worst case estimation)
	 */
	private int totalRescueTime(DataAgent rescueAgent, DataVictim victim, StandardWorldModel centerModel, boolean isTransporting) {
		if (isTransporting) {
			// time to refuge + time to new task + 2 * time to refuge (from new task)
			return rescueAgent.timeToFinishTransport(centerModel) + victim.timeToUnbury()
					+ 2 * DataList.timeToRefuge(victim, centerModel, rescueAgent);
		} else {
			//
			return DataList.timeToVictim(rescueAgent, victim) + victim.timeToUnbury()
					+ DataList.timeToRefuge(victim, centerModel, rescueAgent);
		}

	}


	private boolean[] getTrueArray(int length) {
		boolean[] ans = new boolean[length];
		for (int i = 0; i < length; i++) {
			ans[i] = true;
		}
		return ans;
	}

	private int NumOfNonZero(boolean[] zeroArr) {
		int ans = zeroArr.length;
		for (int i = 0; i < zeroArr.length; i++) {
			if (zeroArr[i]) {
				ans--;
			}
		}
		return ans;
	}
	

}
