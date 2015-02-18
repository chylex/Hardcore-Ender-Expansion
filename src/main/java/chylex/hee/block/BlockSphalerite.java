package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.state.BlockAbstractStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.ItemList;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.util.MathUtil;

public class BlockSphalerite extends BlockAbstractStateEnum implements IBlockSubtypes{
	public enum Variant{ NORMAL, STARDUST }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public BlockSphalerite(){
		super(Material.rock);
		createSimpleMeta(VARIANT,Variant.class);
	}
	
	@Override
	public IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		if (state.getValue(VARIANT) == Variant.STARDUST)return CollectionUtil.newList(new ItemStack(BlockList.sphalerite,1,0),new ItemStack(ItemList.stardust,1+BlockList.blockRandom.nextInt(3+MathUtil.ceil(fortune*0.49D)),0)));
		else return super.getDrops(world,pos,state,fortune);
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 0: return "tile.sphalerite";
			case 1: return "tile.sphalerite.stardust";
			default: return "";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}
}
