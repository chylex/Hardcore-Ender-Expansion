package chylex.hee.block.base;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockAbstractInventory extends BlockContainer{
	protected BlockAbstractInventory(Material material){
		super(material);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta){
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof IInventory){
			IInventory inv = (IInventory)tile;

			for(int a = 0; a < inv.getSizeInventory(); ++a){
				ItemStack is = inv.getStackInSlot(a);
				if (is == null)continue;

				float offX = world.rand.nextFloat()*0.8F+0.1F;
				float offY = world.rand.nextFloat()*0.8F+0.1F;
				float offZ = world.rand.nextFloat()*0.8F+0.1F;

				EntityItem entityItem = new EntityItem(world, x+offX, y+offY, z+offZ, is.copy());
				entityItem.motionX = world.rand.nextGaussian()*0.05F;
				entityItem.motionY = world.rand.nextGaussian()*0.05F+0.2F;
				entityItem.motionZ = world.rand.nextGaussian()*0.05F;
				world.spawnEntityInWorld(entityItem);
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
}
