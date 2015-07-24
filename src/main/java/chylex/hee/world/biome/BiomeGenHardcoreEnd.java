package chylex.hee.world.biome;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import chylex.hee.entity.mob.EntityMobEnderman;

public final class BiomeGenHardcoreEnd extends BiomeGenEnd{
	public static float overworldEndermanMultiplier;
	
	private List emptyList = new ArrayList();
	
	public BiomeGenHardcoreEnd(int id){
		super(id);
		theBiomeDecorator = createBiomeDecorator();
	}
	
	public void overrideMobLists(){
		spawnableMonsterList.clear();
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			
			SpawnListEntry endermanEntry = null;
			int totalWeight = 0;
			
			for(SpawnListEntry spawnEntry:(List<SpawnListEntry>)biome.getSpawnableList(EnumCreatureType.monster)){
				if (spawnEntry.entityClass == EntityEnderman.class)endermanEntry = spawnEntry;
				else totalWeight += spawnEntry.itemWeight;
			}
			
			if (endermanEntry != null){
				endermanEntry.entityClass = EntityMobEnderman.class;
				endermanEntry.itemWeight = Math.round(totalWeight*overworldEndermanMultiplier*0.04F); // ~double of vanilla weight by default
			}
		}
	}
	
	@Override
	public List getSpawnableList(EnumCreatureType type){
		return emptyList;
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
