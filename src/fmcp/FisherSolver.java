package fmcp;

import java.util.*;
import java.util.Map.Entry;

import fisher.*;
import Comparators.*;
import Helpers.WriteToFile;
import PoliceTaskAllocation.DataVictim;
import TaskAllocation.*;

public class FisherSolver extends Solver {

	public static double minValue = Double.MIN_VALUE;
	protected Vector<Assignment>[] allocation;

	public FisherSolver(Utility[][] input, TaskOrdering taskOrdering,
			Vector<DataVictim> tasks) {
		super(input, taskOrdering, tasks);
		// TODO Auto-generated constructor stub
	}

	// run Fisher allocation
	public Vector<Assignment>[] solve() {
		Double[][] fisherInput = createFisherInput(input);
	//WriteToFile.writeFisherOutpuToFile(fisherInput);
		FisherPolinom f2 = new FisherPolinom(fisherInput);
	 //WriteToFile.writeFisherOutpuToFile(f2.getOutput());
		creatFisherSolution(f2.getOutput());
		return taskPrioritization(allocation);
	}

	// / convert utility to double for fisher input
	protected Double[][] createFisherInput(Utility[][] input) {
		Double[][] fisherInput = new Double[input.length][input[0].length];
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
	
	public static void setInput (DataList<DataAgent> RescueAgents, DataList<DataVictim> victims) {
		
		
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
    	// remove zero rows\columns	`1
	}
	
	/**
     * gets all agents who are not transporting a victim
     * @param list
     * @return a list with agents either scouting, digging ,in rest, or at refuge
     */
    private static DataList<DataAgent> getAllAgents (DataList<DataAgent> list) {
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
    private static int totalRescueTime (DataAgent rescueAgent, DataVictim victim) {
    	return DataList.timeToVictim(rescueAgent, victim)
    			+ victim.timeToUnbury()
    			+ DataList.timeToRefuge(rescueAgent, victim, rescueAgent.getVelocity());//fix
    }



}
