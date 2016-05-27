package fmcp;

public class DataAgent {
	private int Id;
	private int Hp;
	private int damage;
	private int position;
	private int buriedness;
	
	
	public DataAgent (int Id, int Hp, int damage, int position, int buriedness) {
		this.Id = Id;
		this.damage = damage;
		this.position = position;
		this.buriedness = buriedness;
	}
	
	//////////////////////////////////////////////////////
	
	// Id //
	public int getId () {
		return this.Id;
	}
	
	public void setId (int Id) {
		this.Id = Id;
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
	public int getPosition () {
		return this.position;
	}
	
	public void setPosition (int position) {
		this.position = position;
	}
	
	// buriedness
	public int getBuriedness () {
		return this.buriedness;
	}
	
	public void setBuriedness (int buriedness) {
		this.buriedness = buriedness;
	}

}
