package fmcp.Algo;

import java.util.Vector;

public abstract class TaskOrdering {
	
	public TaskOrdering() {
		super();
	}

	public abstract Vector<Assignment>[] TaskPrioritization(Vector<Assignment>[] allocation);
}
