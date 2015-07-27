package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;

public final class RavagedDungeonLoot{
	public static ItemStack[] flowerPotItems = new ItemStack[]{
		new ItemStack(Blocks.sapling,1,2), // birch sapling
		new ItemStack(Blocks.sapling,1,3), // jungle sapling
		new ItemStack(Blocks.yellow_flower), // dandelion
		new ItemStack(Blocks.red_flower,1,0), // poppy
		new ItemStack(Blocks.red_flower,1,1), // blue orchid
		new ItemStack(Blocks.red_flower,1,2), // allium
		new ItemStack(Blocks.red_flower,1,3), // azure bluet
		new ItemStack(Blocks.red_flower,1,8), // oxeye daisy
		new ItemStack(Blocks.tallgrass,1,2), // fern
		new ItemStack(Blocks.deadbush), // dead bush,
		new ItemStack(BlockList.death_flower,1,0), // death flower
		new ItemStack(BlockList.death_flower,1,15) // decayed death flower
	};
	
	public static final WeightedLootList lootGeneral = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.end_powder).setAmount(1,5).setWeight(100),
		new LootItemStack(Items.paper).setAmount(1,7).setWeight(68),
		new LootItemStack(BlockList.ravaged_brick).setAmount(1,5).setWeight(56),
		new LootItemStack(BlockList.ravaged_brick).setAmount(1,4).setDamage(1,BlockRavagedBrick.metaAmount-1).setWeight(50),
		new LootItemStack(BlockList.ravaged_brick_glow).setAmount(1,3).setWeight(46),
		new LootItemStack(Items.leather).setAmount(1,4).setWeight(44),
		new LootItemStack(ItemList.stardust).setAmount(1,3).setWeight(20),
		new LootItemStack(ItemList.rune).setDamage(0,4).setWeight(6),
		new LootItemStack(ItemList.music_disk).setDamage(0,ItemMusicDisk.getRecordCount()-1).setWeight(5)
	});
	
	public static final WeightedLootList lootUncommon = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(ItemList.end_powder).setAmount(3,7).setWeight(80),
		new LootItemStack(Items.paper).setAmount(1,7).setWeight(65),
		new LootItemStack(Items.book).setAmount(1,6).setWeight(59),
		new LootItemStack(Items.leather).setAmount(1,5).setWeight(50),
		new LootItemStack(BlockList.ravaged_brick).setAmount(4,8).setWeight(45),
		new LootItemStack(Items.ender_pearl).setAmount(1,3).setWeight(44),
		new LootItemStack(BlockList.ravaged_brick).setAmount(2,6).setDamage(1,BlockRavagedBrick.metaAmount-1).setWeight(42),
		new LootItemStack(BlockList.ravaged_brick_glow).setAmount(2,6).setWeight(42),
		new LootItemStack(Items.iron_ingot).setAmount(1,5).setWeight(40),
		new LootItemStack(ItemList.enhanced_ender_pearl).setAmount(1,3).setWeight(35),
		new LootItemStack(Items.gold_ingot).setAmount(1,4).setWeight(32),
		new LootItemStack(ItemList.stardust).setAmount(2,5).setWeight(25),
		new LootItemStack(Items.enchanted_book).setWeight(22),
		new LootItemStack(ItemList.rune).setDamage(0,4).setWeight(11),
		new LootItemStack(ItemList.music_disk).setDamage(0,ItemMusicDisk.getRecordCount()-1).setWeight(6),
		new LootItemStack(ItemList.rune).setDamage(5).setWeight(5),
	}).addItemPostProcessor((is, rand) -> {
		if (is.getItem() == ItemList.enhanced_ender_pearl){
			List<EnderPearlEnhancements> availableTypes = CollectionUtil.newList(EnderPearlEnhancements.values());
			int amount = 1+(int)Math.abs(Math.round(rand.nextDouble()*rand.nextGaussian()*1.5D));
			
			for(int a = 0; a < amount; a++){
				is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
				if (availableTypes.isEmpty())break;
			}
		}
		else if (is.getItem() == Items.enchanted_book){
			is.func_150996_a(Items.book); // OBFUSCATED set item
			EnchantmentHelper.addRandomEnchantment(rand,is,15+rand.nextInt(10));
		}
		
		return is;
	});
	
	public static final WeightedLootList lootRare = new WeightedLootList(new LootItemStack[]{
		new LootItemStack(Items.iron_ingot).setAmount(3,9).setWeight(42),
		new LootItemStack(Items.gold_ingot).setAmount(3,9).setWeight(42),
		new LootItemStack(Items.dye).setAmount(2,6).setDamage(4).setWeight(35),
		new LootItemStack(Items.diamond).setAmount(2,7).setWeight(28),
		new LootItemStack(Items.emerald).setAmount(2,7).setWeight(28),
		new LootItemStack(ItemList.enhanced_ender_pearl).setAmount(1,5).setWeight(24),
		new LootItemStack(ItemList.instability_orb).setAmount(1,3).setWeight(12),
		new LootItemStack(ItemList.rune).setDamage(0,4).setWeight(9),
		new LootItemStack(ItemList.rune).setDamage(5).setWeight(7),
		new LootItemStack(ItemList.charm_pouch).setWeight(1)
	}).addItemPostProcessor((is, rand) -> {
		if (is.getItem() == ItemList.enhanced_ender_pearl){
			List<EnderPearlEnhancements> availableTypes = CollectionUtil.newList(EnderPearlEnhancements.values());
			int amount = 1+(int)Math.abs(Math.round(rand.nextDouble()*rand.nextGaussian()*2.25D));
			
			for(int a = 0; a < amount; a++){
				is = EnhancementHandler.addEnhancement(is,availableTypes.remove(rand.nextInt(availableTypes.size())));
				if (availableTypes.isEmpty())break;
			}
		}
		
		return is;
	});
	
	public static final WeightedLootList lootEnd = new WeightedLootList(new LootItemStack[]{
			new LootItemStack(ItemList.end_powder).setAmount(4,16).setWeight(31),
			new LootItemStack(Items.iron_ingot).setAmount(9,20).setWeight(27),
			new LootItemStack(Items.gold_ingot).setAmount(8,17).setWeight(27),
			new LootItemStack(Items.diamond).setAmount(5,12).setWeight(25),
			new LootItemStack(ItemList.rune).setAmount(1,2).setDamage(0,4).setWeight(25),
			new LootItemStack(ItemList.stardust).setAmount(6,21).setWeight(20),
			new LootItemStack(ItemList.rune).setDamage(5).setWeight(12),
			new LootItemStack(ItemList.charm).setWeight(3)
	}).addItemPostProcessor((is, rand) -> {
		if (is.getItem() == ItemList.charm){
			CharmType[] types = CharmType.values();
			CharmType type = types[rand.nextInt(types.length)];
			is.setItemDamage(type.recipes[rand.nextInt(type.recipes.length)].id);
		}
		
		return is;
	});
	
	private RavagedDungeonLoot(){}
}
