package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.tileentity.TileEntityEnhancedTNT;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnhancedTNT extends BlockAbstractEnhanceable{
	@SideOnly(Side.CLIENT)
	private IIcon iconTop;
	@SideOnly(Side.CLIENT)
	private IIcon iconBottom;

	public BlockEnhancedTNT(){
		super(Material.tnt);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnhancedTNT();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		super.onBlockAdded(world,x,y,z);
		if (world.isBlockIndirectlyGettingPowered(x,y,z))world.scheduleBlockUpdate(x,y,z,this,1);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		tryIgniteTNT(world,x,y,z,true,null);
		world.setBlockToAir(x,y,z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor){
		if (world.isBlockIndirectlyGettingPowered(x,y,z)){
			tryIgniteTNT(world,x,y,z,true,null);
			world.setBlockToAir(x,y,z);
		}
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion){
		if (!world.isRemote){
			TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(x,y,z);
			
			if (tile != null){
				EntityBlockEnhancedTNTPrimed tnt = new EntityBlockEnhancedTNTPrimed(world,x+0.5F,y+0.5F,z+0.5F,explosion.getExplosivePlacedBy(),tile.getEnhancements());
				tnt.fuse = tile.getEnhancements().contains(TNTEnhancements.NO_FUSE) ? 1 : world.rand.nextInt(tnt.fuse/4)+tnt.fuse/8;
				world.spawnEntityInWorld(tnt);
			}
		}
		
		super.onBlockExploded(world,x,y,z,explosion);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest){
		if (!player.capabilities.isCreativeMode)tryIgniteTNT(world,x,y,z,false,null);
		return super.removedByPlayer(world,player,x,y,z,willHarvest);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ){
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
			tryIgniteTNT(world,x,y,z,true,player);
			world.setBlockToAir(x,y,z);
			player.getCurrentEquippedItem().damageItem(1,player);
			return true;
		}
		else return super.onBlockActivated(world,x,y,z,player,meta,hitX,hitY,hitZ);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (!world.isRemote && entity instanceof EntityArrow){
			EntityArrow arrow = (EntityArrow)entity;

			if (arrow.isBurning()){
				tryIgniteTNT(world,x,y,z,true,arrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)arrow.shootingEntity : null);
				world.setBlockToAir(x,y,z);
			}
		}
	}
	
	private boolean tryIgniteTNT(World world, int x, int y, int z, boolean ignite, EntityLivingBase igniter){
		if (!world.isRemote){
			TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(x,y,z);
			
			if (tile != null && (ignite || tile.getEnhancements().contains(TNTEnhancements.TRAP))){
				EntityBlockEnhancedTNTPrimed tnt = new EntityBlockEnhancedTNTPrimed(world,x+0.5F,y+0.5F,z+0.5F,igniter,tile.getEnhancements());
				world.spawnEntityInWorld(tnt);
				world.playSoundAtEntity(tnt,"game.tnt.primed",1F,1F);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean canDropFromExplosion(Explosion explosion){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 0 ? iconBottom : (side == 1 ? iconTop : blockIcon);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon(getTextureName()+"_side");
		iconTop = iconRegister.registerIcon(getTextureName()+"_top");
		iconBottom = iconRegister.registerIcon(getTextureName()+"_bottom");
	}
}
