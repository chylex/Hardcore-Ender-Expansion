package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGloomtorch extends Block{
	@SideOnly(Side.CLIENT)
	private IIcon iconMiddle;
	
	public BlockGloomtorch(){
		super(Material.circuits);
		setBlockBounds(0.4375F, 0F, 0.4375F, 0.5625F, 0.53125F, 0.5625F);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return true;
	}
	
	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdGloomtorch;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z){
		float min = 0.4375F, max = 0.5625F;
		float p1 = 0.53125F, p2 = 1F-p1;
		
		switch(CollectionUtil.get(Facing6.list, Pos.at(x, y, z).getMetadata(world)-1).orElse(Facing6.DOWN_NEGY)){
			default: setBlockBounds(min, 0F, min, max, p1, max); break;
			case UP_POSY: setBlockBounds(min, p2, min, max, 1F, max); break;
			case NORTH_NEGZ: setBlockBounds(min, min, 0F, max, max, p1); break;
			case SOUTH_POSZ: setBlockBounds(min, min, p2, max, max, 1F); break;
			case WEST_NEGX: setBlockBounds(0F, min, min, p1, max, max); break;
			case EAST_POSX: setBlockBounds(p2, min, min, 1F, max, max); break;
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side){
		return canStayOn(world, x, y, z, Facing6.fromSide(side).opposite());
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		Facing6 facing = Facing6.fromSide(side).opposite();
		return canStayOn(world, x, y, z, facing) ? 1+facing.ordinal() : 0;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock){
		if (!canStayOn(world, x, y, z, CollectionUtil.get(Facing6.list, Pos.at(x, y, z).getMetadata(world)-1).orElse(Facing6.DOWN_NEGY)))dropMe(world, x, y, z);
	}
	
	private boolean canStayOn(World world, int x, int y, int z, Facing6 attachedTo){
		Pos pos = Pos.at(x, y, z).offset(attachedTo);
		return (attachedTo == Facing6.DOWN_NEGY && pos.getBlock(world).canPlaceTorchOnTop(world, pos.getX(), pos.getY(), pos.getZ())) || world.isSideSolid(pos.getX(), pos.getY(), pos.getZ(), ForgeDirection.getOrientation(attachedTo.opposite().toEnumFacing().ordinal()));
	}
	
	private void dropMe(World world, int x, int y, int z){
		dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		world.setBlockToAir(x, y, z);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == CollectionUtil.get(Facing6.list, meta-1).orElse(Facing6.DOWN_NEGY).opposite().toEnumFacing().ordinal() ? iconMiddle : blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName(){
		return "hardcoreenderexpansion:gloomtorch";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		if (rand.nextBoolean())HardcoreEnderExpansion.fx.global("glitter", x+0.3D+rand.nextDouble()*0.4D, y+0.3D+rand.nextDouble()*0.4D, z+0.3D+rand.nextDouble()*0.4D, 0D, -0.002D, 0D, 0.8F, 0.8F, 0.8F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		super.registerBlockIcons(iconRegister);
		iconMiddle = iconRegister.registerIcon(textureName+"_middle");
	}
}
