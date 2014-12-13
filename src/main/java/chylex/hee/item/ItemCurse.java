package chylex.hee.item;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.mechanics.curse.CurseType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCurse extends Item{
	public ItemCurse(){
		setHasSubtypes(true);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Block block = world.getBlock(x,y,z);

		if (block == Blocks.snow_layer && (world.getBlockMetadata(x,y,z)&7) < 1)side = 1;
		else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world,x,y,z)){
			switch(side){
				case 0: --y; break;
				case 1: ++y; break;
				case 2: --z; break;
				case 3: ++z; break;
				case 4: --x; break;
				case 5: ++x; break;
			}
		}

		if (is.stackSize == 0)return false;
		else{
			
			// TODO
			return true;
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote); // TODO
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		return is;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		CurseType type = CurseType.getFromDamage(is.getItemDamage());
		return "item.curse."+(type == null ? "invalid" : type.name().toLowerCase().replaceAll("_",""));
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if ((is.getItemDamage()&0b100000000) != 0)textLines.add(EnumChatFormatting.YELLOW+StatCollector.translateToLocal("item.curse.eternal"));
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list){
		
	}
}
