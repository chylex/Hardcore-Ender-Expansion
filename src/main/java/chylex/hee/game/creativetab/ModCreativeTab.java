package chylex.hee.game.creativetab;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs{
	public static final ModCreativeTab tabMain = new ModCreativeTab(0);
	public static final ModCreativeTab tabCharms = new ModCreativeTab(1);

	@SideOnly(Side.CLIENT)
	public static void setupTabsClient(){
		tabMain.list.setupClient();
		tabCharms.list.setupClient();
	}
	
	private final byte type;
	public final CreativeTabItemList list;
	
	public ModCreativeTab(int type){
		super("tabHardcoreEnderExpansion");
		this.type = (byte)type;
		this.list = new CreativeTabItemList(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel(){
		return ModCommonProxy.hardcoreEnderbacon ?
			   (type == 0 ? "Hardcore Bacon Expansion" : "Hardcore Bacon Expansion - Charms") :
			   (type == 0 ? "Hardcore Ender Expansion" : "Hardcore Ender Expansion - Charms");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return type == 0 ? ItemList.essence : ItemList.charm_pouch;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List targetList){
		targetList.addAll(list.getAllItems());
	}
}
