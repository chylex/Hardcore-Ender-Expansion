package chylex.hee.system.test.list.old;
import java.util.Random;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.api.HeeIMC;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.game.integration.ModIntegrationManager;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.mechanics.essence.handler.DragonEssenceHandler;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;
import chylex.hee.system.test.UnitTest.RunTime;
import chylex.hee.tileentity.TileEntityExperienceTable;

public class ImcTests{
	@UnitTest(runTime = RunTime.PREINIT)
	public void prepareImcs(){
		for(String msgs:new String[]{
			"HEE:DragonEssence:AddRecipe { 'input': { 'id': 'ghast_tear' }, 'output': { 'id': '~hee:spectral_tear' }, 'cost': 15 }",
			"HEE:DragonEssence:RemoveRecipe { 'type': 'input', 'search': { 'id': '~hee:*' }, 'limit': 1 }",
			
			"HEE:Mobs:SetGooImmune { 'id': 'Blaze' }",
			
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
	
	@UnitTest
	public void testImcsDragonEssence(){
		Assert.equal(DragonEssenceHandler.recipes.size(),3);
		Assert.equal(DragonEssenceHandler.recipes.get(1).input.getItem(),Items.ender_eye);
		// TODO Assert.equal(DragonEssenceHandler.recipes.get(2).cost,15);
		
		ItemStack tear = new ItemStack(Items.ghast_tear);
		
		for(AltarItemRecipe recipe:DragonEssenceHandler.recipes){
			if (recipe.isApplicable(tear))return;
		}
		
		Assert.fail("Failed searching for the newly added recipe.");
	}
	
	@UnitTest
	public void testImcsMobs(){
		Assert.isTrue(GlobalMobData.isEnderGooTolerant(new EntityBlaze(null)));
	}
	
	@UnitTest
	public void testImcsOrb(){
		Assert.contains(OrbSpawnableMobs.classList,EntityVillager.class);
		Assert.notContains(OrbSpawnableMobs.classList,EntityWither.class);
		Assert.contains(OrbSpawnableMobs.classList,EntityCreeper.class);
	}
	
	@UnitTest
	public void testImcTables(){
		Assert.isNull(StardustDecomposition.getRandomRecipeIngredientsFor(new ItemStack(Blocks.dispenser),new Random()));
		Assert.notNull(StardustDecomposition.getRandomRecipeIngredientsFor(new ItemStack(Items.bow),new Random()));
		
		Assert.equal(EnergyValues.getItemEnergy(new ItemStack(Items.coal,1,1)),EnergyValues.unit*5.4F);
		Assert.equal(EnergyValues.getItemEnergy(new ItemStack(Items.coal,1,0)),0F); // make sure it only takes charcoal
		
		Assert.equal(TileEntityExperienceTable.getDirectExperience(new ItemStack(Items.coal,1,0)),12);
		Assert.equal(TileEntityExperienceTable.getDirectExperience(new ItemStack(Items.coal,1,1)),12); // make sure it takes all damage values
	}
	/* TODO
	@UnitTest
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
	
	@UnitTest
	public void testImcSystem(){
		Assert.contains(ModIntegrationManager.blacklistedMods,"NotEnoughItems");
	}
}
