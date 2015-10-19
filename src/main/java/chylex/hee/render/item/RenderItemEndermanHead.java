package chylex.hee.render.item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemEndermanHead implements IItemRenderer{
	public static boolean isRenderingArmor;
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type){
		return isRenderingArmor;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object...data){
		isRenderingArmor = false;
	}
}
