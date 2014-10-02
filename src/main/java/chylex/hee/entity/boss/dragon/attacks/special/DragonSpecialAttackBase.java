package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.util.MathUtil;

public abstract class DragonSpecialAttackBase{
	protected static Random rand = new Random();
	
	protected EntityBossDragon dragon;
	protected float damageTaken;
	protected float damageDealt;
	protected TObjectFloatHashMap<String> lastPlayerHealth = new TObjectFloatHashMap<>();
	public int tick;
	public int phase;
	public double previousEffectivness;
	public double newEffectivness;
	public double effectivness;
	private byte[] disabledPassiveAttacks = new byte[0];
	public boolean disabled;
	public final byte id;
	
	public DragonSpecialAttackBase(EntityBossDragon dragon, int attackId){
		if (!dragon.attacks.registerSpecialAttack(this,attackId)){
			this.id = -1;
			return;
		}
		this.dragon = dragon;
		this.id = (byte)attackId;
	}
	
	public DragonSpecialAttackBase setDisabled(){
		this.disabled = true;
		return this;
	}
	
	public DragonSpecialAttackBase setDisabledPassiveAttacks(DragonPassiveAttackBase...attacks){
		disabledPassiveAttacks = new byte[attacks.length];
		for(int a = 0; a < attacks.length; a++)disabledPassiveAttacks[a] = attacks[a].id;
		return this;
	}
	
	public boolean isPassiveAttackDisabled(DragonPassiveAttackBase attack){
		for(byte id:disabledPassiveAttacks){
			if (id == attack.id)return true;
		}
		return false;
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
		for(Object o:dragon.worldObj.playerEntities){
			if (o instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)o;
				String username = player.getCommandSenderName();
				
				if (lastPlayerHealth.containsKey(username)){
					float last = lastPlayerHealth.get(username);
					if (player.getHealth() < last)damageDealt += (last-player.getHealth());
				}
				
				lastPlayerHealth.put(username,player.getHealth());
			}
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
	
	public boolean hasEnded(){
		return true;
	}
	
	public int getNextAttackTimer(){
		return Math.max(140,180+rand.nextInt(100)+((4-dragon.getWorldDifficulty())*30)-dragon.worldObj.playerEntities.size()*15);  
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
	
	@Override
	public boolean equals(Object o){
		if (o instanceof DragonSpecialAttackBase){
			return ((DragonSpecialAttackBase)o).id == this.id;
		}
		return false;
	}
	
	public boolean equals(DragonSpecialAttackBase attack){
		return attack != null && attack.id == this.id;
	}

	@Override
	public int hashCode(){
		return id;
	}
}