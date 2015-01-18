package chylex.hee.render.projectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
abstract class RenderProjectileBase extends Render{
	protected final RenderItem renderItem;
	
	public RenderProjectileBase(RenderManager renderManager, RenderItem renderItem){
		super(renderManager);
		this.renderItem = renderItem;
	}
	
	protected abstract void render(Entity entity);
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F,0.5F,0.5F);
		
		GL11.glRotatef(180F-renderManager.playerViewY,0F,1F,0F);
		GL11.glRotatef(-renderManager.playerViewX,1F,0F,0F);
		
		bindEntityTexture(entity);
		render(entity);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	@Override
	protected final ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationBlocksTexture;
	}
}
