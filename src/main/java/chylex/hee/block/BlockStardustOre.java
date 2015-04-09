package chylex.hee.block;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStardustOre extends BlockAbstractOre{
	private static final byte iconAmount = 16;
	private static final byte[][] iconIndexes = new byte[6][16];
	
	static{
		Random rand = new Random(69);
		
		for(int side = 0; side < 6; side++){
			for(int meta = 0; meta < 16; meta++){
				iconIndexes[side][meta] = (byte)rand.nextInt(iconAmount);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		if (BlockPosM.tmp(x,y,z).getMetadata(world) == 0){
			world.setBlockMetadataWithNotify(x,y,z,world.rand.nextInt(15)+1,3);
		}
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.stardust;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return rand.nextInt(3)+rand.nextInt(3);
	}
	
	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune){
		return MathHelper.getRandomIntegerInRange(BlockList.blockRandom,1,6);
	}
	
	@Override
	protected int getCausatumLevel(){
		return 4;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
		if (meta == 0)return Blocks.end_stone.getIcon(world,x,y,z,side);
		
		return iconArray[iconIndexes[side][meta]];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[iconIndexes[side][meta]];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		String s = getTextureName()+"_";
		iconArray = new IIcon[iconAmount];
		for(int a = 0; a < iconAmount; a++)iconArray[a] = iconRegister.registerIcon(s+(a+1));
	}
}
