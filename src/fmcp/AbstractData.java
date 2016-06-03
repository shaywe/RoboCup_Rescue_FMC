package fmcp;
import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractData {
	protected EntityID id;
	protected int Hp;
	protected int damage;
	protected EntityID position;
	protected Pair<Integer, Integer> location;
	protected int buriedness;
	protected Status status; // add to functions
	
	
	public AbstractData (EntityID id, int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location) {
		this.id = id;
		this.status = Status.REST; // start
		update(Hp, damage, position, buriedness, location, true);
	}
	
	//////////////////////////////////////////////////////
	
	protected void update (int Hp, int damage, EntityID position, int buriedness, Pair<Integer, Integer> location, Boolean isFullUpdate) {
		if (!(this.status == Status.DEAD)) {
			this.location = location;
			if (isFullUpdate) {
				this.position = position;
				this.damage = damage;
				this.setBuriedness(buriedness);
				this.setHp(Hp);
			}
		}
	}
	
	
	//////////////////////////////////////////////////////
	
	// id //
	protected EntityID getId () {
		return this.id;
	}
	
	// Hp //
	protected int getHp () {
		return this.Hp;
	}
	
	protected void setHp (int Hp) {
		this.Hp = Hp;
		if (this.Hp <= 0) {
			this.status = Status.DEAD;
		}
	}
	
	protected boolean isDead () {
		return this.status == Status.DEAD;
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
		this.buriedness = buriedness;
		if (this.buriedness > 0) {
			this.status = Status.BURIED;
		}
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
	protected Status getStatus () {
		return this.status;
	}
	
	protected void setStatus (Status status) {
		this.status = status;
	}
}
