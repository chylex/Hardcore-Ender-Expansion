package chylex.hee.item.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockEnhanceableTile extends ItemBlock{
	public ItemBlockEnhanceableTile(Block block){
		super(block);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state){
		ItemStack isCopy = is.copy();
		
		if (super.placeBlockAt(is,player,world,pos,side,hitX,hitY,hitZ,state)){
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
	public boolean hasEffect(ItemStack is, int pass){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementHandler.appendEnhancementNames(is,textLines);
	}
}
