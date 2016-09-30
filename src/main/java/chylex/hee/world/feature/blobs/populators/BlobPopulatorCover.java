package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.BoundingBox;

public class BlobPopulatorCover extends BlobPopulator{
	private Block block = Blocks.air;
	private double chance = 1D;
	private boolean replaceTop;
	
	public BlobPopulatorCover(int weight){
		super(weight);
	}
	
	public BlobPopulatorCover setBlock(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorCover setChance(double chance){
		this.chance = chance;
		return this;
	}
	
	public BlobPopulatorCover setReplaceTop(){
		this.replaceTop = true;
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		BoundingBox area = world.getArea();
		
		for(int x = area.x1; x <= area.x2; x++){
			for(int z = area.z1; z <= area.z2; z++){
				if (chance == 1D || rand.nextFloat() < chance){
					int y = world.getTopY(x, z, Blocks.end_stone);
					if (y != -1)world.setBlock(x, replaceTop ? y : y+1, z, block);
				}
			}
		}
	}
}
