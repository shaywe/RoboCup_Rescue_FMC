package fmcp.Algo;

import java.util.*;
import java.util.Map.Entry;

import fmcp.Sim.*;

public class FisherSolver2 extends Solver {

	public static double minValue = Double.MIN_VALUE;
	protected Vector<Assignment>[] allocation;
	protected Utility[][] input;
	protected TaskOrdering taskOrdering;

	

	public FisherSolver2(Vector<DataAgent> ambulanceAgents, Vector<DataVictim> tasks,
			 Vector<Assignment>[] allocation) {
		super(ambulanceAgents, tasks);
		this.allocation = allocation;
	}

	// run Fisher allocation
	public Vector<Assignment>[] solve() {
		createFisherInput();
		FisherDistributed f2 = new FisherDistributed(input);
	 //WriteToFile.writeFisherOutpuToFile(f2.getOutput());
		creatFisherSolution(f2.getOutput());
		return taskPrioritization(allocation);
	}

	// / convert utility to double for fisher input
	protected Utility[][] createFisherInput() {
		input = new Utility[ambulanceAgents.size()][input[0].length];
		for (int i = 0; i < fisherInput.length; i++) {
			for (int j = 0; j < fisherInput[0].length; j++) {
				if (input[i][j] != null) {
					fisherInput[i][j] = input[i][j].getUtility(1);
				} else {
					fisherInput[i][j] = 0.0;
				}
			}

		}
		return fisherInput;
	}

	// create allocation from fisher output
	protected Vector<Assignment>[] creatFisherSolution(Double[][] output) {
		allocation = new Vector[output.length];
		for (int i = 0; i < allocation.length; i++) {
			allocation[i] = new Vector<Assignment>();
		}
		// sort the fraction allocation to task j
		for (int j = 0; j < output[0].length; j++) {
			int uR = tasks.get(j).getAgentsRequiered();
			List<Ratio> all = new ArrayList<Ratio>();

			for (int i = 0; i < output.length; i++) {
				if (output[i][j] != null && output[i][j] > minValue) {
					all.add(new Ratio(output[i][j],i));
				}
			}
			Collections.sort(all);
			// allocates only the required number of units
			if(uR>all.size()){
				uR=all.size();
			}
			while(all.size()>uR){
				all.remove(all.size()-1);
			}

			
			all = createAllocationForMission(j, all);
			
			for (Ratio e : all) {
				int an = e.getAgentId();
				if(input[an][j]==null){
					System.out.println("stop");
				}
				Assignment as = new Assignment(input[an][j].getAgent(),input[an][j].getTask(), e.getRatio());
				as.setFisherUtility(input[an][j].getUtility(1));
				allocation[an].add(as);
			}
			tasks.get(j).setNumOfAllocatedAgents(all.size());
		}
		return allocation;
	}

	// Creates relative allocation according to output from FMC
	private List<Ratio> createAllocationForMission(int j,
			List<Ratio> all) {
		double sum = 0;
		for (Ratio e : all) {

			sum = sum +e.getRatio();
		}
		for (Ratio e : all) {
			e.setRatio(e.getRatio()/ sum);
		}
		 return all;


	}

	// creates equal allocation for mission
	private  List<Ratio> createEqualAllocationForMission(int j, List<Ratio> all) {
		for (Ratio e : all) {
			e.setRatio(1.0/ all.size());
		}
		return all;
	}

	
	// Creates relative allocation according to output from FMC
	private  List<Ratio> createRelativeAllocationForMission(int j,List<Ratio> all) {
		double sum = 0;
		for (Ratio e : all) {
			sum = sum +e.getRatio();
		}
		for (Ratio e : all) {			
			e.setRatio(e.getRatio()/ sum);
		}

		EqualAllcoation ea = new EqualAllcoation(all);
		all = ea.divide();
		
		return all;
	}
	
	////////////////////////////////////////////
	////////////////////////////////////////////
	
	public  Utility[][] setInput (DataList<DataAgent> RescueAgents, DataList<DataVictim> victims) {
		
		
		// getting vectors of agents and tasks
    	DataList<DataVictim> tasks;
    	
    	DataList<DataVictim> victimsTasks = DataList.merge(victims.getAllBuried(), RescueAgents.getAllBuried());
    	DataList<DataAgent> agents = getAllAgents(RescueAgents);
    	
    	Utility utilities [][] = new Utility[agents.size()][victimsTasks.size()];
    	// calculating for every victim time to live
    	for (int i = 0; i < agents.size(); i++) {
    		for (int j = 0; j < victimsTasks.size(); j++) {
    			
    			// if the victim can be saved
    			if (totalRescueTime(agents.get(i), victimsTasks.get(j)) < victimsTasks.get(j).timeToLive()) {
        			// fill Rij
    				utilities[i][j] = victimsTasks.get(j).utility();
        		}
    			else {
    				utilities[i][j] = new Utility(0);
    			}
    		}
    		
    	}
    	// remove zero rows\columns	
	}
	
	
    

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
