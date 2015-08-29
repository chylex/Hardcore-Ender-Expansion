package chylex.hee.block.vanilla;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.item.block.ItemBlockSlab.IBlockSlab;
import chylex.hee.system.abstractions.BlockInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicSlab extends BlockSlab implements IBlockSlab{
	private final BlockInfo full;
	
	public BlockBasicSlab(BlockInfo fullBlockInfo){
		super(false,fullBlockInfo.block.getMaterial());
		this.full = fullBlockInfo;
		this.useNeighborBrightness = true;
		setHardness(full.block.blockHardness*0.5F);
		setResistance(full.block.blockResistance*0.5F);
        setStepSound(full.block.stepSound);
	}
	
	public BlockBasicSlab(Block fullBlock){
		this(new BlockInfo(fullBlock));
	}
	
	public BlockBasicSlab(Block fullBlock, int fullMeta){
		this(new BlockInfo(fullBlock,fullMeta));
	}
	
	@Override
	public String func_150002_b(int meta){
		return getUnlocalizedName();
	}

	@Override
	public BlockInfo getFullBlock(){
		return full;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	protected ItemStack createStackedBlock(int meta){
		return new ItemStack(Item.getItemFromBlock(this),1,0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return full.block.getIcon(side,full.meta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){}
}