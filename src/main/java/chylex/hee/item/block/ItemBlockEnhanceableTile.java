package chylex.hee.item.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockEnhanceableTile extends ItemBlock{
	public ItemBlockEnhanceableTile(Block block){
		super(block);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata){
		if (super.placeBlockAt(is,player,world,x,y,z,side,hitX,hitY,hitZ,metadata)){
			IEnhanceableTile tile = (IEnhanceableTile)world.getTileEntity(x,y,z);
			if (tile != null)tile.getEnhancements().replace(EnhancementRegistry.getEnhancementList(is));
			
			FXHelper.create("portalbig").pos(Pos.at(x,y,z)).fluctuatePos(0.65D).fluctuateMotion(0.02D).paramSingle(0.075F+world.rand.nextFloat()*0.05F).spawn(world.rand,15);
			return true;
		}
		else return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementRegistry.getEnhancementList(is).addTooltip(textLines,EnumChatFormatting.YELLOW);
	}
}
