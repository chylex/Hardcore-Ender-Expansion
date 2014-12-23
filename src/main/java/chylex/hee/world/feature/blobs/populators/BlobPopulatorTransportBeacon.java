package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;

public class BlobPopulatorTransportBeacon extends BlobPopulator{
	public BlobPopulatorTransportBeacon(int weight){
		super(weight);
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockLocation> locs = gen.getUsedLocations();
		
		while(!locs.isEmpty()){
			BlockLocation loc = locs.remove(rand.nextInt(locs.size()));
			
			if (gen.getBlock(loc.x,loc.y,loc.z) == Blocks.end_stone && gen.getTopBlockY(loc.x,loc.z) == loc.y){
				gen.setBlock(loc.x,loc.y+1,loc.z,BlockList.transport_beacon);
				break;
			}
		}
	}
}
