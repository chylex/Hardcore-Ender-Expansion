package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRavagedBrick extends Block implements IBlockSubtypes{
	public static byte metaNormal = 0, metaCracked = 1, metaDamaged1 = 2, metaDamaged2 = 3, metaDamaged3 = 4, metaDamaged4 = 5, metaAmount = 6;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockRavagedBrick(){
		super(Material.rock);
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z){
		if (world.provider.getDimensionId() != 1 || !world.isRemote)return blockHardness; // only run on client side
		
		List<TileEntity> list = world.loadedTileEntityList;
		int spawnerCount = 0;
		
		for(TileEntity tile:list){
			if (tile.blockType == BlockList.custom_spawner && MathUtil.distance(x-tile.xCoord,z-tile.zCoord) < 260)++spawnerCount;
		}
		
		if (spawnerCount > 24)world.spawnParticle("reddust",x-0.2D+world.rand.nextDouble()*1.4D,y-0.2D+world.rand.nextDouble()*1.4D,z-0.2D+world.rand.nextDouble()*1.4D,1D,0.2D,0.2D);
		
		return spawnerCount <= 24 ? blockHardness : blockHardness+(3F+(float)Math.pow(spawnerCount-24,0.8D)*1.5F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < metaAmount; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta >= metaDamaged1 && meta <= metaDamaged4 && (side == 0 || side == 1) ? iconArray[0] : iconArray[meta < metaAmount ? meta : 0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[metaAmount];
		iconArray[0] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick");
		iconArray[1] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_cracked");
		iconArray[2] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_1");
		iconArray[3] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_2");
		iconArray[4] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_3");
		iconArray[5] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_4");
	}
}
