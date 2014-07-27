package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

final class RavagedDungeonLoot{
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
		new ItemStack(Blocks.deadbush), // dead bush
		
	};
	
	private RavagedDungeonLoot(){}
}
