package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.biome.BiomeDecoratorHardcoreEnd;

public abstract class MapGenScatteredFeatureCustom extends MapGenScatteredFeature{
	private final Random coordCheckRand = new Random();
	protected final int minSpacing;
	protected final int maxSpacing;
	protected final int minDistanceFromCenter;
	protected final int featureSize;
	
	public MapGenScatteredFeatureCustom(int minSpacing, int maxSpacing, int minDistanceFromCenter, int featureSize){
		this.minSpacing = minSpacing;
		this.maxSpacing = maxSpacing;
		this.minDistanceFromCenter = minDistanceFromCenter;
		this.featureSize = featureSize;
	}
	
	protected abstract boolean canStructureSpawn(int x, int z, Random rand);
	protected abstract String getStructureName();
	
	@Override
	protected final boolean canSpawnStructureAtCoords(int x, int z){
		int origChunkX = x, origChunkZ = z;

		if (x < 0)x -= maxSpacing-1;
		if (z < 0)z -= maxSpacing-1;

		int x2 = x/maxSpacing, z2 = z/maxSpacing;
		
		coordCheckRand.setSeed(x2*341873128712L+z2*132897987541L+worldObj.getWorldInfo().getSeed()+358041L);
		coordCheckRand.nextInt(1+BiomeDecoratorHardcoreEnd.getCache(worldObj).getDragonDeathAmount());
		
		x2 *= maxSpacing;
		z2 *= maxSpacing;
		
		x2 += coordCheckRand.nextInt(maxSpacing-minSpacing);
		z2 += coordCheckRand.nextInt(maxSpacing-minSpacing);

		return origChunkX == x2 && origChunkZ == z2 && Math.sqrt(MathUtil.square(x2*16+(featureSize>>1))+MathUtil.square(z2*16+(featureSize>>1))) >= minDistanceFromCenter && canStructureSpawn(x,z,coordCheckRand);
	}
	
	@Override
	public final String func_143025_a(){ // OBFUSCATED get structure name
		return getStructureName();
	}
}
