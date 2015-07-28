package chylex.hee.system.test.list;
import java.util.Random;
import chylex.hee.api.HeeIMC;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.mechanics.essence.handler.DragonEssenceHandler;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.system.integration.ModIntegrationManager;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.tileentity.TileEntityExperienceTable;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ImcTests{
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.PREINIT)
	public void prepareImcs(){
		for(String msgs:new String[]{
			"HEE:DragonEssence:AddRecipe { 'input': { 'id': 'ghast_tear' }, 'output': { 'id': '~hee:spectral_tear' }, 'cost': 15 }",
			"HEE:DragonEssence:RemoveRecipe { 'type': 'input', 'search': { 'id': '~hee:*' }, 'limit': 1 }",
			
			"HEE:Mobs:SetGooImmune { 'id': 'Blaze' }",
			"HEE:Mobs:SetEnergy { 'id': 'HardcoreEnderExpansion.VampireBat', 'units': 2.5 }",
			
			"HEE:Orb:ItemBlacklist { 'pattern': { 'id': '*' } }",
			"HEE:Orb:MobAdd { 'id': 'Villager' }",
			"HEE:Orb:MobAdd { 'id': 'WitherBoss' }", // should fail
			"HEE:Orb:MobRemove { 'id': 'Creeper' }",
			
			"HEE:DecompositionTable:Blacklist { 'pattern': { 'id': 'dispenser' } }",
			"HEE:ExtractionTable:SetEnergy { 'item': { 'id': 'coal', 'damage': 1 }, 'units': 5.4 }",
			"HEE:ExperienceTable:AddItem { 'item': { 'id': 'coal' }, 'bottles': 12 }",
			
			"HEE:World:LootAdd { 'list': 'DungeonTowerChest', 'item': { 'id': 'minecraft:dye', 'damage': [ 0, 15 ], 'count': [ 2 ], 'weight': 32000 } }",
			"HEE:World:LootRemove { 'list': 'DungeonTowerFurnaceFuel', 'search': { 'id': '*' }, 'limit': 6 }",
			"HEE:World:BiomeMobAdd { 'biome': 'InfestedForest.Ravaged', 'mob': { 'id': 'Silverfish', 'limit': 32, 'weight': 10 } }",
			
			"HEE:System:DisableIntegration { 'modid': 'NotEnoughItems' }"
		})HeeIMC.acceptString("UnitTester",msgs.replace('\'','"'));
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcsDragonEssence(){
		Assert.equal(DragonEssenceHandler.recipes.size(),3,"Unexpected list size, expected $2, got $1.");
		Assert.equal(DragonEssenceHandler.recipes.get(1).input.getItem(),Items.ender_eye,"Unexpected second entry, expected $2, got $1. Full list: "+DragonEssenceHandler.recipes);
		Assert.equal(DragonEssenceHandler.recipes.get(2).cost,15,"Unexpected recipe cost, expected $2, got $1.");
		
		ItemStack tear = new ItemStack(Items.ghast_tear);
		
		for(AltarItemRecipe recipe:DragonEssenceHandler.recipes){
			if (recipe.isApplicable(tear))return;
		}
		
		Assert.fail("Failed searching for the newly added recipe.");
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcsMobs(){
		Assert.state(GlobalMobData.isEnderGooTolerant(new EntityBlaze(null)),"Expected Blaze to be marked as a Goo tolerant mob.");
		Assert.equal(EnergyValues.getMobEnergy(new EntityMobVampiricBat(null)),EnergyChunkData.energyDrainUnit*2.5F,"Unexpected mob Energy value, expected $2, got $1.");
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcsOrb(){
		Assert.state(OrbSpawnableMobs.classList.contains(EntityVillager.class),"Expected Villager to have been added to mob list.");
		Assert.state(!OrbSpawnableMobs.classList.contains(EntityWither.class),"Expected Wither to NOT have been added to mob list.");
		Assert.state(!OrbSpawnableMobs.classList.contains(EntityCreeper.class),"Expected Creeper to have been removed from mob list.");
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcTables(){
		Assert.isNull(StardustDecomposition.getRandomRecipeIngredientsFor(new ItemStack(Blocks.dispenser),new Random()),"Unexpected ingredient list, expected null, got $.");
		Assert.notNull(StardustDecomposition.getRandomRecipeIngredientsFor(new ItemStack(Items.bow),new Random()),"Unexpected ingredient list, got null.");
		
		Assert.equal(EnergyValues.getItemEnergy(new ItemStack(Items.coal,1,1)),EnergyChunkData.energyDrainUnit*5.4F,"Unexpected item Energy value, expected $2, got $1.");
		Assert.equal(EnergyValues.getItemEnergy(new ItemStack(Items.coal,1,0)),0F,"Unexpected item Energy value, expected $2, got $1."); // make sure it only takes charcoal
		
		Assert.equal(TileEntityExperienceTable.getDirectExperience(new ItemStack(Items.coal,1,0)),12,"Unexpected item Experience value, expected $2, got $1.");
		Assert.equal(TileEntityExperienceTable.getDirectExperience(new ItemStack(Items.coal,1,1)),12,"Unexpected item Experience value, expected $2, got $1."); // make sure it takes all damage values
	}
	/* TODO
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcWorld(){
		Assert.equal(ComponentTower.lootFuel.size(),2,"Unexpected list size, expected $2, got $1. List: "+ComponentTower.lootFuel);
		
		SpawnEntry entry = IslandBiomeBase.infestedForest.getSpawnEntries(IslandBiomeInfestedForest.RAVAGED).get(0);
		Assert.equal(entry.getMobClass(),EntitySilverfish.class,"Unexpected entry data, expected $2, got $1.");
		Assert.equal(entry.getMaxAmount(),32,"Unexpected entry data, expected $2, got $1.");
		Assert.equal(entry.getWeight(),10,"Unexpected entry data, expected $2, got $1.");
		
		Random rand = new Random();
		
		for(int attempt = 0; attempt < 500; attempt++){
			ItemStack is = ComponentTower.lootTower.generateIS(rand);
			
			if (is.getItem() == Items.dye){
				Assert.equal(is.stackSize,2,"Unexpected loot stack size, expected $2, got $1.");
				return;
			}
		}
		
		Assert.fail("Unexpected weighted loot check result, never generated the expected item.");
	}*/
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcSystem(){
		Assert.state(ModIntegrationManager.blacklistedMods.contains("NotEnoughItems"),"Missing entry in mod integration blacklist, expected NotEnoughItems to be present.");
	}
}
