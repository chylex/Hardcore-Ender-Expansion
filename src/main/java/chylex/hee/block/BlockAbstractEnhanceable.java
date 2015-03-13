package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;

public abstract class BlockAbstractEnhanceable extends BlockContainer{
	public BlockAbstractEnhanceable(Material material){
		super(material);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		TileEntity tile = world.getTileEntity(x,y,z);
		if (tile instanceof IEnhanceableTile)dropBlockAsItem(world,x,y,z,((IEnhanceableTile)tile).createEnhancedItemStack());
		
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
	
	@Override
	public final Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public final int damageDropped(int meta){
		return 0;
	}
	
	@Override
	public final int quantityDropped(int meta, int fortune, Random rand){
		return 0;
	}
	
	@Override
	public final int quantityDropped(Random rand){
		return 0;
	}
}
