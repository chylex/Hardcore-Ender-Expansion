package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

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
	
	protected abstract boolean canStructureSpawn(int x, int z, double dist, Random rand);
	
	@Override
	protected final boolean canSpawnStructureAtCoords(int x, int z){
		int origChunkX = x, origChunkZ = z;

		if (x < 0)x -= maxSpacing-1;
		if (z < 0)z -= maxSpacing-1;

		int x2 = x/maxSpacing, z2 = z/maxSpacing;
		
		coordCheckRand.setSeed(x2*341873128712L+z2*132897987541L+worldObj.getWorldInfo().getSeed()+358041L);
		coordCheckRand.nextInt(1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount());
		
		x2 *= maxSpacing;
		z2 *= maxSpacing;
		
		x2 += coordCheckRand.nextInt(maxSpacing-minSpacing);
		z2 += coordCheckRand.nextInt(maxSpacing-minSpacing);

		if (origChunkX == x2 && origChunkZ == z2){
			double dist = Math.sqrt(MathUtil.square(x2*16L+(featureSize>>1))+MathUtil.square(z2*16L+(featureSize>>1)));
			return dist >= minDistanceFromCenter && canStructureSpawn(x2,z2,dist,coordCheckRand);
		}
		else return false;
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			int minSpacing = 8, maxSpacing = 15;
			
			int deathAmt = WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount();
			int px = MathUtil.floor(player.posX), py = MathUtil.floor(player.posY), pz = MathUtil.floor(player.posZ);
			Random rand = new Random();
			BlockPosM pos = new BlockPosM();
			
			for(int x = -48; x <= 48; x++){
				for(int z = -48; z <= 48; z++){
					int xx = x, zz = z;
					int origChunkX = xx, origChunkZ = zz;

					if (xx < 0)xx -= maxSpacing-1;
					if (zz < 0)zz -= maxSpacing-1;

					int x2 = xx/maxSpacing, z2 = zz/maxSpacing;
					
					rand.setSeed(x2*341873128712L+z2*132897987541L+world.getWorldInfo().getSeed()+358041L);
					rand.nextInt(1+deathAmt);
					
					x2 *= maxSpacing;
					z2 *= maxSpacing;
					
					world.setBlockState(pos.moveTo(px+x2,py-1,pz+z2),Blocks.emerald_block.getDefaultState());
					
					x2 += rand.nextInt(maxSpacing-minSpacing);
					z2 += rand.nextInt(maxSpacing-minSpacing);
					
					if (origChunkX == x2 && origChunkZ == z2 && rand.nextInt(3) == 0)world.setBlockState(pos.moveTo(px+x,py-1,pz+z),Blocks.brick_block.getDefaultState());
				}
			}
		}
	};
}
