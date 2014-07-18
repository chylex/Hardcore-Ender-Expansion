package chylex.hee.entity.boss.dragon.attacks.special.event;
import net.minecraft.util.DamageSource;

public class DamageTakenEvent extends BaseEvent{
	public DamageSource source;
	public float damage;
	
	public DamageTakenEvent(DamageSource source, float damage){
		this.source = source;
		this.damage = damage;
	}
	
	public void cancel(){
		damage = 0F;
	}
}
