package fmcp;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import Comparators.UtilityComparator;
import PoliceTaskAllocation.DataVictim;
import PoliceTaskAllocation.DataAgent;
import TaskAllocation.*;


public abstract class Solver {
	
	protected Utility[][] input;
	protected Vector<DataAgent> ambulanceAgents;
	protected Vector<DataVictim>  tasks;
	protected TaskOrdering taskOrdering;
	




	
	
	public Solver(Utility[][] input, Vector<DataAgent> ambulanceAgents, Vector<DataVictim>  tasks,TaskOrdering taskOrdering) {
		super();
		this.input = input;

		this.ambulanceAgents = ambulanceAgents;
		this.tasks = tasks;
		this.taskOrdering=taskOrdering;
	}



	public Solver(Utility[][] input, TaskOrdering taskOrdering2,Vector<DataVictim>  tasks) {
		this.input = input;
		this.taskOrdering=taskOrdering2;
		this.tasks = tasks;
	}
	
	public abstract Vector<Assignment>[] solve ();
	
	// prioritize tasks for each agent 
	protected Vector<Assignment>[] taskPrioritization(Vector<Assignment>[] allocation){
		return taskOrdering.TaskPrioritization(allocation);		
	}
	
	
	
	/// convert division by tasks to division by agents
	protected Vector<Assignment>[] divideAllocation(
			Vector<Assignment>[] missionAllocation) {

		Vector<Assignment>[] agentsAllocation = new Vector[ambulanceAgents.size()];
		for (int i = 0; i < agentsAllocation.length; i++) {
			agentsAllocation[i] = new Vector<Assignment>();

		}
		for (int i = 0; i < missionAllocation.length; i++) {
			
			for (Assignment a : missionAllocation[i]) {
				
				agentsAllocation[a.getAgent().getId()-1].add(a);
			}
		}
		taskPrioritization(agentsAllocation);
		return agentsAllocation;
	}



	public void setInput(Utility[][] input) {
		this.input = input;
	}
	
	public void setAgnets(Vector<DataAgent> ambulanceAgents) {
		this.ambulanceAgents = ambulanceAgents;
	}

	public void setTasks(Vector<DataVictim>  tasks) {
		this.tasks = tasks;
	}
	protected Vector<Assignment>[] creatSolution(Double[][] output) {
		Vector<Assignment>[] allocation = new Vector[output.length];
		for (int i = 0; i < allocation.length; i++) {
			allocation[i] = new Vector<Assignment>();
		}
		//sort the fraction allocation to task j 
			for(int j = 0; j < output[0].length; j++) {
			int uR = tasks.get(j).getAgentsRequiered();
			TreeMap<Double,Integer>all=new TreeMap<Double,Integer>();
			
				for (int i = 0; i < output.length; i++) {
					if(output[i][j]!=null){
						all.put(output[i][j],i);
					}
				}
			//allocates only the required number of units
				while(all.size()>uR){
					all.remove(all.firstKey());
				}
				tasks.get(j).setNumOfAllocatedAgents(all.size());
				double sum=0;
				for(Entry<Double, Integer> e: all.entrySet()) {
				
					sum=sum+output[e.getValue()][j];
				}
				for(Entry<Double, Integer> e: all.entrySet()) {
					int an=e.getValue();
					allocation[an].add(new Assignment(input[an][j].getAgent(),input[an][j].getTask(),
							 output[an][j]/sum));
								
				}
				
		}
		return allocation;
	}

}
