package chylex.hee.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil{
	public static TextureAtlasSprite getIcon(Item item){
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item);
	}
	
	private RenderUtil(){}
}
