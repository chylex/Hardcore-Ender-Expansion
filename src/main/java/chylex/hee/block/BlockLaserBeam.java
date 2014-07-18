package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.tileentity.TileEntityLaserBeam;

public class BlockLaserBeam extends BlockContainer{
	public static final Material laserBeam = new MaterialLaserBeam();
	
	public BlockLaserBeam(){
		super(laserBeam);
		setBlockBounds(0F,0F,0F,0F,0F,0F);
	}
	
	@Override
	public Item getItemDropped(int metadata, Random rand, int fortune){
		return null;
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		return null;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity.isImmuneToFire())return;
		entity.setFire(1);
		entity.attackEntityFrom(DamageSource.magic,ModCommonProxy.opMobs?4F:2F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityLaserBeam();
	}
}
