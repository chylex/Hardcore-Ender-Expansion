package chylex.hee.mechanics.charms;

public enum RuneType{
	POWER(0,0xff0000), AGILITY(1,0xffff00), VIGOR(2,0x00ff00), DEFENSE(3,0x00ffff), MAGIC(4,0xff00ff), VOID(5,0x888888);
	
	public final byte damage;
	public final int color;
	
	RuneType(int damage, int color){
		this.damage = (byte)damage;
		this.color = color;
	}
}