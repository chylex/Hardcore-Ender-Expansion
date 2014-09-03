package chylex.hee.mechanics.essence;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.handler.AltarActionHandler;
import chylex.hee.mechanics.essence.handler.DragonEssenceHandler;
import chylex.hee.mechanics.essence.handler.FieryEssenceHandler;
import chylex.hee.system.util.IItemSelector;

public enum EssenceType{
	INVALID(0, "Basic", AltarActionHandler.class, new RuneItem[]{}, new float[]{ 1F, 1F, 1F }),
	
	DRAGON(1, "Dragon", DragonEssenceHandler.class, new RuneItem[]{
		new RuneItem(Items.ender_pearl, "random.glass"),
		new RuneItem(Items.ender_eye, "random.glass"),
		new RuneItem(ItemList.enderman_head, "dig.stone").setShowcaseItem(new ItemStack(BlockList.enderman_head)),
		new RuneItem(Blocks.end_stone, "dig.stone"),
		new RuneItem(Items.book, "hardcoreenderexpansion:player.random.pageflip"),
		new RuneItem(Blocks.bookshelf, "dig.wood"),
		new RuneItem(Blocks.iron_bars, "dig.stone"),
		new RuneItem(new IItemSelector(){
			@Override public boolean isValid(ItemStack is){
				return is.getItem() == Items.map || is.getItem() == Items.filled_map;
			}
		}, new ItemStack(Items.map), "hardcoreenderexpansion:player.random.pageflip"),
		new RuneItem(Blocks.obsidian, "dig.stone"),
		new RuneItem(Blocks.stonebrick, "dig.stone")
	}, new float[]{ 0.4648F,0.1914F,0.5195F }),

	FIERY(2, "Fiery", FieryEssenceHandler.class, new RuneItem[]{
		new RuneItem(ItemList.igneous_rock, "mob.ghast.fireball"),
		new RuneItem(Blocks.furnace, "dig.stone"),
		new RuneItem(Items.blaze_powder, "mob.blaze.hit"),
		new RuneItem(Items.blaze_rod, "mob.blaze.hit"),
		new RuneItem(Items.magma_cream, "mob.magmacube.big"),
		new RuneItem(Items.fire_charge, "mob.ghast.fireball"),
		new RuneItem(Items.lava_bucket, "random.fizz"),
		new RuneItem(Items.brewing_stand, "dig.stone"),
		new RuneItem(Items.flint_and_steel, "fire.ignite"),
		new RuneItem(Blocks.coal_block, "dig.stone")
	}, new float[]{ 0.5898F,0.4023F,0.125F }),
	
	SPECTRAL(3, "Spectral", AltarActionHandler.class, new RuneItem[]{
		new RuneItem(Items.fish, "random.burp"),
		new RuneItem(Items.chicken, "random.burp"),
		new RuneItem(Blocks.red_flower, "dig.dirt"),
		new RuneItem(Items.emerald, "random.glass"),
		new RuneItem(Items.ghast_tear, "mob.ghast.scream"),
		new RuneItem(Items.spider_eye, "mob.spider.say"),
		new RuneItem(Items.gold_ingot, "random.glass"),
		new RuneItem(Items.egg, "mob.chicken.plop")
	}, new float[]{ 0.1875F,0.1641F,0.5273F });
	
	public final byte id;
	public final String essenceName;
	public final String essenceNameLowercase;
	public final Class<? extends AltarActionHandler> actionHandlerClass;
	public final RuneItem[] itemsNeeded;
	public final float[] glyphColors;
	
	private EssenceType(int id, String essenceName, Class<? extends AltarActionHandler> actionHandlerClass, RuneItem[] itemsNeeded, float[] glyphColors){
		this.id = (byte)id;
		this.essenceName = essenceName;
		this.essenceNameLowercase = essenceName.toLowerCase();
		this.actionHandlerClass = actionHandlerClass;
		this.itemsNeeded = itemsNeeded;
		this.glyphColors = glyphColors;
		
		for(byte a = 0; a < itemsNeeded.length; a++)itemsNeeded[a].indexInArray = a;
	}
	
	public byte getItemDamage(){
		return (byte)(id-1);
	}
	
	public static EssenceType getById(int id){
		for(EssenceType type:values()){
			if (type.id == id)return type;
		}
		return INVALID;
	}
}
