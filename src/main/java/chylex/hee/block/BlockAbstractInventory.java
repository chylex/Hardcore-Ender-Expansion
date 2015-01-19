package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class BlockAbstractInventory extends BlockContainer{
	protected BlockAbstractInventory(Material material){
		super(material);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity tile = world.getTileEntity(pos);

		if (tile instanceof IInventory){
			IInventory inv = (IInventory)tile;

			for(int a = 0; a < inv.getSizeInventory(); ++a){
				ItemStack is = inv.getStackInSlot(a);
				if (is == null)continue;

				float offX = world.rand.nextFloat()*0.8F+0.1F;
				float offY = world.rand.nextFloat()*0.8F+0.1F;
				float offZ = world.rand.nextFloat()*0.8F+0.1F;

				EntityItem entityItem = new EntityItem(world,pos.getX()+offX,pos.getY()+offY,pos.getZ()+offZ,is.copy());
				entityItem.motionX = world.rand.nextGaussian()*0.05F;
				entityItem.motionY = world.rand.nextGaussian()*0.05F+0.2F;
				entityItem.motionZ = world.rand.nextGaussian()*0.05F;
				world.spawnEntityInWorld(entityItem);
			}
		}

		super.breakBlock(world,pos,state);
	}
}
