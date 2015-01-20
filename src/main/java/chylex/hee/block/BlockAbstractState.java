package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public abstract class BlockAbstractState extends Block{
	public BlockAbstractState(Material material){
		super(material);
	}
	
	/**
	 * Sets block property, applicable only if there is exactly one property available.
	 */
	public abstract IBlockState setProperty(Comparable value);
	
	/**
	 * Sets block property using default state.
	 */
	public abstract IBlockState setProperty(IProperty property, Comparable value);
	
	@Override public abstract IBlockState getStateFromMeta(int meta);
	@Override public abstract int getMetaFromState(IBlockState state);
	@Override protected abstract BlockState createBlockState();
}
