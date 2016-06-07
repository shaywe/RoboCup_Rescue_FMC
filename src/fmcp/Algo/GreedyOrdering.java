package fmcp.Algo;

import java.util.Collections;
import java.util.Vector;


public class GreedyOrdering extends TaskOrdering {

	public GreedyOrdering() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Vector<Assignment>[] TaskPrioritization(Vector<Assignment>[] allocation){
		for (int i = 0; i < allocation.length; i++) {
			Collections.sort(allocation[i], PersonalUtilityComparator.com);
			Collections.reverse(allocation[i]);
		}
		
		return allocation;
	}

}
