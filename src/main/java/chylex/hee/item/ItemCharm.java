package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.mechanics.charms.RuneType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCharm extends Item{
	@SideOnly(Side.CLIENT)
	private static final byte iconsForeAmount = 1;
	@SideOnly(Side.CLIENT)
	private IIcon iconBack,iconDrop;
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArrayFore;
	
	public ItemCharm(){
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(RuneType runeType:RuneType.values())list.add(new ItemStack(item,1,runeType.damage));
		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		CharmType type = CharmType.getFromDamage(is.getItemDamage()).getLeft();
		return "item.charm."+(type == null ? "invalid" : type.name().toLowerCase().replaceAll("_",""));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass){
		CharmType type = pass == 2 ? CharmType.getFromDamage(damage).getLeft() : null;
        return pass == 0 ? iconBack : pass == 1 ? iconDrop : iconArrayFore[type == null ? 0 : type.foregroundIcon];
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 0)return 16777215;
		else{
			CharmType type = CharmType.getFromDamage(is.getItemDamage()).getLeft();
			return pass == 1 ? type.dropColor : type.foreColor;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	public int getRenderPasses(int metadata){
        return 3;
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		iconBack = iconRegister.registerIcon(getIconString()+"_back");
		iconDrop = iconRegister.registerIcon(getIconString()+"_drop");
		iconArrayFore = new IIcon[iconsForeAmount];
		
		for(int a = 0; a < iconsForeAmount; a++){
			iconArrayFore[a] = iconRegister.registerIcon(getIconString()+"_icon_"+a);
		}
	}
}
