package chylex.hee.mechanics.minions.properties;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.minions.MinionData;

public enum MinionModifiers implements IMinionInfusionHandler{
	ADD_ABILITY(0, new ItemStack[]{
		new ItemStack(ItemList.end_powder),
		new ItemStack(Items.quartz),
		new ItemStack(Blocks.emerald_block),
		new ItemStack(Items.book)
	}),
	
	ADD_ATTRIBUTE(0, new ItemStack[]{
		new ItemStack(ItemList.end_powder),
		new ItemStack(Items.glowstone_dust),
		new ItemStack(Blocks.iron_block),
		new ItemStack(Items.book)
	});
	
	private byte cost;
	private final ItemStack[] recipe;
	
	MinionModifiers(int cost, ItemStack[] recipe){
		this.cost = (byte)cost;
		this.recipe = recipe;
	}

	@Override
	public boolean canApply(MinionData data){
		return data.getModifiersLeft() > 0;
	}
	
	@Override
	public void apply(MinionData data){
		data.addModifier(this);
	}
	
	@Override
	public int getEssenceCost(){
		return cost;
	}
	
	@Override
	public ItemStack[] getRecipe(){
		return recipe;
	}
}
