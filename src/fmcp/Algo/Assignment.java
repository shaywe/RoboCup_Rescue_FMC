package fmcp.Algo;

import fmcp.Sim.*;

public class Assignment {

	private double ratio;
	private DataAgent agent;// agent for the task
	private DataVictim task;
	private double endTime; // estimated end time
	private double arrivalTime;// the time that the agent arrived to task
	private double fisherUtility;

	public Assignment(DataAgent agent, DataVictim task, double ratio) {
		super();
		this.ratio = ratio;
		this.agent = agent;
		this.task = task;
		this.arrivalTime = -1;
	}

	@Override
	public String toString() {

		return "Allocating [ratio=" + ratio + ", agent=" + agent.getId() + ", task="
				+ task.getId() + "]";
	}

	// /Getters and Setters
	public double getRatio() {
		return ratio;
	}

	public DataAgent getAgent() {
		return agent;
	}

	public DataVictim getTask() {
		return task;
	}

	public void setAgent(DataAgent agent) {
		this.agent = agent;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	public double getFisherUtility() {
		return fisherUtility;
	}

	public void setFisherUtility(double fisherUtility) {
		this.fisherUtility = fisherUtility;
	}

}
