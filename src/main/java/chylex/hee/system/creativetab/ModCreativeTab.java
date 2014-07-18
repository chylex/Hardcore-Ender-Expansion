package chylex.hee.system.creativetab;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs{
	public static ModCreativeTab tab;
	
	public static void registerTab(){
		tab = new ModCreativeTab();
	}
	
	public ModCreativeTab(){
		super("tabHardcoreEnderExpansion");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel(){
		return "Hardcore Ender Expansion";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return ItemList.essence;
	}
	
	@Override
	public void displayAllReleventItems(List list){
		for(Block block:BlockList.tabOrderedList.getBlocks()){
			block.getSubBlocks(Item.getItemFromBlock(block),this,list);
		}
		
		for(Item item:ItemList.tabOrderedList.getItems()){
			item.getSubItems(item,this,list);
		}
	}
}
