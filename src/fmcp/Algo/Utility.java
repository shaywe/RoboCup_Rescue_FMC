package fmcp.Algo;


public class Utility {
	
	protected double linearUtility;	
	
	public Utility(double linearUtility){
		this.linearUtility = linearUtility;
	}
	

	

	public double getUtility (double ratio) {
		return linearUtility * ratio;
	}
	
	public double getUtility () {
		return this.linearUtility;
	}
	
	public Utility clone () {
		return new Utility (this.linearUtility);
	}
	

}
