package chylex.hee.mechanics.energy;
import gnu.trove.map.hash.TObjectFloatHashMap;
import net.minecraft.item.ItemStack;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.MathUtil;

public final class EnergyValues{
	public static final float unit = 0.05F;
	public static final float min = 0.00001F;
	
	private static final TObjectFloatHashMap<ItemDamagePair> items = new TObjectFloatHashMap<>();
	
	static{
		/* TODO setItemEnergy(Blocks.end_stone, 0.40F);
		setItemEnergy(BlockList.end_terrain, 0.50F);
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
		setItemEnergy(ItemList.ectoplasm, 4.20F);
		setItemEnergy(ItemList.spectral_tear, 4.20F);
		setItemEnergy(BlockList.void_chest, 4.30F);
		setItemEnergy(ItemList.charm, 4.80F);
		setItemEnergy(ItemList.spatial_dash_gem, 5.50F);
		setItemEnergy(ItemList.energy_wand_core, 6.20F);
		setItemEnergy(ItemList.energy_wand, 6.40F);
		setItemEnergy(ItemList.transference_gem, 7.80F);
		setItemEnergy(BlockList.endium_block, 10.10F);
		setItemEnergy(ItemList.living_matter, 10.50F);*/
	}
	
	/*private static void setItemEnergy(Block block, float energyUnits){
		items.put(new ItemDamagePair(Item.getItemFromBlock(block),-1),unit*energyUnits);
	}
	
	private static void setItemEnergy(Item item, float energyUnits){
		items.put(new ItemDamagePair(item,-1),unit*energyUnits);
	}*/
	
	public static boolean setItemEnergy(ItemDamagePair pair, float energyUnits){
		if (!MathUtil.floatEquals(getItemEnergy(new ItemStack(pair.item,1,pair.damage == -1 ? 0 : pair.damage)),0F))return false;
		items.put(pair,unit*energyUnits);
		return true;
	}
	
	public static float getItemEnergy(ItemStack is){
		for(ItemDamagePair idp:items.keySet()){
			if (idp.check(is))return items.get(idp);
		}
		
		return 0F;
	}
	
	private EnergyValues(){}
}
