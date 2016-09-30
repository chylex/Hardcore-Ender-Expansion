package chylex.hee.game.integration.handlers;
import static thaumcraft.api.ThaumcraftApi.*;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.game.integration.IIntegrationHandler;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.essence.EssenceType;

public class ThaumcraftIntegration implements IIntegrationHandler{
	@Override
	public void integrate() throws Throwable{
		
		// BLOCK ASPECTS
		
		registerBlock(BlockList.obsidian_falling, 0).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_stairs, range(0, 7)).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special, 0, 5).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special, 1, 6).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special, 2, 3, 4).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special_glow, 0, 5).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.LIGHT, 5).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special_glow, 1, 6).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.LIGHT, 5).add(Aspect.EARTH, 2);
		registerBlock(BlockList.obsidian_special_glow, 2, 3, 4).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 2).add(Aspect.LIGHT, 5).add(Aspect.EARTH, 2);
		
		registerBlock(BlockList.essence_altar, EssenceType.INVALID.id).add(Aspect.ELDRITCH, 4).add(Aspect.MIND, 5).add(Aspect.EARTH, 6);
		registerBlock(BlockList.essence_altar, EssenceType.DRAGON.id).add(Aspect.ELDRITCH, 4).add(Aspect.ENERGY, 10).add(Aspect.EXCHANGE, 2).add(Aspect.MIND, 5).add(Aspect.EARTH, 6);
		registerBlock(BlockList.essence_altar, EssenceType.FIERY.id).add(Aspect.ELDRITCH, 4).add(Aspect.ENERGY, 6).add(Aspect.FIRE, 10).add(Aspect.MIND, 5).add(Aspect.EARTH, 6);
		
		registerBlock(BlockList.enhanced_brewing_stand, range(0, 3)).add(Aspect.CRAFT, 3).add(Aspect.ENTROPY, 3).add(Aspect.FIRE, 4).add(Aspect.MAGIC, 4).add(Aspect.EARTH, 3).add(Aspect.WATER, 2);
		
		registerBlock(BlockList.end_powder_ore, 0).add(Aspect.MAGIC, 1).add(Aspect.EARTH, 1);
		registerBlock(BlockList.stardust_ore, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).add(Aspect.ENERGY, 3).add(Aspect.EXCHANGE, 3).add(Aspect.EARTH, 1);
		registerBlock(BlockList.igneous_rock_ore, 0).add(Aspect.FIRE, 3).add(Aspect.EARTH, 2);
		registerBlock(BlockList.instability_orb_ore, 0).add(Aspect.ELDRITCH, 1).add(Aspect.EXCHANGE, 1).add(Aspect.EARTH, 1);
		
		registerBlock(BlockList.ender_goo, range(0, 15)).add(Aspect.POISON, 4).add(Aspect.TAINT, 2);
		
		registerBlock(BlockList.end_terrain, BlockEndstoneTerrain.metaInfested).add(Aspect.DARKNESS, 1).add(Aspect.EARTH, 1).add(Aspect.POISON, 1);
		registerBlock(BlockList.end_terrain, BlockEndstoneTerrain.metaBurned).add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 1).add(Aspect.EARTH, 1);
		registerBlock(BlockList.end_terrain, BlockEndstoneTerrain.metaEnchanted).add(Aspect.DARKNESS, 1).add(Aspect.EARTH, 1).add(Aspect.MAGIC, 1);
		
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataThornBush).add(Aspect.DEATH, 1).add(Aspect.PLANT, 1).add(Aspect.POISON, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataInfestedFern).add(Aspect.PLANT, 1).add(Aspect.POISON, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataInfestedGrass).add(Aspect.PLANT, 1).add(Aspect.POISON, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataInfestedTallgrass).add(Aspect.PLANT, 1).add(Aspect.POISON, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataLilyFire).add(Aspect.ELDRITCH, 1).add(Aspect.LIFE, 1).add(Aspect.PLANT, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataVioletMossModerate).add(Aspect.ELDRITCH, 1).add(Aspect.LIFE, 1).add(Aspect.PLANT, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataVioletMossShort).add(Aspect.ELDRITCH, 1).add(Aspect.LIFE, 1).add(Aspect.PLANT, 1);
		registerBlock(BlockList.crossed_decoration, BlockCrossedDecoration.dataVioletMossTall).add(Aspect.ELDRITCH, 1).add(Aspect.LIFE, 1).add(Aspect.PLANT, 1);
		
		registerBlock(BlockList.spooky_log, 0).add(Aspect.TREE, 3);
		registerBlock(BlockList.spooky_log, range(1, 4)).add(Aspect.DEATH, 2).add(Aspect.SOUL, 1).add(Aspect.TREE, 3);
		registerBlock(BlockList.spooky_leaves, 0).add(Aspect.PLANT, 1);
		
		registerBlock(BlockList.death_flower, range(0, 14)).add(Aspect.DEATH, 1).add(Aspect.ENTROPY, 1).add(Aspect.PLANT, 1);
		registerBlock(BlockList.death_flower, 15).add(Aspect.DEATH, 6).add(Aspect.ENTROPY, 3).add(Aspect.SENSES, 1).add(Aspect.PLANT, 1);
		
		registerBlock(BlockList.custom_spawner, 0).add(Aspect.BEAST, 4).add(Aspect.DEATH, 1).add(Aspect.MAGIC, 4).add(Aspect.TRAVEL, 4);
		registerBlock(BlockList.custom_spawner, 1).add(Aspect.BEAST, 2).add(Aspect.DEATH, 2).add(Aspect.EARTH, 1).add(Aspect.MAGIC, 4).add(Aspect.TRAVEL, 4);
		
		registerBlock(BlockList.endium_ore, 0).add(Aspect.ELDRITCH, 1).add(Aspect.METAL, 2).add(Aspect.EARTH, 1);
		registerBlock(BlockList.endium_block, 0).add(Aspect.ELDRITCH, 13).add(Aspect.METAL, 20);

		registerBlock(BlockList.sphalerite, 0).add(Aspect.EARTH, 1).add(Aspect.VOID, 1);
		registerBlock(BlockList.sphalerite, 1).add(Aspect.EARTH, 1).add(Aspect.EXCHANGE, 3).add(Aspect.ENERGY, 3);

		registerBlock(BlockList.cinder, 0).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1);

		registerBlock(BlockList.laboratory_obsidian, 0).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1).add(Aspect.MIND, 1);
		registerBlock(BlockList.laboratory_floor, 0).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1).add(Aspect.MIND, 1);
		registerBlock(BlockList.laboratory_stairs, range(0, 15)).add(Aspect.EARTH, 1).add(Aspect.FIRE, 1).add(Aspect.MIND, 1);
		registerBlock(BlockList.laboratory_glass, 0).add(Aspect.CRYSTAL, 1).add(Aspect.FIRE, 1).add(Aspect.MIND, 1);
		
		for(int i = 0; i < BlockRavagedBrick.metaAmount; i++)registerBlock(BlockList.ravaged_brick, i).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1);
		registerBlock(BlockList.ravaged_brick_smooth, 0).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1);
		registerBlock(BlockList.ravaged_brick_glow, 0).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.LIGHT, 5);

		registerBlock(BlockList.dungeon_puzzle, range(0, 15)).add(Aspect.EARTH, 1).add(Aspect.ELDRITCH, 1).add(Aspect.MECHANISM, 1).add(Aspect.MIND, 1);
		registerBlock(BlockList.persegrit, range(0, 15)).add(Aspect.EARTH, 1).add(Aspect.DARKNESS, 1);
		registerBlock(BlockList.energy_cluster, 0).add(Aspect.ELDRITCH, 5).add(Aspect.ENERGY, 5).add(Aspect.AURA, 2);
		
		registerBlock(BlockList.enderman_head, range(0, 5)).add(Aspect.DEATH, 4).add(Aspect.ELDRITCH, 6).add(Aspect.SOUL, 4);

		// ITEM APECTS
		
		registerItem(ItemList.knowledge_note, 0).add(Aspect.MIND, 3);
		
		registerItem(ItemList.alteration_nexus, 0).add(Aspect.CRYSTAL, 8).add(Aspect.MIND, 2).add(Aspect.SENSES, 4); // TODO changed
		registerItem(ItemList.essence, EssenceType.DRAGON.getItemDamage()).add(Aspect.BEAST, 3).add(Aspect.ELDRITCH, 10).add(Aspect.ENERGY, 10).add(Aspect.ENTROPY, 6).add(Aspect.EXCHANGE, 2);
		registerItem(ItemList.essence, EssenceType.FIERY.getItemDamage()).add(Aspect.ELDRITCH, 10).add(Aspect.ENERGY, 6).add(Aspect.FIRE, 10).add(Aspect.TAINT, 4);
		
		registerItem(ItemList.end_powder, 0).add(Aspect.ORDER, 1).add(Aspect.SENSES, 2);
		registerItem(ItemList.stardust, 0).add(Aspect.ENERGY, 3).add(Aspect.EXCHANGE, 3).add(Aspect.EARTH, 1);
		registerItem(ItemList.igneous_rock, 0).add(Aspect.ENERGY, 2).add(Aspect.FIRE, 10).add(Aspect.EARTH, 1);
		registerItem(ItemList.instability_orb, 0).add(Aspect.ELDRITCH, 1).add(Aspect.ENTROPY, 5).add(Aspect.EXCHANGE, 3);
		registerItem(ItemList.potion_of_instability, 0).add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 3).add(Aspect.WATER, 1);
		registerItem(ItemList.potion_of_instability, 1).add(Aspect.ELDRITCH, 3).add(Aspect.ENTROPY, 1).add(Aspect.MAGIC, 3).add(Aspect.WATER, 1);
		registerItem(ItemList.potion_of_purity, 0).add(Aspect.ELDRITCH, 3).add(Aspect.MAGIC, 3).add(Aspect.WATER, 1);
		registerItem(ItemList.potion_of_purity, 1).add(Aspect.ELDRITCH, 3).add(Aspect.ENTROPY, 1).add(Aspect.MAGIC, 3).add(Aspect.WATER, 1);
		registerItem(ItemList.transference_gem, OreDictionary.WILDCARD_VALUE).add(Aspect.CRYSTAL, 4).add(Aspect.DARKNESS, 1).add(Aspect.ELDRITCH, 2).add(Aspect.TRAVEL, 6);
		
		registerItem(ItemList.silverfish_blood, 0).add(Aspect.BEAST, 1).add(Aspect.SOUL, 1);
		registerItem(ItemList.dry_splinter, 0).add(Aspect.TREE, 1);
		registerItem(ItemList.infestation_remedy, 0).add(Aspect.LIFE, 2).add(Aspect.MAGIC, 1).add(Aspect.WATER, 1);
		
		registerItem(ItemList.ghost_amulet, 0).add(Aspect.AURA, 3).add(Aspect.TAINT, 2).add(Aspect.CRYSTAL, 1).add(Aspect.ENERGY, 2);
		registerItem(ItemList.ghost_amulet, 1).add(Aspect.AURA, 5).add(Aspect.CRYSTAL, 1).add(Aspect.ENERGY, 2);
		registerItem(ItemList.ectoplasm, 0).add(Aspect.ELDRITCH, 5).add(Aspect.ENERGY, 5).add(Aspect.MAGIC, 5).add(Aspect.SENSES, 2);
		
		registerItem(ItemList.bucket_ender_goo, 0).add(Aspect.METAL, 8).add(Aspect.POISON, 4).add(Aspect.TAINT, 2).add(Aspect.VOID, 1);
		
		registerItem(ItemList.endium_ingot, 0).add(Aspect.METAL, 3).add(Aspect.ELDRITCH, 2);
		
		registerItem(ItemList.arcane_shard, 0).add(Aspect.ELDRITCH, 1).add(Aspect.MAGIC, 1);
		registerItem(ItemList.obsidian_fragment, 0).add(Aspect.EARTH, 1);
		registerItem(ItemList.auricion, 0).add(Aspect.METAL, 1).add(Aspect.GREED, 1).add(Aspect.CRAFT, 1);
		registerItem(ItemList.infernium, 0).add(Aspect.FIRE, 3).add(Aspect.ENERGY, 1).add(Aspect.MAGIC, 2);

		registerItem(ItemList.curse_amulet, 0).add(Aspect.ELDRITCH, 4).add(Aspect.SENSES, 3).add(Aspect.MAGIC, 3).add(Aspect.TAINT, 1);
		registerItem(ItemList.music_disk, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9).add(Aspect.SENSES, 4).add(Aspect.AIR, 4).add(Aspect.ELDRITCH, 4).add(Aspect.GREED, 4);
		registerItem(ItemList.enhanced_brewing_stand, 0).add(Aspect.WATER, 2).add(Aspect.CRAFT, 2).add(Aspect.FIRE, 3).add(Aspect.MAGIC, 4).add(Aspect.EARTH, 2).add(Aspect.ELDRITCH, 3);

		registerItem(ItemList.rune, 0).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 2).add(Aspect.ENERGY, 2);
		registerItem(ItemList.rune, 1).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 2).add(Aspect.MOTION, 2);
		registerItem(ItemList.rune, 2).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 2).add(Aspect.LIFE, 2);
		registerItem(ItemList.rune, 3).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 2).add(Aspect.ARMOR, 2);
		registerItem(ItemList.rune, 4).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 4);
		registerItem(ItemList.rune, 5).add(Aspect.EARTH, 1).add(Aspect.MIND, 1).add(Aspect.MAGIC, 2).add(Aspect.VOID, 2);
		
		// ENTITY ASPECTS
		
		registerMob("EnderEye").add(Aspect.AIR, 2).add(Aspect.DARKNESS, 1).add(Aspect.ELDRITCH, 4).add(Aspect.MAGIC, 2).add(Aspect.MOTION, 1).add(Aspect.SENSES, 1);
		registerMob("FireFiend").add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 8).add(Aspect.FLIGHT, 4);
		registerMob("EnderDemon").add(Aspect.AIR, 1).add(Aspect.ELDRITCH, 4).add(Aspect.FLIGHT, 4).add(Aspect.WEATHER, 4);
		
		registerMob("AngryEnderman").add(Aspect.AIR, 2).add(Aspect.DARKNESS, 1).add(Aspect.DEATH, 1).add(Aspect.ELDRITCH, 4).add(Aspect.TRAVEL, 1);
		registerMob("BabyEnderman").add(Aspect.AIR, 1).add(Aspect.ELDRITCH, 3).add(Aspect.EXCHANGE, 1);
		registerMob("ParalyzedEnderman").add(Aspect.AIR, 2).add(Aspect.DARKNESS, 1).add(Aspect.ORDER, 2);
		registerMob("EnderGuardian").add(Aspect.AIR, 2).add(Aspect.DEATH, 2).add(Aspect.ELDRITCH, 6);
		registerMob("VampireBat").add(Aspect.AIR, 1).add(Aspect.BEAST, 1).add(Aspect.ELDRITCH, 1).add(Aspect.FLIGHT, 1).add(Aspect.SOUL, 1);
		registerMob("InfestedBat").add(Aspect.AIR, 1).add(Aspect.BEAST, 1).add(Aspect.FLIGHT, 1).add(Aspect.POISON, 1);
		registerMob("FireGolem").add(Aspect.DARKNESS, 1).add(Aspect.ELDRITCH, 1).add(Aspect.FIRE, 3).add(Aspect.MAGIC, 1);
		registerMob("ScorchingLens").add(Aspect.DARKNESS, 1).add(Aspect.FIRE, 3).add(Aspect.SENSES, 1);
		registerMob("HauntedMiner").add(Aspect.MINE, 4).add(Aspect.FIRE, 4).add(Aspect.WEAPON, 2).add(Aspect.SOUL, 5).add(Aspect.EARTH, 1);
		registerMob("Endermage").add(Aspect.AIR, 2).add(Aspect.ELDRITCH, 4).add(Aspect.TRAVEL, 2).add(Aspect.MAGIC, 4);
	}
	
	private static AspectList registerBlock(Block block, int...meta){
		AspectList list = new AspectList();
		registerObjectTag(new ItemStack(block), meta, list);
		return list;
	}
	
	private static AspectList registerItem(Item item, int...meta){
		AspectList list = new AspectList();
		registerObjectTag(new ItemStack(item), meta, list);
		return list;
	}
	
	private static AspectList registerMob(String name, EntityTagsNBT...tags){
		AspectList list = new AspectList();
		registerEntityTag("HardcoreEnderExpansion."+name, list, tags);
		return list;
	}
	
	private static int[] range(int startIncl, int endIncl){
		return IntStream.rangeClosed(startIncl, endIncl).toArray();
	}
}
