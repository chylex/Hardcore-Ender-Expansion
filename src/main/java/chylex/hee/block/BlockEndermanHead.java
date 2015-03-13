package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityEndermanHead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndermanHead extends BlockContainer{
	public BlockEndermanHead(){
		super(Material.circuits);
		setBlockBounds(0.25F,0.0F,0.25F,0.75F,0.5F,0.75F);
	}

	@Override
	public int getRenderType(){
		return -1;
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
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z){
		switch(world.getBlockMetadata(x,y,z)&7){
			default: case 1: setBlockBounds(0.25F,0.0F,0.25F,0.75F,0.5F,0.75F); break;
			case 2: setBlockBounds(0.25F,0.25F,0.5F,0.75F,0.75F,1.0F); break;
			case 3: setBlockBounds(0.25F,0.25F,0.0F,0.75F,0.75F,0.5F); break;
			case 4: setBlockBounds(0.5F,0.25F,0.25F,1.0F,0.75F,0.75F); break;
			case 5: setBlockBounds(0.0F,0.25F,0.25F,0.5F,0.75F,0.75F);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		setBlockBoundsBasedOnState(world,x,y,z);
		return super.getCollisionBoundingBoxFromPool(world,x,y,z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is){
		world.setBlockMetadataWithNotify(x,y,z,MathHelper.floor_double((entity.rotationYaw*4F/360F)+2.5D)&3,2);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndermanHead();
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.enderman_head;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z){
		return ItemList.enderman_head;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta){
		return Blocks.soul_sand.getIcon(side,0);
	}
}
