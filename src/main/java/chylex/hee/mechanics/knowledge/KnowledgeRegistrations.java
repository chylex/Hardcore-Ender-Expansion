package chylex.hee.mechanics.knowledge;
/*
public final class KnowledgeRegistrations{
	public static void initialize(){
		Stopwatch.time("KnowledgeRegistrations");
		
		FIERY_ESSENCE
		.setPosition(0,dist-hdist)
		.setRenderer(new ItemStackRenderer(ItemList.essence,EssenceType.FIERY.getItemDamage()))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("It is dropped by various mobs in the Burning Mountains biome.")
		});
		
		FIERY_ESSENCE_ALTAR
		.setPosition(0,-hdist)
		.setRenderer(new ItemStackRenderer(BlockList.essence_altar,EssenceType.FIERY.id))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("It turns Fiery Essence into heat, and distributes it into Furnaces, Brewing Stands etc."),
			new TextKnowledgeFragment(1).setLocalizedText("Amount of Fiery Essence decides the speed of heat distribution, ??? of the essence will have highest speed.").setReplacedBy(2).setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Amount of Fiery Essence decides the speed of heat distribution, 512 of the essence will have highest speed.").setReplacementFor(1).setUnlockRequirements(1),
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
		
		INSTABILITY_POTION
		.setPosition(hdist,hdist)
		.setRenderer(new ItemStackRenderer(ItemList.potion_of_instability,2))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Unstable potion, which will cause random effects when drank."),
			new TextKnowledgeFragment(1).setLocalizedText("Splash potion will give everybody a different effect.").setUnlockRequirements(0)
		});
		
		BIOME_COMPASS
		.setPosition(-dist-hdist,0)
		.setRenderer(new ItemStackRenderer(ItemList.biome_compass))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Special compass found in Dungeon Towers."),
			new TextKnowledgeFragment(1).setLocalizedText("It points to nearest Biome Island, holding it will also show markers of islands of chosen biome, which can be changed by right-clicking.").setUnlockRequirements(0)
		});
		
		SPATIAL_DASH_GEM
		.setPosition(dist+hdist,hdist)
		.setRenderer(new ItemStackRenderer(ItemList.spatial_dash_gem))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Teleportation gem dropped by the Ender Eye."),
			new TextKnowledgeFragment(1).setLocalizedText("Using it will create a magical beam, which teleports the owner to the block or mob it hits.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Unlike Ender Pearls, the gem's teleportation tries to always find ground nearby to teleport the player to.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("The beam's range is ?? blocks.").setReplacedBy(4).setUnlockRequirements(1),
			new TextKnowledgeFragment(4).setLocalizedText("The beam's range is 75 blocks.").setReplacementFor(3).setUnlockRequirements(3)
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
		
		ENDER_DRAGON
		.setPosition(0,-dist*3+hdist)
		.setRenderer(new EntityRenderer(EntityDragon.class,0.95F).setRotation(-127F).setTooltip("Ender Dragon"))
		.setBackgroundTextureIndex(3)
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Giant dragon boss, which protects the End."),
			new TextKnowledgeFragment(1).setLocalizedText("It has 125 hearts, and regenerates if close to an Ender Crystal.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("The dragon can churn out fireballs and freezeballs, destroys blocks it flies through and causes massive knockback.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("Players close to the head of the dragon are bit, with a chance to become poisoned and confused.").setUnlockRequirements(0),
			new TextKnowledgeFragment(4).setLocalizedText("After enough Ender Crystals are broken, the dragon becomes angry.").setUnlockRequirements(1),
			new TextKnowledgeFragment(5).setLocalizedText("When angry, fireballs and biting attacks are more dangerous, and the dragon also does special attacks (on medium or hard difficulty immediately, on easy difficulty when below 60% health).").setUnlockRequirements(2,3,4),
			new TextKnowledgeFragment(6).setLocalizedText("Spikes made of Falling Obsidian are also scattered, when the dragon flies through.").setUnlockRequirements(5),
			new TextKnowledgeFragment(7).setLocalizedText("There are 7 special attacks...").setUnlockRequirements(5),
			new TextKnowledgeFragment(8).setLocalizedText("Divebomb makes the dragon fly high up, and swoop down through the island.").setUnlockRequirements(7),
			new TextKnowledgeFragment(9).setLocalizedText("Stay'n'fire starts with the dragon flying above a player, and then it rapidly churns out fireballs.").setUnlockRequirements(7),
			new TextKnowledgeFragment(10).setLocalizedText("Bitemadness speeds up biting attack, and makes the dragon constantly use it on a player.").setUnlockRequirements(7),
			new TextKnowledgeFragment(11).setLocalizedText("Punch quadruples speed of the dragon, and then targets a player.").setUnlockRequirements(7),
			new TextKnowledgeFragment(12).setLocalizedText("Freeze is a short attack, where the dragon churns out freezeballs and start another special attack immediately.").setUnlockRequirements(7),
			new TextKnowledgeFragment(13).setLocalizedText("Summon makes the dragon fly high up, and spawn Angry Endermen accompanied by lightning.").setUnlockRequirements(7),
			new TextKnowledgeFragment(14).setLocalizedText("Bloodlust slows down and protects the dragon, whilst it turns Endermen into Vampire Bats.").setUnlockRequirements(7),
			new TextKnowledgeFragment(15).setLocalizedText("When killed, the dragon floats into the sky, dropping experience and Dragon Essence.").setUnlockRequirements(1,6),
			new TextKnowledgeFragment(16).setLocalizedText("End Portal is formed below the dragon, with Dragon Egg on top.").setUnlockRequirements(15),
			new TextKnowledgeFragment(17).setLocalizedText("The egg can be picked up by sneaking, and hitting it with a sword.").setUnlockRequirements(17)
		});
		
		ENDER_EYE
		.setPosition(-dist*3,-dist+hdist)
		.setRenderer(new EntityRenderer(EntityMiniBossEnderEye.class).setYOffset(6F).setTooltip("Ender Eye"))
		.setBackgroundTextureIndex(2)
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Small, but very powerful mini-boss made of Obsidian, with ??? hearts.").setReplacedBy(1),
			new TextKnowledgeFragment(1).setLocalizedText("Small, but very powerful mini-boss made of Obsidian, with 125 hearts.").setReplacementFor(0).setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Hitting it will wake it up, if there is nothing to attack, the Ender Eye will fall asleep and regenerate.").setUnlockRequirements(0),
			new TextKnowledgeFragment(3).setLocalizedText("It is very tough, so at least Iron Sword has to be used to deal damage.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("The eye has 3 special attacks apart from melee, it can break blocks and cause massive knockback to everything around, it can cause nausea and blindness, or spawn purple beams which deal massive fire damage.").setUnlockRequirements(2),
			new TextKnowledgeFragment(5).setLocalizedText("When killed, it drops 1 Spatial Dash Gem, 1 Eye of Ender, 3-6 Obsidian and 35 experience orbs.").setUnlockRequirements(3,4)
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
		
		BABY_ENDERMAN
		.setPosition(-dist,dist*2)
		.setBackgroundTextureIndex(1)
		.setRenderer(new EntityRenderer(EntityMobBabyEnderman.class,1.35F))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("It spawns in Enchanted Island biome."),
			new TextKnowledgeFragment(1).setLocalizedText("If attacked, it will call their parents that will teleport and defend the baby."),
			new TextKnowledgeFragment(2).setLocalizedText("Occasionally, the baby will steal items from players, unless they are wearing an Enderman Head."),
			new TextKnowledgeFragment(3).setLocalizedText("Each baby has different preferences, it is possible to get stolen item back by throwing another item at the baby, and it might take that instead.").setUnlockRequirements(2),
		});
		
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
		
		ENDER_GUARDIAN
		.setPosition(dist*3,dist*2)
		.setBackgroundTextureIndex(1)
		.setRenderer(new EntityRenderer(EntityMobEnderGuardian.class))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Big golem that spawns in Enchanted Island biome."),
			new TextKnowledgeFragment(1).setLocalizedText("It protects the Enderman Relic, and attacks anyone who has it."),
			new TextKnowledgeFragment(2).setLocalizedText("When attacking, it charges at the player and then teleports away and uses blast attack."),
			new TextKnowledgeFragment(3).setLocalizedText("Drops ??? Ender Pearls, and ??? Obsidian.").setReplacedBy(4),
			new TextKnowledgeFragment(4).setLocalizedText("Drops 0-1 Ender Pearls, and 1-3 Obsidian.").setReplacementFor(3).setUnlockRequirements(3),
		});
		
		FIRE_GOLEM
		.setPosition(dist*2-hdist,dist*3+hdist)
		.setRenderer(new EntityRenderer(EntityMobFireGolem.class,0.85F))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Fiery creature which spawns in the Burning Mountains biome."),
			new TextKnowledgeFragment(1).setLocalizedText("It has both ranged and melee attacks."),
			new TextKnowledgeFragment(2).setLocalizedText("During the ranged attack, it forms fireballs which create large crater.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("If hit by an explosion, it will teleport away with a cooldown."),
			new TextKnowledgeFragment(4).setLocalizedText("Drops 0-1 Fire Charges and ??? Fiery Essence.").setReplacedBy(5),
			new TextKnowledgeFragment(5).setLocalizedText("Drops 0-1 Fire Charges and 1-3 Fiery Essence.").setReplacementFor(4)
		});
		
		SCORCHING_LENS
		.setPosition(dist*2+hdist,dist*3+hdist)
		.setRenderer(new EntityRenderer(EntityMobScorchingLens.class))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Small biped with a large eye, it spawns in Burning Mountains biome."),
			new TextKnowledgeFragment(1).setLocalizedText("It attacks any player close to it by breathing fire out of it's eye."),
			new TextKnowledgeFragment(2).setLocalizedText("The fire causes damage and has a chance to knock the player back.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("Drops ??? Igneous Rock and ??? Fiery Essence.").setReplacedBy(4),
			new TextKnowledgeFragment(4).setLocalizedText("Drops 0-2 Igneous Rock and 1-2 Fiery Essence.").setReplacementFor(3).setUnlockRequirements(3)
		});
		
		SILVERFISH_BLOOD
		.setPosition(-dist*3,-dist)
		.setRenderer(new ItemStackRenderer(ItemList.silverfish_blood))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("There is a 1 in ?? chance a Silverfish will drop Silverfish Blood.").setReplacedBy(1),
			new TextKnowledgeFragment(1).setLocalizedText("There is a 1 in 56 chance a Silverfish will drop Silverfish Blood.").setReplacementFor(0).setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("However, if a Golden Sword is used to kill the Silverfish, chance of getting Silverfish Blood is 1 in 14.").setUnlockRequirements(1)
		});
		
		DRY_SPLINTER
		.setPosition(-dist*3,dist)
		.setRenderer(new ItemStackRenderer(ItemList.dry_splinter))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Each of the Spooky Logs has a 1 in ? chance of dropping Dry Splinter.").setReplacedBy(1),
			new TextKnowledgeFragment(1).setLocalizedText("Each of the Spooky Logs has a 1 in 8 chance of dropping Dry Splinter.").setReplacementFor(0).setUnlockRequirements(0),
			new CraftingKnowledgeFragment(2).setRecipeFromRegistry(new ItemStack(BlockList.spooky_log)),
			new CraftingKnowledgeFragment(3).setRecipeFromRegistry(new ItemStack(BlockList.spooky_leaves))
		});
		
		INFESTATION_REMEDY
		.setPosition(-dist*2+hdist,0)
		.setRenderer(new ItemStackRenderer(ItemList.infestation_remedy))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Magical potion, that can partially suppress effect of infestation."),
			new TextKnowledgeFragment(1).setLocalizedText("When drank, it will erase 2.5 to 3 minutes of infestation effects.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("It must be brewed in an Enhanced Brewing Stand using Silverfish Blood.").setUnlockRequirements(1)
			//new TextKnowledgeFragment(2).setLocalizedText("It must be brewed in a cauldron with heat source below.").setUnlockRequirements(1),
			//new TextKnowledgeFragment(3).setLocalizedText("Silverfish Blood, Infested Bat Wing and Dry Splinter must be thrown into the cauldron, and then slowly boiled.").setUnlockRequirements(2),
			//new TextKnowledgeFragment(4).setLocalizedText("Fiery Essence Altar speeds up the boiling process.").setUnlockRequirements(3),
			//new TextKnowledgeFragment(5).setLocalizedText("Leather Armor can be dyed with colored water inside the cauldron.").setUnlockRequirements(3)
		});
		
		SPOOKY_TREES
		.setPosition(0,0)
		.setRenderer(new ItemStackRenderer(BlockList.spooky_log))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Each tree consists of Spooky Logs and Spooky Leaves."),
			new TextKnowledgeFragment(1).setLocalizedText("Breaking a log will make other logs above break too, and the leaves will very quickly decay.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Some of the trees have faces on them, breaking them will have ??% chance of spawning a Forest Ghost.").setReplacedBy(3),
			new TextKnowledgeFragment(3).setLocalizedText("Some of the trees have faces on them, breaking them will have 25% chance of spawning a Forest Ghost.").setReplacementFor(2).setUnlockRequirements(1)
		});
		
		GHOST_AMULET
		.setPosition(dist,0)
		.setRenderer(new ItemStackRenderer(ItemList.ghost_amulet))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Can be found as uncommon loot in Silverfish Dungeons."),
			new TextKnowledgeFragment(1).setLocalizedText("It will banish the Forest Ghost as soon as they spawn, and have ??% chance of turning it into Endoplasm.").setReplacedBy(2),
			new TextKnowledgeFragment(2).setLocalizedText("It will banish the Forest Ghost as soon as they spawn, and have 60% chance of turning it into Endoplasm.").setReplacementFor(1).setUnlockRequirements(1)
		});
		
		ECTOPLASM
		.setPosition(dist*2,0)
		.setRenderer(new ItemStackRenderer(ItemList.ectoplasm))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Strange ethereal substance dropped by Forest Ghosts."),
			new TextKnowledgeFragment(1).setLocalizedText("It gives spectral powers to certain items.").setUnlockRequirements(0),
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
		
		DRAGON_LAIR
		.setPosition(dist*2+hdist,0)
		.setRenderer(new ItemStackRenderer(Blocks.dragon_egg).setTooltip("Dragon Lair"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Large island, which serves as a battle area for the Ender Dragon boss."),
			new TextKnowledgeFragment(1).setLocalizedText("Several spikes of various sizes, made of Falling Obsidian, and with Ender Crystals on top, are scattered across the terrain.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("Ender Crystals may have Iron Bars around, drop TNT and attract Endermen to the shooter, or create a powerful explosion that will scatter Falling Obsidian around.").setUnlockRequirements(1)
		});
		
		ENDSTONE_BLOB
		.setPosition(dist+hdist,0)
		.setRenderer(new ItemStackRenderer(Blocks.end_stone).setTooltip("Endstone Blob"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Small islands of varying shapes, sizes and features, made of End Stone."),
			new TextKnowledgeFragment(1).setLocalizedText("Most common feature is a small cluster of End Powder Ore.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("They will also sometimes contain Obsidian, Death Flowers, small Ender Goo lakes, and rarely tiny clusters of Igneous Rock Ore.").setUnlockRequirements(0)
		});
		
		DUNGEON_TOWER
		.setPosition(hdist,0)
		.setRenderer(new ItemStackRenderer(BlockList.obsidian_special,1).setTooltip("Dungeon Tower"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("A tall tower, which spawns in the End, and is made of different variations of Obsidian."),
			new TextKnowledgeFragment(1).setLocalizedText("Each tower has 3 regular floors which often contain Angry Enderman spawners, and 1 chest floor with various loot.").setUnlockRequirements(0),
			new TextKnowledgeFragment(2).setLocalizedText("The floors are randomly generated, and containers such as Dispensers or Furnaces will often have loot too.").setUnlockRequirements(1),
			new TextKnowledgeFragment(3).setLocalizedText("Angry Endermen in the tower have random potion effects, getting more dangerous as you get higher.").setUnlockRequirements(1),
			new TextKnowledgeFragment(4).setLocalizedText("Some floors contain Brewing Stands, and there is a 1 in 100 chance a Brewing Stand will spawn as Enhanced Brewing Stand.").setUnlockRequirements(2),
			new TextKnowledgeFragment(5).setLocalizedText("On top of the tower lies the Ender Eye mini-boss.").setUnlockRequirements(3,4)
		});
		
		METEOROID
		.setPosition(-hdist,0)
		.setRenderer(new ItemStackRenderer(BlockList.sphalerite).setTooltip("Meteoroid"))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Tiny clump of Sphalerite which spawns far from the main island."),
			new TextKnowledgeFragment(1).setLocalizedText("It will often contain Sphalerite with Stardust.").setUnlockRequirements(0)
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
		
		DEATH_FLOWER
		.setPosition(-dist,dist*2)
		.setRenderer(new ItemStackRenderer(BlockList.death_flower))
		.setFragments(new KnowledgeFragment[]{
			new TextKnowledgeFragment(0).setLocalizedText("Can be found on Endstone Blobs across the End dimension."),
			new TextKnowledgeFragment(1).setLocalizedText("It can be placed on all types of End Stone, Grass and Dirt."),
			new TextKnowledgeFragment(2).setLocalizedText("If it is planted outside the End, the flower will slowly decay."),
			new TextKnowledgeFragment(3).setLocalizedText("Whilst decaying, it will spawn Angry Endermen nearby.").setUnlockRequirements(2),
			new TextKnowledgeFragment(4).setLocalizedText("After it completely decays, it will become dark and cause massive decay damage to large area around it.").setUnlockRequirements(2),
			new TextKnowledgeFragment(5).setLocalizedText("Partially decayed flower can be healed using End Powder."), // unlocks with End Powder fragment
			new CraftingKnowledgeFragment(6).setCustomRecipe(new ItemStack(Items.dye,2,13),new ItemStack(BlockList.death_flower,1,0)),
			new CraftingKnowledgeFragment(7).setCustomRecipe(new ItemStack(Items.dye,2,8),new ItemStack(BlockList.death_flower,1,15)).setUnlockRequirements(4)
		});

		Stopwatch.finish("KnowledgeRegistrations");
	}
	
	private KnowledgeRegistrations(){}
}*/
