package chylex.hee.block;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

abstract class BlockAbstractStateInt extends BlockAbstractState{
	private IProperty metaProp;
	private int[] metaStates;
	
	public BlockAbstractStateInt(Material materialIn){
		super(materialIn);
	}
	
	protected abstract IProperty[] getPropertyArray();
	
	protected final void createSimpleMeta(IProperty property, int minValue, int maxValue){
		metaProp = property;
		metaStates = new int[maxValue-minValue+1];
		for(int a = 0; a < metaStates.length; a++)metaStates[a] = minValue+a;
		setDefaultState(blockState.getBaseState().withProperty(metaProp,metaStates[0]));
	}
	
	/*
	 * STATE HANDLING
	 */
	
	@Override
	public final IBlockState setProperty(Comparable value){
		return setProperty(metaProp,value);
	}
	
	@Override
	public final IBlockState setProperty(IProperty property, Comparable value){
		return getDefaultState().withProperty(property,value);
	}
	
	@Override
	public final IBlockState getStateFromMeta(int meta){
		return metaStates != null && meta >= 0 && meta < metaStates.length ? getDefaultState().withProperty(metaProp,metaStates[meta]) : getDefaultState();
	}
	
	@Override
	public final int getMetaFromState(IBlockState state){
		return metaStates != null ? ((Integer)state.getValue(metaProp)).intValue() : 0;
	}
	
	@Override
	protected final BlockState createBlockState(){
		return new BlockState(this,getPropertyArray());
	}
	
	/*
	 * OVERRIDES
	 */
	
	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		if (metaStates != null){
			for(int a = 0; a < metaStates.length; a++)list.add(new ItemStack(item,1,a));
		}
	}
}
