package chylex.hee.entity.boss.dragon.managers;
import gnu.trove.list.array.TByteArrayList;
import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.util.DamageSource;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;

public class DragonAttackManager{
	public static boolean nocreative = false;
	
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
	
	public boolean registerPassiveAttack(DragonPassiveAttackBase attack, int attackId){
		if (getPassiveAttackById(attackId) != null){
			DragonUtil.severe("Tried to register passive dragon attack with already registered attack ID %0%!",attackId);
			return false;
		}
		passiveAttackList.add(attack);
		return true;
	}
	
	public boolean registerSpecialAttack(DragonSpecialAttackBase attack, int attackId){
		if (getSpecialAttackById(attackId) != null){
			DragonUtil.severe("Tried to register special dragon attack with already registered attack ID %0%!",attackId);
			return false;
		}
		specialAttackList.add(attack);
		return true;
	}
	
	public boolean biteClosePlayer(){
		EntityPlayer player = null;
		for(Object e:dragon.worldObj.getEntitiesWithinAABBExcludingEntity(dragon,dragon.dragonPartHead.boundingBox.expand(2.8D,2.4D,2.8D))){
			if (e instanceof EntityPlayer){
				player = (EntityPlayer)e;
				break;
			}
		}
		if (player == null)return false;
		int diff = dragon.getWorldDifficulty(), rm;
		player.attackEntityFrom(DamageSource.causeMobDamage(dragon),(ModCommonProxy.opMobs?9F:4F)+diff);
		
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
		
		KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment(player,0.1F,new byte[]{ 0,3 });
		
		return true;
	}
	
	public EntityPlayer getRandomPlayer(){
		if (DragonAttackManager.nocreative){
			List<EntityPlayer> list = new ArrayList<>();
			for(Object o:dragon.worldObj.playerEntities){
				EntityPlayer p = (EntityPlayer)o;
				if (!p.capabilities.isCreativeMode)list.add(p);
			}
			return list.isEmpty() ? null : list.get(rand.nextInt(list.size()));
		}
		
		if (dragon.worldObj.playerEntities.isEmpty())return null;
		return (EntityPlayer)dragon.worldObj.playerEntities.get(rand.nextInt(dragon.worldObj.playerEntities.size()));
	}
	
	public EntityPlayer getWeakPlayer(){
		List<?> list = dragon.worldObj.playerEntities;
		if (list.isEmpty())return null;
		else if (list.size() == 1)return getRandomPlayer();
		
		WeightedList<ObjectWeightPair<EntityPlayer>> players = new WeightedList<>();
		
		for(Object o:list){
			EntityPlayer p = (EntityPlayer)o;
			if ((nocreative && p.capabilities.isCreativeMode) || p.isDead)continue;
			
			players.add(new ObjectWeightPair<EntityPlayer>(p,5+((int)p.getHealth()>>1)+(p.getTotalArmorValue()>>2)));
		}

		return players.getRandomItem(rand).getObject();
	}
	
	public void updatePassiveAttacks(DragonSpecialAttackBase currentSpecialAttack){
		for(DragonPassiveAttackBase attack:passiveAttackList){
			if (currentSpecialAttack != null && currentSpecialAttack.isPassiveAttackDisabled(attack))continue;
			attack.update();
		}
	}
	
	public DragonSpecialAttackBase pickSpecialAttack(DragonSpecialAttackBase lastAttack){
		int healthPercentage = getHealthPercentage();
		if (healthPercentage == 0)return null;
		
		Map<Byte,Double> effList = new TreeMap<>();
		TByteArrayList notTried = new TByteArrayList();
		//DragonUtil.info("Starting attack picking");
		
		for(DragonSpecialAttackBase attack:specialAttackList){
			if (attack.disabled || !attack.canStart())continue;
			//DragonUtil.info("Testing attack ID %0%",attack.id);
			if (attack.previousEffectivness == 0D && attack.newEffectivness == 0D)notTried.add(attack.id);
			//DragonUtil.info("Not tried yet - %0%",attack.previousEffectivness == 0D && attack.newEffectivness == 0D);
			
			double add = 0D;
			if (attack.equals(lastAttack))add -= attack.effectivness*0.6D; // lower chance of retrying
			if (attack.newEffectivness > attack.previousEffectivness)add += 0.5D*(attack.newEffectivness-attack.previousEffectivness); // if attack went better, increase chance
			//DragonUtil.info("Effectivness: %0% (+ add %1%)",attack.effectivness,add);
			effList.put(attack.id,attack.effectivness+add);
		}
		
		//DragonUtil.info("effList size: %0%",effList.size());
		//DragonUtil.info("notTried size: %0%",notTried.size());
		if (effList.isEmpty())return null;
		
		if (notTried.size() > 1 || (notTried.size() == 1 && rand.nextBoolean())){
			return getSpecialAttackById(notTried.get(rand.nextInt(notTried.size()))); // try a new attack
		}
		
		//DragonUtil.info("Choosing attacks based on efficiency");
		SortedSet<Entry<Byte,Double>> effSorted = DragonUtil.sortMapByValueDescending(effList);
		//for(Entry<Byte,Double> entry:effSorted)DragonUtil.info("Attack %0%, efficiency %1%",entry.getKey(),entry.getValue());
		int maxId = rand.nextInt(Math.min(healthPercentage < 30?3:4,effSorted.size()));
		//DragonUtil.info("maxId is %0%",maxId);
		for(Entry<Byte,Double> entry:effSorted){
			if (maxId-- <= 0)return getSpecialAttackById(entry.getKey());
		}
		
		//DragonUtil.info("Still no decision!");		
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