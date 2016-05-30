package fmcp;
import rescuecore2.worldmodel.EntityID;

public class DataVictim {
	private EntityID id;
	private int Hp;
	private int damage;
	private EntityID position;
	private int buriedness;
	private Status status; // add to functions
	
	
	
	public DataVictim (EntityID id, int Hp, int damage, EntityID position, int buriedness) {
		//this.id = id;
		update(Hp, damage, position, buriedness, true);
	}
	
	//////////////////////////////////////////////////////
	
	public boolean update (int Hp, int damage, EntityID position, int buriedness, Boolean isFullUpdate) {
		
		this.position = position;
		if (isFullUpdate) {
			this.buriedness = buriedness;
			this.damage = damage;
		}
		return true;
	}
	
	
	//////////////////////////////////////////////////////
	
	// id //
	public EntityID getId () {
		return this.id;
	}
	
	// Hp //
	public int getHp () {
		return this.Hp;
	}
	
	public void setHp (int Hp) {
		this.Hp = Hp;
	}
	
	// damage //
	public int getDamage () {
		return this.damage;
	}
	
	public void setDamage (int damage) {
		this.damage = damage;
	}
	
	// position //
	public EntityID getPosition () {
		return this.position;
	}
	
	public void setPosition (EntityID position) {
		this.position = position;
	}
	
	// buriedness
	public int getBuriedness () {
		return this.buriedness;
	}
	
	public void setBuriedness (int buriedness) {
		this.buriedness = buriedness;
	}
	
	// Status
	public Status getStatus () {
		return this.status;
	}
	
	public void setStatus (Status status) {
		this.status = status;
	}
}
