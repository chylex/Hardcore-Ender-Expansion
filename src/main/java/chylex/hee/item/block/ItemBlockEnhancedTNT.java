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
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.tileentity.TileEntityEnhancedTNT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockEnhancedTNT extends ItemBlock{
	public ItemBlockEnhancedTNT(Block block){
		super(block);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state){
		ItemStack isCopy = is.copy();
		
		if (super.placeBlockAt(is,player,world,pos,side,hitX,hitY,hitZ,state)){
			TileEntityEnhancedTNT tile = (TileEntityEnhancedTNT)world.getTileEntity(pos);
			if (tile != null)tile.loadEnhancementsFromItem(isCopy);
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
