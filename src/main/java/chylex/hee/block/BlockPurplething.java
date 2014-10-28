package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPurplething extends Block implements IBlockSubtypes{
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockPurplething(){
		super(Material.cloth);
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[Math.min(iconArray.length-1,Math.max(0,meta))];
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < iconArray.length; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[16];
		iconArray[0] = iconRegister.registerIcon(textureName);
		
		String tex = textureName+"_"; // 0    1     2     3     4     5     6     7     8     9     10     11     12     13   14
		String[] names = new String[]{ "h", "v", "hl", "hr", "vb", "vt", "tl", "tr", "bl", "br", "trb", "trl", "tbl", "rbl", "x" };
		
		for(int a = 1; a < 16; a++)iconArray[a] = iconRegister.registerIcon(tex+names[a-1]);
	}
	
	public static int getConnectionMeta(LargeStructureWorld world, int x, int y, int z){
		boolean l = false, r = false, t = false, b = false;
		
		if (world.getBlock(x,y-1,z) != BlockList.purplething || world.getBlock(x,y+1,z) != BlockList.purplething){ // xz plane
			l = isConnectable(world,x-1,y,z);
			r = isConnectable(world,x+1,y,z);
			t = isConnectable(world,x,y,z-1);
			b = isConnectable(world,x,y,z+1);
		}
		else if (world.getBlock(x-1,y,z) != BlockList.purplething || world.getBlock(x+1,y,z) != BlockList.purplething){ // yz plane
			l = isConnectable(world,x,y,z+1);
			r = isConnectable(world,x,y,z-1);
			t = isConnectable(world,x,y+1,z);
			b = isConnectable(world,x,y-1,z);
		}
		else if (world.getBlock(x,y,z-1) != BlockList.purplething || world.getBlock(x,y,z+1) != BlockList.purplething){ // xy plane
			l = isConnectable(world,x+1,y,z);
			r = isConnectable(world,x-1,y,z);
			t = isConnectable(world,x,y+1,z);
			b = isConnectable(world,x,y-1,z);
		}
		
		if (l && r && t && b)return 15;
		else if (r && b && l)return 14;
		else if (t && b && l)return 13;
		else if (t && r && l)return 12;
		else if (t && r && b)return 11;
		else if (b && r)return 10;
		else if (b && l)return 9;
		else if (t && r)return 8;
		else if (t && l)return 7;
		else if (t && b)return 2;
		else if (l && r)return 1;
		else return 0;
	}
	
	public static int getEndMeta(LargeStructureWorld world, int addX, int addY, int addZ, boolean wall){
		if (wall){
			if (addY == 0)return addX == 1 || addZ == 1 ? 3 : 4;
			else return addY == -1 ? 5 : 6;
			
		}
		else{
			if (addX != 0)return addX == -1 ? 3 : 4;
			else if (addZ != 0)return addZ == 1 ? 5 : 6;
		}
		
		return 0;
	}
	
	public static boolean isConnectable(LargeStructureWorld world, int x, int y, int z){
		return world.getMetadata(x,y,z) != 0 && world.getBlock(x,y,z) == BlockList.purplething;
	}
}
