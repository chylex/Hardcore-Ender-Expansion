package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.world.util.Direction;
import chylex.hee.init.BlockList;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.system.util.BlockPosM;

public class BlobPopulatorTransportBeacon extends BlobPopulator{
	public BlobPopulatorTransportBeacon(int weight){
		super(weight);
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockPosM> locs = gen.getUsedLocations();
		BlockPosM top = null;
		
		while(!locs.isEmpty()){
			BlockPosM loc = locs.remove(rand.nextInt(locs.size()));
			if (gen.getBlock(loc.x,loc.y,loc.z) == Blocks.end_stone && gen.getBlock(loc.x,loc.y+1,loc.z) == Blocks.air && (top == null || loc.y > top.y)){
				boolean canGen = true;
				
				for(int dir = 0; dir < 4; dir++){
					if (gen.getBlock(loc.x+Direction.offsetX[dir],loc.y,loc.z+Direction.offsetZ[dir]) != Blocks.end_stone){
						canGen = false;
						break;
					}
				}
				
				if (canGen)top = loc;
			}
		}
		
		if (top != null)gen.setBlock(top.x,top.y,top.z,BlockList.transport_beacon);
	}
}
