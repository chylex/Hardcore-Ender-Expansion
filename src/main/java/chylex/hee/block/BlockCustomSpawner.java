package chylex.hee.block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlockCustomSpawner extends BlockMobSpawner{
	public enum Type{ TOWER_ENDERMAN, SILVERFISH_DUNGEON, LOUSE_RAVAGED, SILVERFISH_RAVAGED, BLOB_ENDERMAN }
	public static final PropertyEnumSimple TYPE = PropertyEnumSimple.create("type",Type.class);
	
	public static IBlockState createSpawner(Type type){
		return BlockList.custom_spawner.getDefaultState().withProperty(TYPE,type);
	}
	
	public BlockCustomSpawner(){
		disableStats();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return meta >= 0 && meta < Type.values().length ? getDefaultState().withProperty(TYPE,Type.values()[meta]) : getDefaultState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return ((Enum)state.getValue(TYPE)).ordinal();
	}
	
	@Override
	protected BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ TYPE });
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state){
		return new TileEntityCustomSpawner().setLogicId(getMetaFromState(state));
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if (!world.isRemote){
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)world.getTileEntity(pos);
			if (spawner != null)spawner.getSpawnerLogic().onBlockBreak();
		}
		
		super.breakBlock(world,pos,state);
	}
}
