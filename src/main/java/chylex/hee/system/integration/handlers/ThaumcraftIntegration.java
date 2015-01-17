package chylex.hee.system.integration.handlers;
import static thaumcraft.api.ThaumcraftApi.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockDungeonPuzzle;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockPersegrit;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.system.integration.IIntegrationHandler;

public class ThaumcraftIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "Thaumcraft";
	}

	@Override
	public void integrate(){
		
		// BLOCK ASPECTS
		
		registerObjectTag(block(BlockList.obsidian_falling), meta(0), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_stairs), meta(0,1,2,3,4,5,6,7), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special), meta(0,5), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special), meta(1,6), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special), meta(2,3,4), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special_glow), meta(0,5), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.LIGHT,5).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special_glow), meta(1,6), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.LIGHT,5).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.obsidian_special_glow), meta(2,3,4), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,2).add(Aspect.LIGHT,5).add(Aspect.EARTH,2));
		
		registerObjectTag(block(BlockList.essence_altar), meta(EssenceType.INVALID.id), new AspectList().add(Aspect.ELDRITCH,4).add(Aspect.MIND,5).add(Aspect.EARTH,6));
		registerObjectTag(block(BlockList.essence_altar), meta(EssenceType.DRAGON.id), new AspectList().add(Aspect.ELDRITCH,4).add(Aspect.ENERGY,10).add(Aspect.EXCHANGE,2).add(Aspect.MIND,5).add(Aspect.EARTH,6));
		registerObjectTag(block(BlockList.essence_altar), meta(EssenceType.FIERY.id), new AspectList().add(Aspect.ELDRITCH,4).add(Aspect.ENERGY,6).add(Aspect.FIRE,10).add(Aspect.MIND,5).add(Aspect.EARTH,6));
		registerObjectTag(item(ItemList.essence), meta(EssenceType.SPECTRAL.id), new AspectList());
		
		registerObjectTag(block(BlockList.enhanced_brewing_stand), meta(0,1,2,3), new AspectList().add(Aspect.CRAFT,3).add(Aspect.ENTROPY,3).add(Aspect.FIRE,4).add(Aspect.MAGIC,4).add(Aspect.EARTH,3).add(Aspect.WATER,2));
		
		registerObjectTag(block(BlockList.end_powder_ore), meta(0), new AspectList().add(Aspect.MAGIC,1).add(Aspect.EARTH,1));
		registerObjectTag(block(BlockList.stardust_ore), meta(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15), new AspectList().add(Aspect.ENERGY,3).add(Aspect.EXCHANGE,3).add(Aspect.EARTH,1));
		registerObjectTag(block(BlockList.igneous_rock_ore), meta(0), new AspectList().add(Aspect.FIRE,3).add(Aspect.EARTH,2));
		registerObjectTag(block(BlockList.instability_orb_ore), meta(0), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.EXCHANGE,1).add(Aspect.EARTH,1));
		
		registerObjectTag(block(BlockList.ender_goo), meta(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15), new AspectList().add(Aspect.POISON,4).add(Aspect.TAINT,2));
		
		registerObjectTag(block(BlockList.end_terrain), meta(BlockEndstoneTerrain.metaInfested), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.EARTH,1).add(Aspect.POISON,1));
		registerObjectTag(block(BlockList.end_terrain), meta(BlockEndstoneTerrain.metaBurned), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,1).add(Aspect.EARTH,1));
		registerObjectTag(block(BlockList.end_terrain), meta(BlockEndstoneTerrain.metaEnchanted), new AspectList().add(Aspect.DARKNESS,1).add(Aspect.EARTH,1).add(Aspect.MAGIC,1));
		
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataThornBush), new AspectList().add(Aspect.DEATH,1).add(Aspect.PLANT,1).add(Aspect.POISON,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataInfestedFern), new AspectList().add(Aspect.PLANT,1).add(Aspect.POISON,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataInfestedGrass), new AspectList().add(Aspect.PLANT,1).add(Aspect.POISON,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataInfestedTallgrass), new AspectList().add(Aspect.PLANT,1).add(Aspect.POISON,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataLilyFire), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.LIFE,1).add(Aspect.PLANT,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataVioletMossModerate), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.LIFE,1).add(Aspect.PLANT,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataVioletMossShort), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.LIFE,1).add(Aspect.PLANT,1));
		registerObjectTag(block(BlockList.crossed_decoration), meta(BlockCrossedDecoration.dataVioletMossTall), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.LIFE,1).add(Aspect.PLANT,1));
		
		registerObjectTag(block(BlockList.spooky_log), meta(0), new AspectList().add(Aspect.TREE,3));
		registerObjectTag(block(BlockList.spooky_log), meta(1,2,3,4), new AspectList().add(Aspect.DEATH,2).add(Aspect.SOUL,1).add(Aspect.TREE,3));
		registerObjectTag(block(BlockList.spooky_leaves), meta(0), new AspectList().add(Aspect.PLANT,1));
		
		registerObjectTag(block(BlockList.soul_charm), meta(0), new AspectList().add(Aspect.AURA,8).add(Aspect.ENTROPY,4).add(Aspect.SENSES,3).add(Aspect.SOUL,2).add(Aspect.TRAVEL,1));
		
		registerObjectTag(block(BlockList.death_flower), meta(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14), new AspectList().add(Aspect.DEATH,1).add(Aspect.ENTROPY,1).add(Aspect.PLANT,1));
		registerObjectTag(block(BlockList.death_flower), meta(15), new AspectList().add(Aspect.DEATH,6).add(Aspect.ENTROPY,3).add(Aspect.SENSES,1).add(Aspect.PLANT,1));
		
		registerObjectTag(block(BlockList.custom_spawner), meta(0), new AspectList().add(Aspect.BEAST,4).add(Aspect.DEATH,1).add(Aspect.MAGIC,4).add(Aspect.TRAVEL,4));			
		registerObjectTag(block(BlockList.custom_spawner), meta(1), new AspectList().add(Aspect.BEAST,2).add(Aspect.DEATH,2).add(Aspect.EARTH,1).add(Aspect.MAGIC,4).add(Aspect.TRAVEL,4));
		
		registerObjectTag(block(BlockList.endium_ore), meta(0), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.METAL,2).add(Aspect.EARTH,1));
		registerObjectTag(block(BlockList.endium_block), meta(0), new AspectList().add(Aspect.ELDRITCH,13).add(Aspect.METAL,20));

		registerObjectTag(block(BlockList.sphalerite), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.VOID,1));
		registerObjectTag(block(BlockList.sphalerite), meta(1), new AspectList().add(Aspect.EARTH,1).add(Aspect.EXCHANGE,3).add(Aspect.ENERGY,3));

		registerObjectTag(block(BlockList.cinder), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.FIRE,1));
		
		registerObjectTag(block(BlockList.transport_beacon), meta(0), new AspectList().add(Aspect.ELDRITCH,8).add(Aspect.TRAVEL,5).add(Aspect.LIGHT,4).add(Aspect.MAGIC,4));

		registerObjectTag(block(BlockList.laboratory_obsidian), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.FIRE,1).add(Aspect.MIND,1));
		registerObjectTag(block(BlockList.laboratory_floor), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.FIRE,1).add(Aspect.MIND,1));
		registerObjectTag(block(BlockList.laboratory_stairs), meta(0,1,2,3,4,5,6,7), new AspectList().add(Aspect.EARTH,1).add(Aspect.FIRE,1).add(Aspect.MIND,1));
		registerObjectTag(block(BlockList.laboratory_glass), meta(0), new AspectList().add(Aspect.CRYSTAL,1).add(Aspect.FIRE,1).add(Aspect.MIND,1));
		
		for(int i=0;i<BlockRavagedBrick.metaAmount;i++)
			registerObjectTag(block(BlockList.ravaged_brick), meta(i), new AspectList().add(Aspect.EARTH,1).add(Aspect.ELDRITCH,1));
		registerObjectTag(block(BlockList.ravaged_brick_smooth), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.ELDRITCH,1));
		registerObjectTag(block(BlockList.ravaged_brick_glow), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.ELDRITCH,1).add(Aspect.LIGHT,5));

		for(int i=0;i<BlockDungeonPuzzle.metaAmount;i++)
			registerObjectTag(block(BlockList.dungeon_puzzle), meta(i), new AspectList().add(Aspect.EARTH,1).add(Aspect.ELDRITCH,1).add(Aspect.MECHANISM, 1).add(Aspect.MIND, 1));

		for(int i=0;i<BlockPersegrit.metaAmount;i++)
			registerObjectTag(block(BlockList.persegrit), meta(i), new AspectList().add(Aspect.EARTH,1).add(Aspect.DARKNESS,1));
		
		registerObjectTag(block(BlockList.energy_cluster), meta(0), new AspectList().add(Aspect.ELDRITCH,5).add(Aspect.ENERGY,5).add(Aspect.AURA,2));

		// ITEM APECTS

		registerObjectTag(item(ItemList.adventurers_diary), meta(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15), new AspectList().add(Aspect.MIND,2).add(Aspect.SOUL,1));
		registerObjectTag(item(ItemList.knowledge_note), meta(0), new AspectList().add(Aspect.MIND,3));
		
		registerObjectTag(item(ItemList.altar_nexus), meta(0), new AspectList().add(Aspect.CRYSTAL,8).add(Aspect.MIND,2).add(Aspect.SENSES,4));
		registerObjectTag(item(ItemList.essence), meta(EssenceType.DRAGON.getItemDamage()), new AspectList().add(Aspect.BEAST,3).add(Aspect.ELDRITCH,10).add(Aspect.ENERGY,10).add(Aspect.ENTROPY,6).add(Aspect.EXCHANGE,2));
		registerObjectTag(item(ItemList.essence), meta(EssenceType.FIERY.getItemDamage()), new AspectList().add(Aspect.ELDRITCH,10).add(Aspect.ENERGY,6).add(Aspect.FIRE,10).add(Aspect.TAINT,4));
		registerObjectTag(item(ItemList.essence), meta(EssenceType.SPECTRAL.getItemDamage()), new AspectList());
		
		registerObjectTag(item(ItemList.end_powder), meta(0), new AspectList().add(Aspect.ORDER,1).add(Aspect.SENSES,2));
		registerObjectTag(item(ItemList.stardust), meta(0), new AspectList().add(Aspect.ENERGY,3).add(Aspect.EXCHANGE,3).add(Aspect.EARTH,1));
		registerObjectTag(item(ItemList.igneous_rock), meta(0), new AspectList().add(Aspect.ENERGY,2).add(Aspect.FIRE,10).add(Aspect.EARTH,1));
		registerObjectTag(item(ItemList.instability_orb), meta(0), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.ENTROPY,5).add(Aspect.EXCHANGE,3));
		registerObjectTag(item(ItemList.potion_of_instability), meta(0), new AspectList().add(Aspect.ELDRITCH,3).add(Aspect.MAGIC,3).add(Aspect.WATER,1));
		registerObjectTag(item(ItemList.potion_of_instability), meta(1), new AspectList().add(Aspect.ELDRITCH,3).add(Aspect.ENTROPY,1).add(Aspect.MAGIC,3).add(Aspect.WATER,1));
		registerObjectTag(item(ItemList.potion_of_purity), meta(0), new AspectList().add(Aspect.ELDRITCH,3).add(Aspect.MAGIC,3).add(Aspect.WATER,1));
		registerObjectTag(item(ItemList.potion_of_purity), meta(1), new AspectList().add(Aspect.ELDRITCH,3).add(Aspect.ENTROPY,1).add(Aspect.MAGIC,3).add(Aspect.WATER,1));
		
		registerObjectTag(new ItemStack(ItemList.transference_gem, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.CRYSTAL,4).add(Aspect.DARKNESS,1).add(Aspect.ELDRITCH,2).add(Aspect.TRAVEL,6));
		registerObjectTag(new ItemStack(ItemList.temple_caller, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.ELDRITCH,6).add(Aspect.MAGIC,5).add(Aspect.MIND,2).add(Aspect.LIGHT,2).add(Aspect.TRAVEL,4));
		
		registerObjectTag(item(ItemList.silverfish_blood), meta(0), new AspectList().add(Aspect.BEAST,1).add(Aspect.SOUL,1));
		registerObjectTag(item(ItemList.dry_splinter), meta(0), new AspectList().add(Aspect.TREE,1));
		registerObjectTag(item(ItemList.infestation_remedy), meta(0), new AspectList().add(Aspect.LIFE,2).add(Aspect.MAGIC,1).add(Aspect.WATER,1));

		registerObjectTag(item(ItemList.ghost_amulet), meta(0), new AspectList().add(Aspect.AURA,3).add(Aspect.TAINT,2).add(Aspect.CRYSTAL,1).add(Aspect.ENERGY,2));
		registerObjectTag(item(ItemList.ghost_amulet), meta(1), new AspectList().add(Aspect.AURA,5).add(Aspect.CRYSTAL,1).add(Aspect.ENERGY,2));
		registerObjectTag(item(ItemList.ectoplasm), meta(0), new AspectList().add(Aspect.ELDRITCH,5).add(Aspect.ENERGY,5).add(Aspect.MAGIC,5).add(Aspect.SENSES,2));
		registerObjectTag(item(ItemList.corporeal_mirage_orb), meta(0), new AspectList().add(Aspect.AIR,4).add(Aspect.ENTROPY,3).add(Aspect.MIND,2).add(Aspect.SOUL,5).add(Aspect.TRAVEL,1));
		
		registerObjectTag(item(ItemList.bucket_ender_goo), meta(0), new AspectList().add(Aspect.METAL,8).add(Aspect.POISON,4).add(Aspect.TAINT,2).add(Aspect.VOID,1));

		registerObjectTag(item(ItemList.endium_ingot), meta(0), new AspectList().add(Aspect.METAL,3).add(Aspect.ELDRITCH,2));
		
		registerObjectTag(item(ItemList.arcane_shard), meta(0), new AspectList().add(Aspect.ELDRITCH,1).add(Aspect.MAGIC,1));
		registerObjectTag(item(ItemList.obsidian_fragment), meta(0), new AspectList().add(Aspect.EARTH,1));
		registerObjectTag(item(ItemList.auricion), meta(0), new AspectList().add(Aspect.METAL,1).add(Aspect.GREED,1).add(Aspect.CRAFT,1));
		registerObjectTag(item(ItemList.infernium), meta(0), new AspectList().add(Aspect.FIRE,3).add(Aspect.ENERGY,1).add(Aspect.MAGIC,2));

		registerObjectTag(item(ItemList.curse_amulet), meta(0), new AspectList().add(Aspect.ELDRITCH,4).add(Aspect.SENSES,3).add(Aspect.MAGIC,3).add(Aspect.TAINT, 1));
		
		for(int i=0;i<ItemMusicDisk.getRecordCount();i++)
			registerObjectTag(item(ItemList.music_disk), meta(i), new AspectList().add(Aspect.SENSES,4).add(Aspect.AIR,4).add(Aspect.ELDRITCH,4).add(Aspect.GREED, 4));

		registerObjectTag(item(ItemList.enhanced_brewing_stand), meta(0), new AspectList().add(Aspect.WATER,2).add(Aspect.CRAFT,2).add(Aspect.FIRE,3).add(Aspect.MAGIC,4).add(Aspect.EARTH,2).add(Aspect.ELDRITCH, 3));

		registerObjectTag(item(ItemList.rune), meta(0), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,2).add(Aspect.ENERGY,2));
		registerObjectTag(item(ItemList.rune), meta(1), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,2).add(Aspect.MOTION,2));
		registerObjectTag(item(ItemList.rune), meta(2), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,2).add(Aspect.LIFE,2));
		registerObjectTag(item(ItemList.rune), meta(3), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,2).add(Aspect.ARMOR,2));
		registerObjectTag(item(ItemList.rune), meta(4), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,4));
		registerObjectTag(item(ItemList.rune), meta(5), new AspectList().add(Aspect.EARTH,1).add(Aspect.MIND,1).add(Aspect.MAGIC,2).add(Aspect.VOID,2));
		
		registerObjectTag(item(ItemList.enderman_head), meta(0,1,2,3,4,5), new AspectList().add(Aspect.DEATH,4).add(Aspect.ELDRITCH,6).add(Aspect.SOUL,4));

		//TODO: Add aspects to fixed value bottle o enchanting
		//TODO: Add aspects to enhanced TNT
		//TODO: Add aspects to enhanced ender pearl
		
		// ENTITY ASPECTS
		
		registerEntityTag("HardcoreEnderExpansion.EnderEye", new AspectList().add(Aspect.AIR,2).add(Aspect.DARKNESS,1).add(Aspect.ELDRITCH,4).add(Aspect.MAGIC,2).add(Aspect.MOTION,1).add(Aspect.SENSES,1));
		registerEntityTag("HardcoreEnderExpansion.FireFiend", new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,8).add(Aspect.FLIGHT,4));
		registerEntityTag("HardcoreEnderExpansion.EnderDemon", new AspectList().add(Aspect.AIR,1).add(Aspect.ELDRITCH,4).add(Aspect.FLIGHT,4).add(Aspect.WEATHER,4));
		
		registerEntityTag("HardcoreEnderExpansion.AngryEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.DARKNESS,1).add(Aspect.DEATH,1).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1));
		registerEntityTag("HardcoreEnderExpansion.BabyEnderman", new AspectList().add(Aspect.AIR,1).add(Aspect.ELDRITCH,3).add(Aspect.EXCHANGE,1));
		registerEntityTag("HardcoreEnderExpansion.ParalyzedEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.DARKNESS,1).add(Aspect.ORDER,2));
		registerEntityTag("HardcoreEnderExpansion.EnderGuardian", new AspectList().add(Aspect.AIR,2).add(Aspect.DEATH,2).add(Aspect.ELDRITCH,6));
		registerEntityTag("HardcoreEnderExpansion.VampireBat", new AspectList().add(Aspect.AIR,1).add(Aspect.BEAST,1).add(Aspect.ELDRITCH,1).add(Aspect.FLIGHT,1).add(Aspect.SOUL,1));
		registerEntityTag("HardcoreEnderExpansion.InfestedBat", new AspectList().add(Aspect.AIR,1).add(Aspect.BEAST,1).add(Aspect.FLIGHT,1).add(Aspect.POISON,1));
		registerEntityTag("HardcoreEnderExpansion.FireGolem", new AspectList().add(Aspect.DARKNESS,1).add(Aspect.ELDRITCH,1).add(Aspect.FIRE,3).add(Aspect.MAGIC,1));
		registerEntityTag("HardcoreEnderExpansion.ScorchingLens", new AspectList().add(Aspect.DARKNESS,1).add(Aspect.FIRE,3).add(Aspect.SENSES,1));
		registerEntityTag("HardcoreEnderExpansion.CorporealMirage", new AspectList().add(Aspect.AIR,4).add(Aspect.ENTROPY,3).add(Aspect.MIND,2).add(Aspect.SOUL,5).add(Aspect.TRAVEL,1));

		//TODO: Add aspects to HardcoreEnderExpansion.Louse

		registerEntityTag("HardcoreEnderExpansion.HauntedMiner", new AspectList().add(Aspect.MINE,4).add(Aspect.FIRE,4).add(Aspect.WEAPON,2).add(Aspect.SOUL,5).add(Aspect.EARTH,1));

		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,2).add(Aspect.EXCHANGE,1),new EntityTagsNBT("homelandRole",(byte)HomelandRole.BUSINESSMAN.ordinal())); //Good?
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,2).add(Aspect.GREED,1),new EntityTagsNBT("homelandRole",(byte)HomelandRole.COLLECTOR.ordinal())); //Good?
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,2).add(Aspect.ARMOR,1),new EntityTagsNBT("homelandRole",(byte)HomelandRole.GUARD.ordinal()));
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,3),new EntityTagsNBT("homelandRole",(byte)HomelandRole.INTELLIGENCE.ordinal()));
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,2).add(Aspect.ORDER,1),new EntityTagsNBT("homelandRole",(byte)HomelandRole.ISLAND_LEADERS.ordinal()));
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,2).add(Aspect.MIND,2),new EntityTagsNBT("homelandRole",(byte)HomelandRole.OVERWORLD_EXPLORER.ordinal()));
		registerEntityTag("HardcoreEnderExpansion.HomelandEnderman", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,1).add(Aspect.MIND,2),new EntityTagsNBT("homelandRole",(byte)HomelandRole.WORKER.ordinal())); //Good?

		registerEntityTag("HardcoreEnderExpansion.Endermage", new AspectList().add(Aspect.AIR,2).add(Aspect.ELDRITCH,4).add(Aspect.TRAVEL,2).add(Aspect.MAGIC,4));
	}
	
	private static ItemStack block(Block block){
		return new ItemStack(block,1,0);
	}
	
	private static ItemStack item(Item item){
		return new ItemStack(item,1,0);
	}
	
	private static int[] meta(int...meta){
		return meta;
	}
}
