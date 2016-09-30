package chylex.hee.render.projectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileItem3D extends Render{
	private final Item item;
	private final ItemStack is;
	
	public RenderProjectileItem3D(Item item){
		this.item = item;
		this.is = new ItemStack(item);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		bindEntityTexture(entity);
		TextureUtil.func_152777_a(false, false, 1F);
		
		GL.pushMatrix();
		GL.translate(x, y+getBob(entity, partialTickTime), z);
		GL.enableRescaleNormal();
		
		GL.scale(0.5F, 0.5F, 0.5F);
		
		if (item.requiresMultipleRenderPasses()){
			for(int pass = 0; pass < item.getRenderPasses(is.getItemDamage()); pass++){
				int color = item.getColorFromItemStack(is, pass);
				GL.color((color>>16&255)/255F, (color>>8&255)/255F, (color&255)/255F, 1F);
				renderDroppedItem(entity, item.getIcon(is, pass), partialTickTime, (color>>16&255)/255F, (color>>8&255)/255F, (color&255)/255F, pass);
			}
		}
		else{
			int color = item.getColorFromItemStack(is, 0);
			renderDroppedItem(entity, is.getIconIndex(), partialTickTime, (color>>16&255)/255F, (color>>8&255)/255F, (color&255)/255F, 0);
		}
		
		GL.disableRescaleNormal();
		GL.popMatrix();
		bindEntityTexture(entity);
		TextureUtil.func_147945_b();
	}
	
	private void renderDroppedItem(Entity entity, IIcon icon, float partialTickTime, float red, float green, float blue, int pass){
		Tessellator tessellator = Tessellator.instance;
		
		if (icon == null){
			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			icon = ((TextureMap)textureManager.getTexture(textureManager.getResourceLocation(is.getItemSpriteNumber()))).getAtlasSprite("missingno");
		}
		
		GL.pushMatrix();
		GL.rotate(getRotation(entity, partialTickTime), 0F, 1F, 0F);
		
		float unitSize = 0.0625F;
		float offset = 0.021875F;
		
		GL.translate(-0.5F, -0.25F, -(unitSize+offset)/2F);
		GL.translate(0F, 0F, unitSize+offset);
		
		bindTexture(TextureMap.locationItemsTexture);
		
		GL.color(red, green, blue, 1F);
		ItemRenderer.renderItemIn2D(tessellator, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), unitSize);
		
		GL.popMatrix();
	}
	
	protected float getBob(Entity entity, float partialTickTime){
		return 0F;
	}
	
	protected float getRotation(Entity entity, float partialTickTime){
		return 0F;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationItemsTexture;
	}
}
