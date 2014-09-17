package chylex.hee.mechanics.knowledge;
/*
public final class KnowledgeRegistrations{
	public static void initialize(){
		Stopwatch.time("KnowledgeRegistrations");
		
		
		
		
		
		INSTABILITY_POTION
		.setPosition(hdist,hdist)
		.setRenderer(new ItemStackRenderer(ItemList.potion_of_instability,2))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Unstable potion, which will cause random effects when drank."),
			new TextKnowledgeFragment(1).setLocalizedText("Splash potion will give everybody a different effect.").setUnlockRequirements(0)
		});
		
		INFESTED_FOREST_BIOME
		.setPosition(-dist*2-hdist,-dist)
		.setRenderer(new ItemStackRenderer(BlockList.end_terrain,BlockEndstoneTerrain.metaInfested).setTooltip("Infested Forest Biome"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Mostly flat biome, with long caves, decorated densely with Spooky Trees, different types of infested grasses, ferns and bushes."),
			new TextKnowledgeFragment(1).setLocalizedText("Thorny Bushes often spawn in patches of grass, they damage and randomly poison creatures on contact.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("It is populated with Infested Bats, Silverfish and occasionally Endermen spawn too.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Each island has several Silverfish Dungeons, which are often hidden in terrain, and detectable by sound.").setUnlockRequirements(1,2),
			new TextKnowledgeFragment(4).setLocalizedText("There is a chance the biome will have one or more of the rare variations, which include taller trees, larger amounts of Thorny Bushes, dungeons with more spawners and hills.").setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("Infested terrain cause Infestation to humans, which gets worse the longer they stay in the biome, and kicks off some time after leaving the island.").setUnlockRequirements(1),
			new TextKnowledgeFragment(6).setLocalizedText("Infestation might make the infected weak, slow or tired, in severe cases even blind, poisoned or cause nausea.").setUnlockRequirements(5),
			new TextKnowledgeFragment(7).setLocalizedText("The only way of easing infestation is by creating Infestation Remedy.").setUnlockRequirements(6)
		});
		
		BURNING_MOUNTAINS_BIOME
		.setPosition(-dist*2,0)
		.setRenderer(new ItemStackRenderer(BlockList.end_terrain,BlockEndstoneTerrain.metaBurned).setTooltip("Burning Mountains Biome"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Biome with huge mountains and altitude fluctuations, compensated with larger amount of ores and caves."),
			new TextKnowledgeFragment(1).setLocalizedText("Surface is decorated with Lilyfires, and lava streams, caves are populated with clusters of Igneous Rock Ore.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Endermen are very rare to spawn on the surface, Fire Golems and Scorching Lenses spawn commonly.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("One or more rare variations have a chance to spawn, those include increased amount of caves and larger patches of Lilyfires.").setUnlockRequirements(1,2),
			new TextKnowledgeFragment(4).setLocalizedText("Every island has one Dungeon Puzzle, usually close to the surface, and with a cave leading to it.").setUnlockRequirements(1,2),
			new TextKnowledgeFragment(5).setLocalizedText("The Dungeon Puzzle contains puzzle blocks, which can be manipulated with by throwing Igneous Rock at them.").setUnlockRequirements(4),
			new TextKnowledgeFragment(6).setLocalizedText("After solving the Dungeon Puzzle, a Fire Fiend miniboss is spawned.").setUnlockRequirements(5),
			new TextKnowledgeFragment(7).setLocalizedText("There is a ??% chance the island will have one Resource Pit, which is a round hole with lava on the bottom, and high rates of ores.").setReplacedBy(8).setUnlockRequirements(1,2),
			new TextKnowledgeFragment(8).setLocalizedText("There is a 40% chance the island will have one Resource Pit, which is a round hole with lava on the bottom, and high rates of ores.").setReplacementFor(7).setUnlockRequirements(1,2)
		});
		
		ENCHANTED_ISLAND_BIOME
		.setPosition(-dist*2-hdist,dist)
		.setRenderer(new ItemStackRenderer(BlockList.end_terrain,BlockEndstoneTerrain.metaEnchanted).setTooltip("Enchanted Island Biome"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Very flat biome with small amount of caves."),
			new TextKnowledgeFragment(1).setLocalizedText("Tall broken pillars of Falling Obsidian, and Ender Goo lakes populate the surface.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Endermen, Baby Endermen and Ender Guardians form the population of the island.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Each island has a chance of having one or more rare variations, which include much taller pillars and larger lakes.").setUnlockRequirements(1,2),
			new TextKnowledgeFragment(4).setLocalizedText("Inside the terrain can be found Block Stashes, which are rectangular rooms with piles of blocks stolen by Endermen.").setUnlockRequirements(1,2)
		});
		
		
		
		
		
		
		
		
		
		DECOMPOSITION_TABLE
		.setPosition(-hdist,-dist*2)
		.setRenderer(new ItemStackRenderer(BlockList.decomposition_table))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Used to decraft items using Stardust."),
			new CraftingKnowledgeFragment(1).setRecipeFromRegistry(new ItemStack(BlockList.decomposition_table)),
			new TextKnowledgeFragment(2).setLocalizedText("Amount of Stardust and time it takes for an item to decraft depends on recipe complexity.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Between 43% and ??% items can be retrieved, damaged items yield less.").setReplacedBy(4).setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("Between 43% and 84% items can be retrieved, damaged items yield less.").setReplacementFor(3).setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("Hoppers can be used to manipulate with the Table items.").setUnlockRequirements(2)
		});
		
		ENERGY_EXTRACTION_TABLE
		.setPosition(hdist,-dist*2)
		.setRenderer(new ItemStackRenderer(BlockList.energy_extraction_table))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("It is used to extract Energy from certain items using Stardust."),
			new CraftingKnowledgeFragment(1).setRecipeFromRegistry(new ItemStack(BlockList.energy_extraction_table)),
			new TextKnowledgeFragment(2).setLocalizedText("Only blocks and items found in the End may contain Energy.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Amount of Stardust and extraction time depend on amount of Energy a block or item has.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("One Table can only hold up to 5 units of Energy").setUnlockRequirements(2),
			new TextKnowledgeFragment(5).setLocalizedText("Energy rapidly leaks into the world, which may cause Corrupted Energy to appear, fill nearby Energy Clusters or create new ones if enough Energy is stored.").setUnlockRequirements(4),
			new TextKnowledgeFragment(6).setLocalizedText("Leaking can be massively suppressed by placing Instability Orbs into the Table.").setUnlockRequirements(5)
		});
		
		TRANSFERENCE_GEM
		.setPosition(dist,dist+hdist)
		.setRenderer(new ItemStackRenderer(ItemList.transference_gem))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Teleportation gem consisting of several Gem Fragments."),
			new TextKnowledgeFragment(1).setLocalizedText("Using it on a block while sneaking will link the gem to that block.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("When linked, it can be used while standing to teleport to linked block.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("Each time the gem is used, durability goes down, but it can be restored with energy from Energy Clusters.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("When the durability is below ??%, it will have a chance to cause bad effects, such as hunger or nausea, the gem may also teleport to a different place, or even rarely start a very short storm, etc.").setReplacedBy(5).setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("When the durability is below 44%, it will have a chance to cause bad effects, such as hunger or nausea, the gem may also teleport to a different place, or even rarely start a very short storm, etc.").setReplacementFor(4).setUnlockRequirements(3),
			new TextKnowledgeFragment(6).setLocalizedText("Gem with depleted durability will not break, it will refuse to work instead.").setUnlockRequirements(3),
			new TextKnowledgeFragment(7).setLocalizedText("End Powder can enhance the gem with new effects.").setUnlockRequirements(2)
		});
		
		ENDERMAN_RELIC
		.setPosition(dist,-dist-hdist)
		.setRenderer(new ItemStackRenderer(ItemList.enderman_relic))
		.setFragments(new KnowledgeFragment[]{
			/*new TextKnowledgeFragment(0).setLocalizedText("There are 2 shattered relics."),
			new TextKnowledgeFragment(1).setLocalizedText("First one can be sometimes found in the Enchanted Island biome, and picking it up will cause Endermanpocalypse.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("The Endermanpocalypse will cause strange weather in the Overworld, followed by a long night with Endermen and Ender Guardians trying to get the relic back.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("At the end of the Endermanpocalypse, Ender Demon comes down from the sky above Overworld's spawn point.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("Second part of the shattered relic is dropped by the Ender Demon.").setUnlockRequirements(3),
			new CraftingKnowledgeFragment(5).setRecipeFromRegistry(new ItemStack(ItemList.enderman_relic)).setUnlockRequirements(4),
			new TextKnowledgeFragment(6).setLocalizedText("Repaired relic will drain all powers from Endermen, which attack the owner, turn them into Paralyzed Endermen, and damage the relic in the process.").setUnlockRequirements(5),
			new TextKnowledgeFragment(7).setLocalizedText("The relic can be repaired using energy from Energy Clusters.").setUnlockRequirements(6)*/
		/*});
		
		ENERGY_CLUSTER
		.setPosition(0,0)
		.setRenderer(new ItemStackRenderer(BlockList.energy_cluster))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Cluster of accumulated Energy, which is used as a resource for several items."),
			new TextKnowledgeFragment(1).setLocalizedText("Breaking it will release all Energy into the world, causing an explosion and a cloud of Corrupted Energy, which is an extremely deadly, expanding substance.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Healthiness of a cluster affects the ability to regenerate Energy.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Tired clusters will regenerate slowly, Healthy clusters very quickly and Weakened clusters will regenerate extremely slowly and only partially.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("Using End Powder will cure Weakened and Tired clusters, and improve regeneration speed and limit of Healthy clusters.").setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("New clusters can be created using Energy Extraction Table.").setUnlockRequirements(0),
			new TextKnowledgeFragment(6).setLocalizedText("Spectral Wand can move the clusters, one at a time.").setUnlockRequirements(0)
		});
		
		INSTABILITY_ORB
		.setPosition(dist+hdist,dist+hdist)
		.setRenderer(new ItemStackRenderer(ItemList.instability_orb))
		.setFragments(new KnowledgeFragment[]{
			new CraftingKnowledgeFragment(3).setRecipeFromRegistry(new ItemStack(ItemList.corporeal_mirage_orb)),
			new TextKnowledgeFragment(4).setLocalizedText("Inside an Energy Extraction Table, it stabilizes the stored Energy and prevents it from leaking into the world.")
		});
		
		FIRE_FIEND
		.setPosition(0,-dist+hdist)
		.setRenderer(new EntityRenderer(EntityMiniBossFireFiend.class,0.75F).setTooltip("Fire Fiend"))
		.setBackgroundTextureIndex(2)
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Large creature similar to the Fire Golem, but with ability to fly."),
			new TextKnowledgeFragment(1).setLocalizedText("The Fire Fiend has 100 hearts, and causes fire damage to any creature that gets close to it.").setUnlockRequirements(1),
			new TextKnowledgeFragment(2).setLocalizedText("It spawns inside the Puzzle Dungeon, digs his way up to the surface, and starts rapidly casting fireballs into the dungeon, until the player gets to the surface too.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("During the primary attack, it will start casting fireballs around the player, and then send them at the player.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("After primary attack ends, the Fire Fiend has 40% chance to do secondary attack, during which it will charge at the target and use the passive ability of dealing fire damage around.").setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("Drops ??? Fiery Essence.").setReplacedBy(6).setUnlockRequirements(4),
			new TextKnowledgeFragment(6).setLocalizedText("Drops 120 Fiery Essence.").setReplacementFor(5).setUnlockRequirements(5)
		});

		ENDER_DEMON
		.setPosition(dist*3,-dist+hdist)
		.setRenderer(new EntityRenderer(EntityBossEnderDemon.class,0.15F).setTooltip("Ender Demon"))
		.setBackgroundTextureIndex(2)
		.setFragments(new KnowledgeFragment[]{
			/*new TextKnowledgeFragment(0).setLocalizedText("Giant enderman-like creature with 8 flailing arms and no legs."),
			new TextKnowledgeFragment(1).setLocalizedText("It has 200 hearts, but rapidly regenerates, which makes it nearly impossible to kill with regular damage.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("The demon alternates two attacks - it can spawn Angry Endermen accompanied by purple lightning, and drop several Falling Obsidian blocks on the player.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("When enough damage has been dealt, it will quickly strike purple lightning, which causes massive amount of damage to the demon if it hits ?????.").setReplacedBy(4).setUnlockRequirements(1,2),
			new TextKnowledgeFragment(4).setLocalizedText("When enough damage has been dealt, it will quickly strike purple lightning, which causes massive amount of damage to the demon if it hits water.").setReplacementFor(3).setUnlockRequirements(3),
			new TextKnowledgeFragment(5).setLocalizedText("Death is followed by lightning strikes, and the demon drops second piece of Shattered Enderman Relic.").setUnlockRequirements(4)*/
		/*});
		
		PARALYZED_ENDERMAN
		.setPosition(dist,dist*2)
		.setBackgroundTextureIndex(1)
		.setRenderer(new EntityRenderer(EntityMobParalyzedEnderman.class))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Enderman from whom all power was drained."),
			new TextKnowledgeFragment(1).setLocalizedText("It drops less Ender Pearls, and has limited teleportation capabilities.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Occasionally, it will attack other creatures nearby.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("After a certain time, paralyzation ends and it either turns into regular Enderman, or die.").setUnlockRequirements(0)
		});
		
		SILVERFISH_BLOOD
		.setPosition(-dist*3,-dist)
		.setRenderer(new ItemStackRenderer(ItemList.silverfish_blood))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("There is a 1 in ?? chance a Silverfish will drop Silverfish Blood.").setReplacedBy(1),
			new TextKnowledgeFragment(1).setLocalizedText("There is a 1 in 56 chance a Silverfish will drop Silverfish Blood.").setReplacementFor(0).setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("However, if a Golden Sword is used to kill the Silverfish, chance of getting Silverfish Blood is 1 in 14.").setUnlockRequirements(1)
		});
		
		INFESTATION_REMEDY
		.setPosition(-dist*2+hdist,0)
		.setRenderer(new ItemStackRenderer(ItemList.infestation_remedy))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Magical potion, that can partially suppress effect of infestation."),
			new TextKnowledgeFragment(1).setLocalizedText("When drank, it will erase 2.5 to 3 minutes of infestation effects.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("It must be brewed in an Enhanced Brewing Stand using Silverfish Blood.").setUnlockRequirements(1)
		});
		
		ECTOPLASM
		.setPosition(dist*2,0)
		.setRenderer(new ItemStackRenderer(ItemList.ectoplasm))
		.setFragments(new KnowledgeFragment[]{
			new CraftingKnowledgeFragment(2).setRecipeFromRegistry(new ItemStack(ItemList.corporeal_mirage_orb)).setUnlockRequirements(1),
			new CraftingKnowledgeFragment(3).setRecipeFromRegistry(new ItemStack(BlockList.soul_charm)).setUnlockRequirements(1)
		});
		
		CORPOREAL_MIRAGE_ORB
		.setPosition(dist*3,-dist)
		.setRenderer(new ItemStackRenderer(ItemList.corporeal_mirage_orb))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Throwable orb, which creates a mirage of the owner."),
			new TextKnowledgeFragment(1).setLocalizedText("The mirage attracts creatures, which intend attack the owner, as long as the creatures sees the mirage.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("As the time goes, the mirage will decay, and eventually die.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Mobs can attack the mirage, and cause damage to it up to ? times.").setReplacedBy(4).setUnlockRequirements(1),
			new TextKnowledgeFragment(4).setLocalizedText("Mobs can attack the mirage, and cause damage to it up to 4 times.").setReplacementFor(3).setUnlockRequirements(1),
			new TextKnowledgeFragment(5).setLocalizedText("When the mirage dies, it will drop Endoplasm.").setUnlockRequirements(0)
		});
		
		SOUL_CHARM
		.setPosition(dist*3,dist)
		.setRenderer(new ItemStackRenderer(BlockList.soul_charm))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Ethereal creature, that attracts all nearby souls."),
			new TextKnowledgeFragment(1).setLocalizedText("It can be placed on the ground, and then absorbed back into the receptacle."),
			new TextKnowledgeFragment(2).setLocalizedText("End Powder can enhance the charm with new effects.").setUnlockRequirements(0,1)
		});
		
		SPECTRAL_WAND
		.setPosition(dist*3,0)
		.setRenderer(new ItemStackRenderer(ItemList.spectral_wand))
		.setFragments(new KnowledgeFragment[]{
			new CraftingKnowledgeFragment(0).setRecipeFromRegistry(new ItemStack(ItemList.spectral_wand)),
			new TextKnowledgeFragment(1).setLocalizedText("Wand that picks up Energy Clusters."),
			new TextKnowledgeFragment(2).setLocalizedText("Releasing a captured Energy Cluster will weaken it and halve its energy.").setUnlockRequirements(1)
		});

		Stopwatch.finish("KnowledgeRegistrations");
	}
	
	private KnowledgeRegistrations(){}
}*/
