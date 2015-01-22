package chylex.hee.block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.mechanics.misc.TempleEvents;
import chylex.hee.system.util.DragonUtil;

public class BlockTempleEndPortal extends BlockEndPortal{
	public enum Status{ ACTIVE, INACTIVE }
	public static final PropertyEnumSimple STATUS = PropertyEnumSimple.create("status",Status.class);
	
	public BlockTempleEndPortal(){
		super(Material.portal);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return meta >= 0 && meta < Status.values().length ? getDefaultState().withProperty(STATUS,Status.values()[meta]) : getDefaultState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return ((Enum)state.getValue(STATUS)).ordinal();
	}
	
	@Override
	protected BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ STATUS });
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity){
		if (world.getBlockState(pos).getValue(STATUS) == Status.INACTIVE)return;
		
		if (!world.isRemote && entity.ridingEntity == null && entity.riddenByEntity == null && entity instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP)entity;
			TempleEvents.attemptDestroyTemple(player);
			DragonUtil.teleportToOverworld(player);
		}
	}
}
