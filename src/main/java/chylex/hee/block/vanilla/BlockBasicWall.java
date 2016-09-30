package chylex.hee.block.vanilla;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicWall extends BlockWall{
	private final Block baseBlock;
	private final byte baseMeta;
	
	public BlockBasicWall(Block baseBlock, int baseMeta){
		super(baseBlock);
		this.baseBlock = baseBlock;
		this.baseMeta = (byte)baseMeta;
	}
	
	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return baseBlock.getIcon(side, baseMeta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item));
	}
}
