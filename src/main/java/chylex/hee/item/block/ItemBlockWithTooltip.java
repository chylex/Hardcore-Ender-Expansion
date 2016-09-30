package chylex.hee.item.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockWithTooltip extends ItemBlock{
	public ItemBlockWithTooltip(Block block){
		super(block);
		if (!(block instanceof IHaveTooltip))throw new IllegalArgumentException("Blocks passed to ItemBlockWithTooltip need to implement the IHaveTooltip interface!");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		((IHaveTooltip)field_150939_a).addTooltip(is, player, textLines, showAdvancedInfo);
	}
	
	public static interface IHaveTooltip{
		void addTooltip(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo);
	}
}
