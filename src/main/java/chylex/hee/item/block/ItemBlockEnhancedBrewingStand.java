package chylex.hee.item.block;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockEnhancedBrewingStand extends ItemReed{
	public ItemBlockEnhancedBrewingStand(){
		super(BlockList.enhanced_brewing_stand);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return !EnhancementRegistry.getEnhancementList(is).isEmpty();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementRegistry.getEnhancementList(is).addTooltip(textLines,EnumChatFormatting.YELLOW);
	}
}
