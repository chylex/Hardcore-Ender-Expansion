package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.system.util.CollectionUtil;

public abstract class BlockAbstractEnhanceable extends BlockContainer{
	public BlockAbstractEnhanceable(Material material){
		super(material);
	}
	
	@Override
	public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune){		
		TileEntity tile = world.getTileEntity(x,y,z);
		return tile instanceof IEnhanceableTile ? CollectionUtil.newList(((IEnhanceableTile)tile).createEnhancedItemStack()) : super.getDrops(world,x,y,z,metadata,fortune);
	}

	@Override
	public final int quantityDropped(Random rand){
		return 1;
	}
}
