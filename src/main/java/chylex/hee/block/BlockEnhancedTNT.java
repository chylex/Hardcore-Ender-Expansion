package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.tileentity.TileEntityEnhancedTNT;

public class BlockEnhancedTNT extends BlockAbstractEnhanceable{
	public BlockEnhancedTNT(){
		super(Material.tnt);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnhancedTNT();
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		super.onBlockAdded(world,pos,state);
		if (world.isBlockIndirectlyGettingPowered(pos) > 0)world.scheduleUpdate(pos,this,1);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		tryIgniteTNT(world,pos,true,null);
		world.setBlockToAir(pos);
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor){
		if (world.isBlockIndirectlyGettingPowered(pos) > 0){
			tryIgniteTNT(world,pos,true,null);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public int quantityDropped(Random rand){
		return 1;
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion){
		if (!world.isRemote){
			TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(pos);
			
			if (tile != null){
				EntityBlockEnhancedTNTPrimed tnt = new EntityBlockEnhancedTNTPrimed(world,pos.getX()+0.5F,pos.getY()+0.5F,pos.getZ()+0.5F,explosion.getExplosivePlacedBy(),tile.getEnhancements());
				tnt.fuse = tile.getEnhancements().contains(TNTEnhancements.NO_FUSE) ? 1 : world.rand.nextInt(tnt.fuse/4)+tnt.fuse/8;
				world.spawnEntityInWorld(tnt);
			}
		}
		
		super.onBlockExploded(world,pos,explosion);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		return !player.capabilities.isCreativeMode && tryIgniteTNT(world,pos,false,null) ? false : super.removedByPlayer(world,pos,player,willHarvest);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel){
			tryIgniteTNT(world,pos,true,player);
			world.setBlockToAir(pos);
			player.getCurrentEquippedItem().damageItem(1,player);
			return true;
		}
		else return super.onBlockActivated(world,pos,state,player,side,hitX,hitY,hitZ);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
		if (!world.isRemote && entity instanceof EntityArrow){
			EntityArrow arrow = (EntityArrow)entity;

			if (arrow.isBurning()){
				tryIgniteTNT(world,pos,true,arrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)arrow.shootingEntity : null);
				world.setBlockToAir(pos);
			}
		}
	}
	
	private boolean tryIgniteTNT(World world, BlockPos pos, boolean ignite, EntityLivingBase igniter){
		if (!world.isRemote){
			TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(pos);
			
			if (tile != null && (ignite || tile.getEnhancements().contains(TNTEnhancements.TRAP))){
				EntityBlockEnhancedTNTPrimed tnt = new EntityBlockEnhancedTNTPrimed(world,pos.getX()+0.5F,pos.getY()+0.5F,pos.getZ()+0.5F,igniter,tile.getEnhancements());
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
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> drops = new ArrayList<>(1);
		TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(pos);
		
		if (tile == null)drops.add(new ItemStack(Blocks.tnt));
		else{
			ItemStack is = new ItemStack(BlockList.enhanced_tnt);
			for(Enum enhancement:tile.getEnhancements())EnhancementHandler.addEnhancement(is,enhancement);
			drops.add(is);
		}
		
		return drops;
	}
}
