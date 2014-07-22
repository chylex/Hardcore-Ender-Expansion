package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.UnlockResult;
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
		if (world.getBlockMetadata(x,y,z) == 0){
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
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,x,y,z,meta,chance,fortune);
		dropXpOnBlockBreak(world,x,y,z,MathHelper.getRandomIntegerInRange(world.rand,1,6));
	}

	@Override
	protected void onOreMined(EntityPlayer player, ArrayList<ItemStack> drops, int x, int y, int z, int meta, int fortune){
		if (KnowledgeRegistrations.STARDUST_ORE.tryUnlockFragment(player,0.05F).stopTrying)return;
		if (KnowledgeRegistrations.STARDUST.tryUnlockFragment(player,0.04F,new byte[]{ 0 }) == UnlockResult.NOTHING_TO_UNLOCK){
			if (player.worldObj.rand.nextBoolean())KnowledgeRegistrations.DECOMPOSITION_TABLE.tryUnlockFragment(player,0.03F,new byte[]{ 0,1 });
			else KnowledgeRegistrations.ENERGY_EXTRACTION_TABLE.tryUnlockFragment(player,0.03F,new byte[]{ 0,1 });
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side){
		int meta = world.getBlockMetadata(x,y,z);
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
