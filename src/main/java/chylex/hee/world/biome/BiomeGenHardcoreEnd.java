package chylex.hee.world.biome;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.system.util.MathUtil;

public final class BiomeGenHardcoreEnd extends BiomeGenEnd{
	public static boolean overrideMobLists;
	public static boolean overrideWorldGen;
	public static float overworldEndermanMultiplier;
	
	public BiomeGenHardcoreEnd(int id){
		super(id);
		
		topBlock = Blocks.dirt;
		fillerBlock = Blocks.dirt;
		theBiomeDecorator = createBiomeDecorator();
	}
	
	public void overrideMobLists(){
		if (overrideMobLists){
			spawnableMonsterList.clear();
			spawnableCreatureList.clear();
			spawnableWaterCreatureList.clear();
			spawnableCaveCreatureList.clear();
			spawnableMonsterList.add(new SpawnListEntry(EntityMobEnderman.class,10,4,4));
		}
		else{
			for(SpawnListEntry entry:(List<SpawnListEntry>)spawnableMonsterList){
				if (entry.entityClass == EntityEnderman.class){
					entry.entityClass = EntityMobEnderman.class;
					break;
				}
			}
		}
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			
			List<SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.monster);
			
			for(SpawnListEntry spawnEntry:spawnList){
				if (spawnEntry.entityClass == EntityEnderman.class){
					spawnEntry.entityClass = EntityMobEnderman.class;
					if (!MathUtil.floatEquals(overworldEndermanMultiplier,1F))spawnEntry.itemWeight = Math.round(spawnEntry.itemWeight*overworldEndermanMultiplier);
					break;
				}
			}
		}
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
