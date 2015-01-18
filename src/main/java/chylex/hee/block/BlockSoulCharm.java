package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.tileentity.TileEntitySoulCharm;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSoulCharm extends Block{
	public BlockSoulCharm(){
		super(BlockLaserBeam.laserBeam);
		setBlockBounds(0.2F,0F,0.2F,0.8F,1F,0.8F);
	}

	@Override
	public boolean hasTileEntity(int meta){
		return meta == 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta){
		return new TileEntitySoulCharm();
	}

	@Override
	public int getRenderType(){
		return -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return null;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		for(int a = 0; a < 12; a++)HardcoreEnderExpansion.fx.soulCharm(world,x,y,z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}
}
