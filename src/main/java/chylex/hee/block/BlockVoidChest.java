package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.tileentity.TileEntityVoidChest;

public class BlockVoidChest extends BlockContainer{
	public static final PropertyDirection FACING = PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL);
	
	public BlockVoidChest(){
		super(Material.rock);
		setBlockBounds(0.0625F,0F,0.0625F,0.9375F,0.875F,0.9375F);
		setDefaultState(blockState.getBaseState().withProperty(FACING,EnumFacing.NORTH));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing = EnumFacing.getFront(meta);
		if (facing.getAxis() == EnumFacing.Axis.Y)facing = EnumFacing.NORTH;
		return getDefaultState().withProperty(FACING,facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ FACING });
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityVoidChest();
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean isFullCube(){
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack is){
		world.setBlockState(pos,state.withProperty(FACING,entity.getHorizontalFacing().getOpposite()),2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (!world.isRemote && !world.getBlockState(pos.up()).getBlock().isNormalCube() && world.getTileEntity(pos) instanceof TileEntityVoidChest){
			player.openGui(HardcoreEnderExpansion.instance,6,world,pos.getX(),pos.getY(),pos.getZ());
		}
		
		return true;
	}
}
