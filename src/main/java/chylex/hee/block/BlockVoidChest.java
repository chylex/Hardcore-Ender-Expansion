package chylex.hee.block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.tileentity.TileEntityVoidChest;

public class BlockVoidChest extends BlockContainer{
	public BlockVoidChest(){
		super(Material.rock);
		setBlockBounds(0.0625F,0F,0.0625F,0.9375F,0.875F,0.9375F);
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
		byte meta = 0;
		
		switch(MathHelper.floor_double(entity.rotationYaw/90F+0.5D)&3){
			case 0: meta = 2; break;
			case 1: meta = 5; break;
			case 2: meta = 3; break;
			case 3: meta = 4; break;
		}

		world.setBlockMetadataWithNotify(x,y,z,meta,2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (!world.isRemote && !world.getBlockState(pos.up()).getBlock().isNormalCube() && world.getTileEntity(pos) instanceof TileEntityVoidChest){
			player.openGui(HardcoreEnderExpansion.instance,6,world,pos.getX(),pos.getY(),pos.getZ());
		}
		
		return true;
	}
}
