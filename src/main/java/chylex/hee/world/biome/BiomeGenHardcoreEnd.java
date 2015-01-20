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
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public final class BiomeGenHardcoreEnd extends BiomeGenEnd{
	public static boolean overrideMobLists;
	public static boolean overrideWorldGen;
	public static float overworldEndermanMultiplier;
	
	public BiomeGenHardcoreEnd(int id){
		super(id);
		
		topBlock = Blocks.dirt.getDefaultState();
		fillerBlock = Blocks.dirt.getDefaultState();
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
		
		if (!MathUtil.floatEquals(overworldEndermanMultiplier,1F) || ModCommonProxy.hardcoreEnderbacon){
			for(BiomeType type:BiomeManager.BiomeType.values()){
				for(BiomeEntry entry:BiomeManager.getBiomes(type)){
					List<SpawnListEntry> spawnList = entry.biome.getSpawnableList(EnumCreatureType.MONSTER);
					
					for(SpawnListEntry spawnEntry:spawnList){
						if (spawnEntry.entityClass == EntityEnderman.class){
							spawnEntry.entityClass = EntityMobEnderman.class;
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
