package chylex.hee.world.biome;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.system.util.MathUtil;

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
	public List getSpawnableList(EnumCreatureType type){
		return emptyList;
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
