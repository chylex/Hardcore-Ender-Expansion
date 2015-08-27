package chylex.hee.block.vanilla;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class BlockBasicStairs extends BlockStairs{
	public BlockBasicStairs(Block sourceBlock, int sourceMetadata){
		super(sourceBlock,sourceMetadata);
		this.useNeighborBrightness = true;
		setHardness(sourceBlock.blockHardness*0.75F);
		setResistance(sourceBlock.blockResistance*0.75F);
        setStepSound(sourceBlock.stepSound);
	}
	
	public BlockBasicStairs(Block sourceBlock){
		this(sourceBlock,0);
	}
}