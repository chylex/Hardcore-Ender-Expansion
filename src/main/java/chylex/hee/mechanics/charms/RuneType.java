package chylex.hee.mechanics.charms;

public enum RuneType{
	POWER(0,"power"), AGILITY(1,"agility"), VIGOR(2,"vigor"), DEFENSE(3,"defense"), MAGIC(4,"magic"), VOID(5,"void");
	
	public final byte damage;
	public final String iconSuffix;
	
	RuneType(int damage, String iconSuffix){
		this.damage = (byte)damage;
		this.iconSuffix = iconSuffix;
	}
}