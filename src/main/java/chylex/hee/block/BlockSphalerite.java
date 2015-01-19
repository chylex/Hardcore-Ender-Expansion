package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.item.ItemList;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.util.MathUtil;

public class BlockSphalerite extends Block implements IBlockSubtypes{
	public BlockSphalerite(){
		super(Material.rock);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune){
		if (meta == 1){
			ArrayList<ItemStack> items = new ArrayList<>();
			items.add(new ItemStack(BlockList.sphalerite,1,0));
			items.add(new ItemStack(ItemList.stardust,1+world.rand.nextInt(3+MathUtil.ceil(fortune*0.49D)),0));
			return items;
		}
		else return super.getDrops(world,x,y,z,meta,fortune);
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
