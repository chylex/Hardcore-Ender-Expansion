package chylex.hee.entity.boss.dragon.managers;
import gnu.trove.list.array.TByteArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.util.DragonUtil;

public class DragonAttackManager{
	protected final Random rand = new Random();
	private final List<DragonPassiveAttackBase> passiveAttackList = new ArrayList<>();
	private final List<DragonSpecialAttackBase> specialAttackList = new ArrayList<>();
	
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
	}
	
	public boolean biteClosePlayers(){
		for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.getEntitiesWithinAABB(EntityPlayer.class,dragon.dragonPartHead.boundingBox.expand(2.8D,2.4D,2.8D))){
			int diff = dragon.worldObj.difficultySetting.getDifficultyId(), rm;
			player.attackEntityFrom(DamageSource.causeMobDamage(dragon),(ModCommonProxy.opMobs ? 9F : 4F)+diff);
			
			switch(diff){
				case 3: rm = 34; break;
				case 2: rm = 22; break;
				case 1: rm = 15; break;
				default: rm = 10;
			}
			
			if (rand.nextInt(100) < rm){
				player.addPotionEffect(new PotionEffect(Potion.poison.id,180+25*(diff),0));
				dragon.rewards.addHandicap(0.1F,false);
				if (rand.nextInt(100) < 35+diff*12){
					player.addPotionEffect(new PotionEffect(Potion.blindness.id,200+18*(diff),0));
					player.addPotionEffect(new PotionEffect(Potion.confusion.id,160+20*(diff),0));
				}
			}
		}
		
		return false;
	}
	
	public List<EntityPlayer> getViablePlayers(){
		List<EntityPlayer> players = dragon.worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(-160D,-32D,-160D,160D,512D,160D));
		
		if (players.size() > 1){
			for(Iterator<EntityPlayer> iter = players.iterator(); iter.hasNext();){
				if (iter.next().capabilities.isCreativeMode)iter.remove();
			}
		}
		
		return players;
	}
	
	public EntityPlayer getRandomPlayer(){
		List<EntityPlayer> list = getViablePlayers();
		return list.isEmpty() ? null : list.get(rand.nextInt(list.size()));
	}
	
	public EntityPlayer getWeakPlayer(){
		List<EntityPlayer> list = getViablePlayers();
		
		if (list.isEmpty())return null;
		else if (list.size() == 1)return list.get(0);
		
		WeightedList<ObjectWeightPair<EntityPlayer>> players = new WeightedList<>();
		for(EntityPlayer p:list)players.add(ObjectWeightPair.of(p,5+((int)p.getHealth()>>1)+(p.getTotalArmorValue()>>2)));
		return players.getRandomItem(rand).getObject();
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
		
		Map<Byte,Double> effList = new TreeMap<>();
		TByteArrayList notTried = new TByteArrayList();
		
		for(DragonSpecialAttackBase attack:specialAttackList){
			if (attack.disabled || !attack.canStart())continue;
			if (attack.previousEffectivness == 0D && attack.newEffectivness == 0D)notTried.add(attack.id);
			
			double add = 0D;
			if (attack.equals(lastAttack))add -= attack.effectivness*0.6D; // lower chance of retrying
			if (attack.newEffectivness > attack.previousEffectivness)add += 0.5D*(attack.newEffectivness-attack.previousEffectivness); // if attack went better, increase chance
			effList.put(attack.id,attack.effectivness+add);
		}
		
		if (effList.isEmpty())return null;
		
		if (notTried.size() > 1 || (notTried.size() == 1 && rand.nextBoolean())){
			return getSpecialAttackById(notTried.get(rand.nextInt(notTried.size()))); // try a new attack
		}
		
		SortedSet<Entry<Byte,Double>> effSorted = DragonUtil.sortMapByValueDescending(effList);
		int maxId = rand.nextInt(Math.min(healthPercentage < 30?3:4,effSorted.size()));

		for(Entry<Byte,Double> entry:effSorted){
			if (maxId-- <= 0)return getSpecialAttackById(entry.getKey());
		}
			
		return null;
	}
	
	public int getHealthPercentage(){
		return (int)((100F/dragon.getMaxHealth())*dragon.getHealth());
	}
	
	public NBTTagCompound writeToNBT(){
		NBTTagCompound tag = new NBTTagCompound();

		for(DragonSpecialAttackBase attack:specialAttackList){
			tag.setDouble("p_"+attack.id,attack.previousEffectivness);
			tag.setDouble("n_"+attack.id,attack.newEffectivness);
			tag.setDouble("c_"+attack.id,attack.effectivness);
		}
		
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag){
		for(DragonSpecialAttackBase attack:specialAttackList){
			attack.previousEffectivness = tag.getDouble("p_"+attack.id);
			attack.newEffectivness = tag.getDouble("n_"+attack.id);
			attack.effectivness = tag.getDouble("c_"+attack.id);
		}
	}
}