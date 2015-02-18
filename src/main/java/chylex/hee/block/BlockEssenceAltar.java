package chylex.hee.block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import chylex.hee.block.state.BlockAbstractContainerStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class BlockEssenceAltar extends BlockAbstractContainerStateEnum{
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",EssenceType.class);
	
	private static final float hitCenter1 = 0.09F, hitCenter2 = 0.9F, hitDist = 0.05F;
	
	public BlockEssenceAltar(){
		super(Material.iron);
		setBlockBounds(0.0F,0.0F,0.0F,1.0F,0.75F,1.0F);
		createSimpleMeta(VARIANT,EssenceType.class);
	}
	
	@Override
	public IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote)return true;
		
		ItemStack held = player.getHeldItem();
		if (held != null && held.getItem() == ItemList.end_powder)return false;
		
		TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(pos);
		
		if (altar != null){
			if (side == EnumFacing.UP){
				if (hitX >= hitCenter1-hitDist && hitX <= hitCenter1+hitDist && hitZ >= hitCenter1-hitDist && hitZ <= hitCenter1+hitDist)altar.onSocketClick(player,3);
				else if (hitX >= hitCenter1-hitDist && hitX <= hitCenter1+hitDist && hitZ >= hitCenter2-hitDist && hitZ <= hitCenter2+hitDist)altar.onSocketClick(player,2);
				else if (hitX >= hitCenter2-hitDist && hitX <= hitCenter2+hitDist && hitZ >= hitCenter2-hitDist && hitZ <= hitCenter2+hitDist)altar.onSocketClick(player,1);
				else if (hitX >= hitCenter2-hitDist && hitX <= hitCenter2+hitDist && hitZ >= hitCenter1-hitDist && hitZ <= hitCenter1+hitDist)altar.onSocketClick(player,0);
				else altar.onRightClick(player);
				return true;
			}
			else altar.onRightClick(player);
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if (world.isRemote)return;
		
		TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(pos);
		if (altar != null)altar.onBlockDestroy();
		super.breakBlock(world,pos,state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEssenceAltar();
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean isFullCube(){
		return false;
	}
}
