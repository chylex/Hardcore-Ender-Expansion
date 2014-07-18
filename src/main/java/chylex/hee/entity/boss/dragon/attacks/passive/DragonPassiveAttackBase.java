package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;

public abstract class DragonPassiveAttackBase{
	protected EntityBossDragon dragon;
	public final byte id;
	
	public DragonPassiveAttackBase(EntityBossDragon dragon, int attackId){
		if (!dragon.attacks.registerPassiveAttack(this,attackId)){
			this.id = -1;
			return;
		}
		this.dragon = dragon;
		this.id = (byte)attackId;
	}
	
	public abstract void update();
}
