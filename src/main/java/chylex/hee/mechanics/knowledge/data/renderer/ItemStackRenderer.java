package chylex.hee.mechanics.knowledge.data.renderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.GuiKnowledgeBook;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStackRenderer implements IRegistrationRenderer{
	private ItemStack isToShow;
	private String customTooltip;
	
	public ItemStackRenderer(Block block){
		this.isToShow = new ItemStack(block);
	}
	
	public ItemStackRenderer(Block block, int metadata){
		this.isToShow = new ItemStack(block,1,metadata);
	}
	
	public ItemStackRenderer(Item item){
		this.isToShow = new ItemStack(item);
	}
	
	public ItemStackRenderer(Item item, int damage){
		this.isToShow = new ItemStack(item,1,damage);
	}
	
	public ItemStackRenderer(ItemStack isToShow){
		this.isToShow = isToShow;
	}
	
	@Override
	public IRegistrationRenderer setTooltip(String tooltip){
		this.customTooltip = tooltip;
		return this;
	}
	
	@Override
	public String getTooltip(){
		return customTooltip == null?isToShow.getDisplayName():customTooltip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(Minecraft mc, int x, int y){
		GuiKnowledgeBook.renderItem.renderItemIntoGUI(mc.fontRenderer,mc.getTextureManager(),isToShow,x,y);
	}
}
