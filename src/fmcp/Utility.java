package fmcp;


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
	
	public static Utility[][] deleteRowsAndColumnsWithZero (Utility[][] utilities) {
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
	
	private static boolean[] getTrueArray (int length) {
		boolean[] ans = new boolean[length];
		for (int i = 0; i < length; i++) {
			ans[i] = true;
		}
		return ans;
	}
	
	private static int NumOfNonZero (boolean[] zeroArr) {
		int ans = zeroArr.length;
		for (int i = 0; i < zeroArr.length; i++) {
			if (zeroArr[i]) {
				ans--;
			}
		}
		return ans;
	}
}
