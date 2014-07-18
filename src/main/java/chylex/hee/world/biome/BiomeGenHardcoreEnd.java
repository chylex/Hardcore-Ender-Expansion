package chylex.hee.world.biome;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenEnd;

public class BiomeGenHardcoreEnd extends BiomeGenEnd{
	private static BiomeGenHardcoreEnd instance;
	
	public static void addMonsterSpawnEntry(SpawnListEntry spawnEntry){
		instance.spawnableMonsterList.add(spawnEntry);
	}
	
	@SuppressWarnings("unchecked")
	public BiomeGenHardcoreEnd(int id){
		super(id);
		
		spawnableMonsterList.clear();
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();
		
		spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class,10,4,4));
		
		topBlock = Blocks.dirt;
		fillerBlock = Blocks.dirt;
		theBiomeDecorator = createBiomeDecorator();
		
		instance = this;
	}
	
	@Override
	public BiomeDecorator createBiomeDecorator(){
		return new BiomeDecoratorHardcoreEnd();
	}
}
