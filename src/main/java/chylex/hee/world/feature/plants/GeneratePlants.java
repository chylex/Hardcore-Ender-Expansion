package chylex.hee.world.feature.plants;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.world.feature.GenerateFeature;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.IBlockPicker;

public final class GeneratePlants extends GenerateFeature{
	final Block placeOn;
	final IBlockPicker plantPicker;
	
	private IPlantGenerator plantGenerator;
	
	public GeneratePlants(Block placeOn, IBlockPicker plantPicker){
		this.placeOn = placeOn;
		this.plantPicker = plantPicker;
	}
	
	public GeneratePlants(Block placeOn, Block oreBlock){
		this.placeOn = placeOn;
		this.plantPicker = new BlockInfo(oreBlock);
	}
	
	public GeneratePlants(Block placeOn, Block oreBlock, int oreMeta){
		this.placeOn = placeOn;
		this.plantPicker = new BlockInfo(oreBlock, oreMeta);
	}
	
	public void setPlantGenerator(IPlantGenerator plantGenerator){
		this.plantGenerator = plantGenerator;
	}
	
	@Override
	public void generateSplit(StructureWorld world, Random rand){
		generateSplitInternal(world, rand, 0);
	}

	@Override
	public void generateFull(StructureWorld world, Random rand){
		generateFullInternal(world, rand, 0);
	}

	@Override
	protected boolean tryGenerateFeature(StructureWorld world, Random rand, int x, int y, int z){
		if (world.getBlock(x, y-1, z) == placeOn){
			plantGenerator.generate(this, world, rand, x, y, z);
			return true;
		}
		else return false;
	}
}
