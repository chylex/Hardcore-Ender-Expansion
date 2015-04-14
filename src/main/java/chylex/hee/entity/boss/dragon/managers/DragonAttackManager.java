package chylex.hee.entity.boss.dragon.managers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.util.MathUtil;

public class DragonAttackManager{
	private final List<DragonPassiveAttackBase> passiveAttackList = new ArrayList<>();
	private final List<DragonSpecialAttackBase> specialAttackList = new ArrayList<>();
	private final WeightedList<DragonSpecialAttackBase> specialAttackWeights = new WeightedList<>();
	private final Queue<Byte> specialAttackQueue = new LinkedList<>();
	
	protected EntityBossDragon dragon;
	
	public DragonAttackManager(EntityBossDragon dragon){
		this.dragon = dragon;
	}
	
	public void registerPassive(DragonPassiveAttackBase attack){
		if (getPassiveAttackById(attack.id) != null)throw new IllegalArgumentException("Tried to register passive dragon attack with already registered attack ID "+attack.id);
		passiveAttackList.add(attack);
	}
	
	public void registerSpecial(DragonSpecialAttackBase attack){
		if (getSpecialAttackById(attack.id) != null)throw new IllegalArgumentException("Tried to register special dragon attack with already registered attack ID "+attack.id);
		specialAttackList.add(attack);
		if (attack.getWeight() != -1)specialAttackWeights.add(attack);
	}
	
	public DragonPassiveAttackBase getPassiveAttackById(int id){
		for(DragonPassiveAttackBase attack:passiveAttackList){
			if (attack.id == id)return attack;
		}
		
		return null;
	}
	
	public DragonSpecialAttackBase getSpecialAttackById(int id){
		for(DragonSpecialAttackBase attack:specialAttackList){
			if (attack.id == id)return attack;
		}
		
		return null;
	}
	
	public List<DragonSpecialAttackBase> getSpecialAttackList(){
		return Collections.unmodifiableList(specialAttackList);
	}
	
	public boolean isPlayerViable(EntityPlayer player){
		return MathUtil.distance(player.posX,player.posZ) <= 160D; 
	}
	
	public List<EntityPlayer> getViablePlayers(){
		List<EntityPlayer> players = dragon.worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(-160D,-32D,-160D,160D,512D,160D));
		
		if (players.size() > 1){
			for(Iterator<EntityPlayer> iter = players.iterator(); iter.hasNext();){
				EntityPlayer player = iter.next();
				if (player.capabilities.isCreativeMode || player.isDead)iter.remove();
			}
		}
		
		return players;
	}
	
	public EntityPlayer getRandomPlayer(){
		List<EntityPlayer> list = getViablePlayers();
		return list.isEmpty() ? null : list.get(dragon.worldObj.rand.nextInt(list.size()));
	}
	
	public EntityPlayer getWeakPlayer(){
		List<EntityPlayer> list = getViablePlayers();
		
		if (list.isEmpty())return null;
		else if (list.size() == 1)return list.get(0);
		
		WeightedList<ObjectWeightPair<EntityPlayer>> players = new WeightedList<>();
		for(EntityPlayer p:list)players.add(ObjectWeightPair.of(p,5+((int)p.getHealth()>>1)+(p.getTotalArmorValue()>>2)));
		return players.getRandomItem(dragon.worldObj.rand).getObject();
	}
	
	public void updatePassiveAttacks(DragonSpecialAttackBase currentSpecialAttack){
		for(DragonPassiveAttackBase attack:passiveAttackList){
			if (currentSpecialAttack != null && currentSpecialAttack.isPassiveAttackDisabled(attack.id))continue;
			attack.update();
		}
	}
	
	public DragonSpecialAttackBase pickSpecialAttack(DragonSpecialAttackBase lastAttack){
		int healthPercentage = getHealthPercentage();
		if (healthPercentage == 0)return null;
		
		if (specialAttackQueue.isEmpty()){
			WeightedList<DragonSpecialAttackBase> list = new WeightedList<DragonSpecialAttackBase>(specialAttackWeights);
			
			for(int a = 0, amt = list.size()-2; a < amt; a++){
				DragonSpecialAttackBase attack = list.getRandomItem(dragon.worldObj.rand);
				list.remove(attack);
				specialAttackQueue.add(Byte.valueOf(attack.id));
			}
		}
		
		return getSpecialAttackById(specialAttackQueue.poll());
	}
	
	public boolean biteClosePlayers(){
		boolean res = false;
		
		for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.getEntitiesWithinAABB(EntityPlayer.class,dragon.dragonPartHead.boundingBox.expand(2.2D,1.5D,2.2D))){
			int diff = dragon.worldObj.difficultySetting.getDifficultyId(), rm;
			player.attackEntityFrom(DamageSource.causeMobDamage(dragon),(ModCommonProxy.opMobs ? 14F : 9F)+diff);
			
			switch(diff){
				case 3: rm = 31; break;
				case 2: rm = 20; break;
				case 1: rm = 14; break;
				default: rm = 9;
			}
			
			if (dragon.worldObj.rand.nextInt(100) < rm){
				player.addPotionEffect(new PotionEffect(Potion.poison.id,90+30*diff,ModCommonProxy.opMobs ? 1 : 0));
				dragon.rewards.addHandicap(0.1F,false);
				
				if (dragon.worldObj.rand.nextInt(100) < 35+diff*12){
					player.addPotionEffect(new PotionEffect(Potion.blindness.id,160+24*diff,0));
					player.addPotionEffect(new PotionEffect(Potion.confusion.id,80+24*diff,0));
				}
			}
			
			res = true;
		}
		
		return res;
	}
	
	public int getHealthPercentage(){
		return (int)((100F/dragon.getMaxHealth())*dragon.getHealth());
	}
	
	public NBTTagCompound writeToNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByteArray("attq",ArrayUtils.toPrimitive(specialAttackQueue.toArray(new Byte[specialAttackQueue.size()])));
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag){
		for(byte b:tag.getByteArray("attq"))specialAttackQueue.add(Byte.valueOf(b));
	}
}