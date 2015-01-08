package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.util.MathUtil;

public abstract class DragonSpecialAttackBase{
	protected EntityBossDragon dragon;
	protected Random rand;
	protected float damageTaken;
	protected float damageDealt;
	protected TObjectFloatHashMap<UUID> lastPlayerHealth = new TObjectFloatHashMap<>();
	public int tick;
	public int phase;
	public double previousEffectivness;
	public double newEffectivness;
	public double effectivness;
	private byte[] disabledPassiveAttacks = ArrayUtils.EMPTY_BYTE_ARRAY;
	public final byte id;
	
	public DragonSpecialAttackBase(EntityBossDragon dragon, int attackId){
		this.dragon = dragon;
		this.rand = dragon.worldObj.rand;
		this.id = (byte)attackId;
	}
	
	public DragonSpecialAttackBase setDisabledPassiveAttacks(byte...attackIds){
		disabledPassiveAttacks = attackIds;
		return this;
	}
	
	public boolean isPassiveAttackDisabled(byte attackId){
		return ArrayUtils.contains(disabledPassiveAttacks,attackId);
	}
	
	public void init(){
		tick = 0;
		phase = 0;
		damageTaken = 0;
		damageDealt = 0;
		lastPlayerHealth.clear();
		updatePlayerHealth();
	}
	
	public void update(){
		tick++;
		updatePlayerHealth();
	}
	
	public void end(){
		previousEffectivness = newEffectivness;
		newEffectivness = calculateTempEffectivness();
		effectivness = calculateFinalEffectivness();
	}
	
	public boolean canStart(){
		return true;
	}
	
	protected void updatePlayerHealth(){
		for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
			UUID id = player.getUniqueID();
			
			if (lastPlayerHealth.containsKey(id)){
				float last = lastPlayerHealth.get(id);
				if (player.getHealth() < last)damageDealt += (last-player.getHealth());
			}
			
			lastPlayerHealth.put(id,player.getHealth());
		}
	}
	
	protected double calculateTempEffectivness(){
		double dealt = damageDealt;
		double taken = damageTaken;
		if (dealt-taken > 0)return ((MathUtil.square(dealt-taken)*0.1D)+Math.sqrt(dealt)+Math.sqrt((4D*(dealt-taken))/(taken == 0D ? 1D : taken))-(((taken*taken)+taken)/35D));
		return (Math.sqrt(3D*dealt)-((taken*taken)+taken)/30D-Math.sqrt(taken)+(dealt/2D*taken));
	}
	
	protected double calculateFinalEffectivness(){
		return (previousEffectivness+newEffectivness)/2D;
	}
	
	public final void onDamageTaken(float damage){
		damageTaken += damage;
	}
	
	public abstract boolean hasEnded();
	
	public int getNextAttackTimer(){
		return Math.max(140,180+rand.nextInt(100)+((4-getDifficulty())*30)-dragon.worldObj.playerEntities.size()*15);  
	}

	public float overrideMovementSpeed(){
		return 1F;
	}
	
	public float overrideWingSpeed(){
		return 1F;
	}

	public void onDamageTakenEvent(DamageTakenEvent event){}
	
	public void onMotionUpdateEvent(MotionUpdateEvent event){}
	
	public void onTargetSetEvent(TargetSetEvent event){}
	
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){}
	
	public void onCollisionEvent(CollisionEvent event){}
	
	protected final int getDifficulty(){
		return dragon.worldObj.difficultySetting.getDifficultyId();
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof DragonSpecialAttackBase && ((DragonSpecialAttackBase)o).id == id;
	}
	
	public boolean equals(DragonSpecialAttackBase attack){
		return attack != null && attack.id == this.id;
	}

	@Override
	public int hashCode(){
		return id;
	}
}