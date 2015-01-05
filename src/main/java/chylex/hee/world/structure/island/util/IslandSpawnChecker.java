package chylex.hee.world.structure.island.util;
import java.util.Random;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.util.WorldGenChance;

public final class IslandSpawnChecker{
	private static final Random coordCheckRand = new Random(0L);
	public static final int minSpacing = 13, maxSpacing = 28, minDistanceFromCenter = 1600, featureSize = 208;
	
	/**
	 * Returns biome ID of present island, or -1 if none was found.
	 */
	public static final byte getIslandBiomeAt(int chunkX, int chunkZ, long seed1, int seed2){
		int origChunkX = chunkX, origChunkZ = chunkZ;

		if (chunkX < 0)chunkX -= maxSpacing-1;
		if (chunkZ < 0)chunkZ -= maxSpacing-1;

		int x2 = chunkX/maxSpacing, z2 = chunkZ/maxSpacing;
		
		coordCheckRand.setSeed(x2*341873128712L+z2*132897987541L+seed1+358041L);
		coordCheckRand.nextInt(seed2);
		
		x2 *= maxSpacing;
		z2 *= maxSpacing;
		
		x2 += coordCheckRand.nextInt(maxSpacing-minSpacing);
		z2 += coordCheckRand.nextInt(maxSpacing-minSpacing);

		if (origChunkX != x2 || origChunkZ != z2)return -1;
		
		double dist = Math.sqrt(MathUtil.square(x2*16L+(featureSize>>1))+MathUtil.square(z2*16L+(featureSize>>1)));
		
		if (dist >= minDistanceFromCenter && coordCheckRand.nextInt(7) <= 4 && WorldGenChance.checkChance(0.65D+0.35D*WorldGenChance.linear2Incr.calculate(dist,1600D,4000D),coordCheckRand)){
			int x = origChunkX*16, z = origChunkZ*16;
			coordCheckRand.setSeed(((x/9)*238504L+(z/9)*10058432215L)^seed1);
			coordCheckRand.nextInt(25);
			return IslandBiomeBase.pickRandomBiome(coordCheckRand).biomeID;
		}
		else return -1;
	}
	
	private IslandSpawnChecker(){}
}
