package fmcp.Sim;
import rescuecore2.misc.Pair;
import fmcp.Algo.*;
import rescuecore2.worldmodel.EntityID;

public class DataVictim {
	protected EntityID id;
	protected int Hp;
	protected final int HpMax = 10000;
	protected int damage;
	protected EntityID position;
	protected Pair<Integer, Integer> location;
	protected int buriedness;
	protected Status status; // add to functions
	int time;

	public DataVictim (int time, EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		this.id = id;
		setStatus(Status.REST); // initial status
		update(time, Hp, damage, position, buriedness, location, true);
	}
	//////////////////////////////////////////////////////
	
	protected void update (int time, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location, Boolean isFullUpdate) {
		if (!isDead()) {
			this.location = location;
			if (isFullUpdate) {
				this.position = position;
				setDamage(damage);
				setBuriedness(buriedness);
				this.setHp(Hp);
			}
			this.time = time;
		}
	}
	/**
	 * Hp: victim's 'life points' [points]
	 * Damage: the number of points reduced from victim's Hp every cycle [points / cycles]
	 * Hp/Damage = number of cycles to live [cycles]
	 * @return number of simulation's iterations till the the victim dies. 
	 * using int to round the time down for worst case scenario
	 */
	
	public int timeToLive () {
		return getHp() / getDamage();
	}
	
	/**
	 * every simulation cycle the buriedness property is reduced by one
	 * 
	 * @return
	 */
	public int timeToUnbury () {
		return this.getBuriedness();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataVictim) {
			return this.getId().equals(((DataVictim)obj).getId());
		}
		else {
			return false;
		}
		
	}
	
	
	public Utility utility (int timeToFinishCurrentTask, int totalRescueTime) {
		return new Utility (this.HpMax - 
						(this.Hp - ((timeToFinishCurrentTask + totalRescueTime) * getDamage())));
	}
	
	
	//////////////////////////////////////////////////////
	
	// id //
	public EntityID getId () {
		return this.id;
	}
	
	// Hp //
	protected int getHp () {
		return this.Hp;
	}
	
	public int getTime () {
		return this.time;
	}
	
	protected void setHp (int Hp) {
		this.Hp = Hp;
		if (this.Hp <= 0) {
			this.status = Status.DEAD;
		}
	}
	
	public void setHpAfter (int time) {
		int dt = Math.abs(getTime() - time);
		setHp(this.Hp - (dt * getDamage()));
	}
	
	
	// damage //
	protected int getDamage () {
		return this.damage;
	}
	
	protected void setDamage (int damage) {
		this.damage = damage;
	}
	
	protected boolean isDamaged () {
		return this.damage > 0;
	}
	
	// position //
	protected EntityID getPosition () {
		return this.position;
	}
	
	protected void setPosition (EntityID position) {
		this.position = position;
	}
	
	// buriedness
	protected int getBuriedness () {
		return this.buriedness;
	}
	
	protected void setBuriedness (int buriedness) {
		if (buriedness > 0) {
			this.status = Status.BURIED;
		}
		if (buriedness == 0 && getStatus() == Status.BURIED) { 
			setStatus(Status.TRANSPORTED_TO_RESCUE);
		}
		this.buriedness = buriedness;
	}
	
	protected boolean isBuried () {
		return this.getBuriedness() > 0;
	}
	
	// location
	protected void setLocation(Pair<Integer, Integer> location) {
		this.location = location;
		if (true) {
			this.status = Status.REFUGE;
		}
	}
	
	protected Pair<Integer,Integer> getLocation() {
		return this.location;
	}
	
	// Status
	public Status getStatus () {
		return this.status;
	}
	
	protected void setStatus (Status status) {
		this.status = status;
	}
	
	protected boolean isDead () {
			return getStatus() == Status.DEAD;
	}
	
	

	
	public boolean isTransported () {
		return this.status == Status.TRANSPORTED_TO_RESCUE;
	}
	
	public void setTransported () {
		this.status = Status.TRANSPORTED_TO_RESCUE;
	}
	
	
	
}
