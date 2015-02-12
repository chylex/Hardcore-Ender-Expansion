package chylex.hee.item.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
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
			
			if (tile != null){
				tile.getEnhancements().clear();
				tile.getEnhancements().addAll(EnhancementHandler.getEnhancements(is));
			}
			
			return true;
		}
		else return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementHandler.appendEnhancementNames(is,textLines);
	}
}
