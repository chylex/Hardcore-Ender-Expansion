package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;

public class ItemCharm extends Item{
	private static final byte iconsBackAmount = 17, iconsForeAmount = 13;
	
	public ItemCharm(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		CharmType type = CharmType.getFromDamage(is.getItemDamage()).getLeft();
		return "item.charm."+(type == null ? "invalid" : type.name().toLowerCase().replaceAll("_",""));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		for(String line:CharmType.getTooltip(is.getItemDamage()).split("\\\\n"))textLines.add(line);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(CharmType charmType:CharmType.values()){
			for(CharmRecipe charmRecipe:charmType.recipes)list.add(new ItemStack(item,1,charmRecipe.id));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	public int getRenderPasses(int metadata){
		return 2;
	}
}
