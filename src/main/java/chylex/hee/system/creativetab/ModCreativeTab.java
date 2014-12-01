package chylex.hee.system.creativetab;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs{
	public static ModCreativeTab tabMain, tabCharms;
	
	public static void registerTabs(){
		tabMain = new ModCreativeTab(0);
		tabCharms = new ModCreativeTab(1);
	}
	
	private final byte type;
	public final CreativeTabItemList list = new CreativeTabItemList();
	
	public ModCreativeTab(int type){
		super("tabHardcoreEnderExpansion");
		this.type = (byte)type;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel(){
		return type == 0 ? "Hardcore Ender Expansion" : "Hardcore Ender Expansion - Charms";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return type == 0 ? ItemList.essence : ItemList.charm_pouch;
	}
	
	@Override
	public void displayAllReleventItems(List targetList){
		for(Block block:list.getBlocks()){
			block.getSubBlocks(Item.getItemFromBlock(block),this,targetList);
		}
		
		for(Item item:list.getItems()){
			item.getSubItems(item,this,targetList);
		}
	}
}
