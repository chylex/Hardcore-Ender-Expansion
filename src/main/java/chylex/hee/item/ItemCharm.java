package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCharm extends Item{
	private static final byte iconsBackAmount = 17, iconsForeAmount = 13;
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArrayBack, iconArrayFore;
	
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
    public IIcon getIconFromDamageForRenderPass(int damage, int pass){
		CharmType type = CharmType.getFromDamage(damage).getLeft();
        return pass == 0 ? iconArrayBack[type == null ? 0 : type.backIcon] : iconArrayFore[type == null ? 0 : type.foregroundIcon];
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		iconArrayBack = new IIcon[iconsBackAmount];
		iconArrayFore = new IIcon[iconsForeAmount];

		for(int a = 0; a < iconsBackAmount; a++){
			iconArrayBack[a] = iconRegister.registerIcon(iconString+"_back_"+a);
		}
		
		for(int a = 0; a < iconsForeAmount; a++){
			iconArrayFore[a] = iconRegister.registerIcon(iconString+"_fore_"+a);
		}
	}
}
