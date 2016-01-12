package chylex.hee.world.end.gen;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryGenerator;
import chylex.hee.world.end.TerritoryProperties;
import chylex.hee.world.end.tick.ITerritoryBehavior;
import chylex.hee.world.end.tick.TerritoryBehaviorMobSpawner;
import chylex.hee.world.end.tick.TerritoryBehaviorWeather;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.SpawnEntry;

public class TerritoryTest extends TerritoryGenerator{
	public TerritoryTest(EndTerritory territory, EnumSet variations, StructureWorld world, Random rand){
		super(territory,variations,world,rand);
	}

	@Override
	public void generate(){
		BoundingBox box = world.getArea();
		
		for(int x = box.x1; x < box.x2; x++){
			for(int z = box.z1; z < box.z2; z++){
				world.setBlock(x,1,z,Blocks.end_stone);
			}
		}
	}
	
	public static enum Variations{
		VAR1, VAR2, VAR3
	}
	
	public static class Properties extends TerritoryProperties<Variations>{
		public Properties(){
			super(Variations.class);
			
			addCommonVariation(Variations.VAR1,10);
			addCommonVariation(Variations.VAR2,5);
			
			setCommonAmount(RandomAmount.linear);
			
			addRareVariation(Variations.VAR1,10);
			addRareVariation(Variations.VAR2,5);
			addRareVariation(Variations.VAR3,5);
			
			setRareAmount(RandomAmount.linear);
		}
		
		@Override
		public void setupBehaviorList(List<ITerritoryBehavior> list, EndTerritory territory, EnumSet<Variations> variations, Pos centerPos, boolean isRare){
			// Mobs
			TerritoryBehaviorMobSpawner mobSpawner = new TerritoryBehaviorMobSpawner(territory,centerPos);
			mobSpawner.setTickRate(10);
			mobSpawner.setAttemptsPerTick(5);
			mobSpawner.setAttemptsPerMob(20);
			
			mobSpawner.addBuilderModifier(entry -> entry.setSpawnPlayerDistance(32D));
			
			mobSpawner.addEntry(
				SpawnEntry.create(EntityMobEnderman.class,EntityMobEnderman::new)
				.setLocationFinder(mobSpawner.spawnOnSolidFloor())
				.addSpawnCondition(SpawnEntry.noLiquid()),
				3
			);
			
			mobSpawner.addEntry(
				SpawnEntry.create(EntityMobInfestedBat.class,EntityMobInfestedBat::new)
				.setLocationFinder(mobSpawner.spawnInAir())
				.addVanillaSpawnConditions()
				.addSpawnCondition(SpawnEntry.withBlocksNearby(8))
				.setGroupSize(1,4)
				.setGroupLocationFinder(mobSpawner.groupInAir(1D,4D)),
				2
			);
			
			mobSpawner.setMobLimit(80);
			mobSpawner.setMobClassLimit(EntityMobEnderman.class,20);
			
			list.add(mobSpawner);
			
			// Weather
			TerritoryBehaviorWeather weatherSpawner = TerritoryBehaviorWeather.forLightning(territory,centerPos,EntityWeatherLightningBoltSafe::new);
			weatherSpawner.setChancePerTick(1F/30F);
			weatherSpawner.setEffectSpawnAttempts(1);
			weatherSpawner.setEffectAmount(rand -> rand.nextInt(10) == 0 ? 2 : 1);
			weatherSpawner.setDelayAfterTrigger(30,80);
			list.add(weatherSpawner);
		}
	}
}
