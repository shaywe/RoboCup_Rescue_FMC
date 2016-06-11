package fmcp.Sim;

import commlib.information.PositionInformation;
import rescuecore2.worldmodel.EntityID;
import rescuecore2.misc.Pair;
public class PositionWithStatus extends PositionInformation{
	Status status;
	public PositionWithStatus (int time, EntityID platoonID, Pair<Integer,Integer> cor, Status status) {
		super(time, platoonID, cor);
		this.status = status;
	}
	
	public Status getStatus () {
		return this.status;
	}
}
