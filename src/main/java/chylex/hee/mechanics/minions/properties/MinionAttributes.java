package chylex.hee.mechanics.minions.properties;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.minions.MinionData;

public enum MinionAttributes implements IMinionInfusionHandler{
	HEALTH(3, 0, new ItemStack[]{
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.speckled_melon),
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.iron_ingot)
	}),
	
	SPEED(3, 0, new ItemStack[]{
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.sugar),
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.ender_pearl)
	}),
	
	REGENERATION(3, 0, new ItemStack[]{
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.ghast_tear),
		new ItemStack(Blocks.obsidian),
		new ItemStack(Items.gold_ingot)
	}),
	
	ARMOR(3, 0, new ItemStack[]{
		
	}),
	
	STRENGTH(3, 0, new ItemStack[]{
		
	}),
	
	FORTUNE(3, 0, new ItemStack[]{
		
	}),
	
	CAPACITY(3, 0, new ItemStack[]{
		
	}),
	
	RANGE(3, 0, new ItemStack[]{
		
	});
	
	public final int maxLevel;
	private final byte cost;
	private final ItemStack[] recipe;
	
	MinionAttributes(int maxLevel, int cost, ItemStack[] recipe){
		this.maxLevel = maxLevel;
		this.cost = (byte)cost;
		this.recipe = recipe;
	}
	
	@Override
	public boolean canApply(MinionData data){
		return data.getAttributesLeft() > 0 && data.getAttributeLevel(this) < maxLevel;
	}
	
	@Override
	public void apply(MinionData data){
		data.increaseAttribute(this);
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
