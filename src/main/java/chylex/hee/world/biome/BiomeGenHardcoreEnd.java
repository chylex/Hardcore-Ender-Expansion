package chylex.hee.world.biome;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import chylex.hee.system.util.MathUtil;

public final class BiomeGenHardcoreEnd extends BiomeGenEnd{
	public static boolean overrideMobLists;
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
			
			spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class,10,4,4));
		}
		
		if (!MathUtil.floatEquals(overworldEndermanMultiplier,1F)){
			for(BiomeType type:BiomeManager.BiomeType.values()){
				for(BiomeEntry entry:BiomeManager.getBiomes(type)){
					List<SpawnListEntry> spawnList = entry.biome.getSpawnableList(EnumCreatureType.monster);
					
					for(SpawnListEntry spawnEntry:spawnList){
						if (spawnEntry.entityClass == EntityEnderman.class){
							spawnEntry.itemWeight = Math.round(spawnEntry.itemWeight*overworldEndermanMultiplier);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
