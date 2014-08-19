package chylex.hee.mechanics.orb;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidBlock;
import chylex.hee.item.ItemList;
import chylex.hee.system.logging.Stopwatch;

public final class OrbAcquirableItems{
	public static final WeightedItemList idList = new WeightedItemList();
	
	public static void initialize(){
		Stopwatch.time("OrbAcquirableItems");
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			else if (biome.biomeID >= 128)break;
			idList.add(new WeightedItem(biome.topBlock,0,34));
			idList.add(new WeightedItem(biome.fillerBlock,0,34));
		}
		
		for(Object o:FurnaceRecipes.smelting().getSmeltingList().entrySet()){
			Entry<?,?> entry = (Entry<?,?>)o;
			ItemStack result = (ItemStack)entry.getValue();
			int weight = 30-(int)(7F*FurnaceRecipes.smelting().func_151398_b(result)); // OBFUSCATED get experience amount
			
			idList.add(new WeightedItem(((ItemStack)entry.getKey()).getItem(),0,weight));
			idList.add(new WeightedItem(result.getItem(),0,weight));
		}
		
		for(Object o:CraftingManager.getInstance().getRecipeList()){
			if (o instanceof ShapedRecipes){
				ShapedRecipes shaped = (ShapedRecipes)o;
				
				ItemStack output = shaped.getRecipeOutput();
				if (output == null)continue;
				if (output.getItem() == null){
					throw new RuntimeException("Hardcore Ender Expansion has found a defective recipe! Another mod is most likely registering a recipe BEFORE registering blocks and items. This is not a fault of Hardcore Ender Expansion, but it will cause a crash when crafting blocks from the defective mod! Please, remove other mods until this message stops appearing to find which mod causes the issue, and then report it to the mod author.");
				}
				
				idList.add(new WeightedItem(output.getItem(),output.getItemDamage(),21-shaped.recipeWidth*2-shaped.recipeHeight*2));
				
				for(ItemStack is:shaped.recipeItems){
					if (is != null)idList.add(new WeightedItem(is.getItem(),is.getItemDamage(),23-shaped.recipeWidth*2-shaped.recipeHeight*2));
				}
			}
			else if (o instanceof ShapelessRecipes){
				ShapelessRecipes shapeless = (ShapelessRecipes)o;
				
				ItemStack output = shapeless.getRecipeOutput();
				if (output == null)continue;
				if (output.getItem() == null){
					throw new RuntimeException("Hardcore Ender Expansion has found a defective recipe! Another mod is most likely registering a recipe BEFORE registering blocks and items. This is not a fault of Hardcore Ender Expansion, but it will cause a crash when crafting blocks from the defective mod! Please, remove other mods until this message stops appearing to find which mod causes the issue, and then report it to the mod author.");
				}
				
				idList.add(new WeightedItem(output.getItem(),output.getItemDamage(),24-shapeless.getRecipeSize()*2));
				
				for(Object item:shapeless.recipeItems){
					ItemStack is = (ItemStack)item;
					idList.add(new WeightedItem(is.getItem(),is.getItemDamage(),25-shapeless.getRecipeSize()*2));
				}
			}
		}
		
		/*
		 *  CLEANUP OF THINGS WE DON'T WANT
		 */
		
		for(Iterator<WeightedItem> iter = idList.iterator(); iter.hasNext();){
			Item item = iter.next().getItem();
			
			if (item == ItemList.enderman_relic || item == Item.getItemFromBlock(Blocks.fire))iter.remove();
			else if (item instanceof ItemBlock){
				Block block = Block.getBlockFromItem(item);
				if (block instanceof IFluidBlock || block instanceof BlockLiquid)iter.remove();
			}
		}

		Stopwatch.finish("OrbAcquirableItems");
	}
	
	private OrbAcquirableItems(){}
}
