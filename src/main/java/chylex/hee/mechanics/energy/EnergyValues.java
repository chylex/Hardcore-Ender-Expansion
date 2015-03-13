package chylex.hee.mechanics.energy;
import gnu.trove.map.hash.TObjectFloatHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.MathUtil;

public final class EnergyValues{
	private static final TObjectFloatHashMap<ItemDamagePair> items = new TObjectFloatHashMap<>();
	private static final TObjectFloatHashMap<Class<? extends EntityLivingBase>> mobs = new TObjectFloatHashMap<>();
	
	static{
		setItemEnergy(Blocks.end_stone, 0.40F);
		setItemEnergy(BlockList.end_terrain, 0.50F);
		setItemEnergy(ItemList.adventurers_diary, 0.50F);
		setItemEnergy(BlockList.persegrit, 0.55F);
		setItemEnergy(ItemList.silverfish_blood, 0.65F);
		setItemEnergy(ItemList.stardust, 0.75F);
		setItemEnergy(ItemList.end_powder, 0.80F);
		setItemEnergy(ItemList.igneous_rock, 0.90F);
		setItemEnergy(ItemList.endium_ingot, 1.10F);
		setItemEnergy(ItemList.knowledge_note, 1.20F);
		setItemEnergy(Items.ender_pearl, 1.50F);
		setItemEnergy(ItemList.enhanced_ender_pearl, 1.65F);
		setItemEnergy(BlockList.enhanced_brewing_stand, 1.65F);
		setItemEnergy(BlockList.enhanced_tnt, 1.65F);
		setItemEnergy(Items.ender_eye, 1.95F);
		setItemEnergy(ItemList.instability_orb, 2.60F);
		setItemEnergy(BlockList.spooky_log, 3.10F);
		setItemEnergy(BlockList.spooky_leaves, 3.10F);
		setItemEnergy(ItemList.rune, 3.25F);
		setItemEnergy(ItemList.auricion, 3.40F);
		setItemEnergy(Blocks.ender_chest, 3.75F);
		setItemEnergy(ItemList.temple_caller, 4.00F);
		setItemEnergy(ItemList.ectoplasm, 4.20F);
		setItemEnergy(ItemList.spectral_tear, 4.20F);
		setItemEnergy(BlockList.void_chest, 4.30F);
		setItemEnergy(ItemList.charm, 4.80F);
		setItemEnergy(ItemList.spatial_dash_gem, 5.50F);
		setItemEnergy(ItemList.energy_wand_core, 6.20F);
		setItemEnergy(ItemList.energy_wand, 6.40F);
		setItemEnergy(ItemList.transference_gem, 7.80F);
		setItemEnergy(BlockList.endium_block, 10.10F);
		setItemEnergy(ItemList.living_matter, 10.50F);
		
		setMobEnergy(EntityMobEnderman.class, 0.85F);
		setMobEnergy(EntityMobAngryEnderman.class, 0.85F);
		setMobEnergy(EntityMobHomelandEnderman.class, 0.85F);
		setMobEnergy(EntityMobBabyEnderman.class, 0.40F);
		setMobEnergy(EntityMobEndermage.class, 1.25F);
		setMobEnergy(EntityMiniBossEnderEye.class, 6.50F);
		setMobEnergy(EntityBossDragon.class, 24.00F);
	}
	
	private static void setItemEnergy(Block block, float energyUnits){
		items.put(new ItemDamagePair(Item.getItemFromBlock(block),-1),EnergyChunkData.energyDrainUnit*energyUnits);
	}
	
	private static void setItemEnergy(Item item, float energyUnits){
		items.put(new ItemDamagePair(item,-1),EnergyChunkData.energyDrainUnit*energyUnits);
	}
	
	public static boolean setItemEnergy(ItemDamagePair pair, float energyUnits){
		if (!MathUtil.floatEquals(getItemEnergy(new ItemStack(pair.item,1,pair.damage == -1 ? 0 : pair.damage)),0F))return false;
		items.put(pair,EnergyChunkData.energyDrainUnit*energyUnits);
		return true;
	}
	
	public static float getItemEnergy(ItemStack is){
		for(ItemDamagePair idp:items.keySet()){
			if (idp.check(is))return items.get(idp);
		}
		
		return 0F;
	}
	
	public static boolean setMobEnergy(Class<? extends EntityLivingBase> entityClass, float energyUnits){
		if (mobs.containsKey(entityClass))return false;
		mobs.put(entityClass,EnergyChunkData.energyDrainUnit*energyUnits);
		return true;
	}
	
	public static float getMobEnergy(EntityLivingBase entity){
		float amt = mobs.get(entity.getClass());
		return amt == mobs.getNoEntryValue() ? 0 : amt;
	}
	
	private EnergyValues(){}
}
