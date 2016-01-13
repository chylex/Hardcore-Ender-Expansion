package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import chylex.hee.init.BlockList;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRavishBell extends BlockFlower{
	private static final int variations = 3;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockRavishBell(){
		super(0);
		setBlockBounds(0.1F,0F,0.1F,0.9F,0.725F,0.9F);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z){
		return (world.getFullBlockLightValue(x,y,z) >= 8 || world.canBlockSeeTheSky(x,y,z) || world.provider.dimensionId == 1) &&
			   (Pos.at(x,y-1,z).getBlock(world).canSustainPlant(world,x,y-1,z,ForgeDirection.UP,this));
	}
	
	@Override
	public boolean canPlaceBlockOn(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || super.canPlaceBlockOn(block);
	}

	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdRavishBell;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName(){
		return textureName;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta == -1 ? blockIcon : CollectionUtil.getClamp(iconArray,meta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon(textureName+"_base");
		
		iconArray = new IIcon[variations];
		for(int a = 0; a < variations; a++)iconArray[a] = iconRegister.registerIcon(textureName+"_"+(a+1));
	}
}
