package chylex.hee.world.structure.island.gen;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.BlockLocation;

public class OreGenerator{
	private static final byte attemptMp = 3;
	
	private final int minX,minY,minZ,maxX,maxY,maxZ;
	
	private final List<OreLocationList> oreListStardust = new ArrayList<>();
	private final List<OreLocationList> oreListInstabilityOrb = new ArrayList<>();
	
	public OreGenerator(int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public void setup(Random rand, IslandBiomeBase biome){
		int amount = (int)(495F*biome.getOreAmountMultiplier());
		for(int a = 0; a < amount; a++){
			generateOreCluster(oreListStardust,rand,3+rand.nextInt(7));
		}
		
		amount = (int)(260F*biome.getOreAmountMultiplier());
		for(int a = 0; a < amount; a++){
			generateOreCluster(oreListInstabilityOrb,rand,1+rand.nextInt(3));
		}
	}
	
	public void generate(LargeStructureWorld world, Random rand){
		int attempt = 0, placed = 0, attemptAm;
		BlockLocation loc;
		
		for(OreLocationList list:oreListStardust){
			attemptAm = list.placeAmount*attemptMp;
			
			for(attempt = 0, placed = 0; attempt < attemptAm && placed < list.placeAmount; ++attempt){
				loc = list.blockList.get(attempt);
				if (loc.y >= 0 && world.getBlock(loc.x,loc.y,loc.z) == Blocks.end_stone){
					world.setBlock(loc.x,loc.y,loc.z,BlockList.stardust_ore,rand.nextInt(15)+1);
					++placed;
				}
			}
		}
		
		for(OreLocationList list:oreListInstabilityOrb){
			attemptAm = list.placeAmount*attemptMp;
			
			for(attempt = 0, placed = 0; attempt < attemptAm && placed < list.placeAmount; ++attempt){
				loc = list.blockList.get(attempt);
				if (world.getBlock(loc.x,loc.y,loc.z) == Blocks.end_stone){
					world.setBlock(loc.x,loc.y,loc.z,BlockList.instability_orb_ore);
					++placed;
				}
			}
		}
	}
	
	private void generateOreCluster(List<OreLocationList> locList, Random rand, int amount){
		int xx = rand.nextInt(maxX-minX+1)+minX,yy = rand.nextInt(maxY-minY+1)+minY,zz = rand.nextInt(maxZ-minZ+1)+minZ;
		double sqrtAmount = Math.sqrt(amount*1.75D);
		
		OreLocationList list = new OreLocationList(amount);
		
		if (yy < minY+14)yy = rand.nextInt(maxY-minY+1)+minY; // try again!
		
		int n = amount*attemptMp;
		for(int a = 0; a < n; ++a){
			list.blockList.add(new BlockLocation(
				xx+(int)(MathHelper.cos((float)(rand.nextDouble()*2D*Math.PI))*sqrtAmount*rand.nextDouble()),
				yy+(int)(MathHelper.cos((float)(rand.nextDouble()*2D*Math.PI))*sqrtAmount*rand.nextDouble()),
				zz+(int)(MathHelper.cos((float)(rand.nextDouble()*2D*Math.PI))*sqrtAmount*rand.nextDouble())
			));
		}
		
		locList.add(list);
	}
	
	private class OreLocationList{
		private final List<BlockLocation> blockList;
		private final byte placeAmount;
		
		public OreLocationList(int amount){
			this.blockList = new ArrayList<>(amount*attemptMp);
			this.placeAmount = (byte)amount;
		}
	}
}
