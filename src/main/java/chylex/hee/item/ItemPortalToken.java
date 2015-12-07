package chylex.hee.item;
import java.util.List;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.global.WorldFile;
import chylex.hee.init.ItemList;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPortalToken extends Item{
	public static final ItemStack forTerritory(EndTerritory territory, boolean rare){
		ItemStack is = new ItemStack(ItemList.portal_token,1,rare ? 1 : 0);
		ItemUtil.getTagRoot(is,true).setByte("territory",(byte)territory.ordinal());
		return is;
	}
	
	public static final @Nullable EndTerritory getTerritory(ItemStack is){
		return CollectionUtil.get(EndTerritory.values,ItemUtil.getTagRoot(is,false).getByte("territory")).orElse(null);
	}
	
	public static final boolean isRare(ItemStack is){
		return is.getItemDamage() == 1;
	}
	
	public static final OptionalInt getIndex(ItemStack is){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		if (nbt.hasKey("tindex"))return OptionalInt.of(nbt.getInteger("tindex"));
		
		EndTerritory territory = getTerritory(is);
		if (territory == null)return OptionalInt.empty();
		
		int index = SaveData.global(WorldFile.class).increment(territory);
		nbt.setInteger("tindex",index);
		return OptionalInt.of(index);
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon iconInscription, iconRareOverlay;
	
	public ItemPortalToken(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return isRare(is) ? getUnlocalizedName()+".rare" : getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(I18n.format("territory."+ItemUtil.getTagRoot(is,false).getByte("territory")));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(EndTerritory territory:EndTerritory.values){
			list.add(forTerritory(territory,false));
			list.add(forTerritory(territory,true));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int pass){
		return pass == 0 ? itemIcon : pass == 1 ? iconInscription : iconRareOverlay;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 0){
			EndTerritory territory = getTerritory(is);
			if (territory != null)return territory.tokenColor;
		}
		
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int damage){
		return damage == 1 ? 3 : 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		itemIcon = iconRegister.registerIcon(iconString+"_back");
		iconInscription = iconRegister.registerIcon(iconString+"_inscription");
		iconRareOverlay = iconRegister.registerIcon(iconString+"_rare");
	}
}
