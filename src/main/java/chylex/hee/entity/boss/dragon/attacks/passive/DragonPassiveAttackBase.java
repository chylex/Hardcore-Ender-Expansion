package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;

public abstract class DragonPassiveAttackBase{
	protected EntityBossDragon dragon;
	public final byte id;
	
	public DragonPassiveAttackBase(EntityBossDragon dragon, int attackId){
		this.dragon = dragon;
		this.id = (byte)attackId;
	}
	
	public abstract void update();
}
