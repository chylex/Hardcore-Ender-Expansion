package chylex.hee.mechanics.knowledge.data.renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityRenderer implements IRegistrationRenderer{
	private final Class<? extends EntityLivingBase> entityClass;
	private final float scale;
	private float rotation = -25F, yOffset = 0F;
	private IEntityInitializer entityInitializer;
	private String customTooltip;
	
	private EntityLivingBase entityCache;
	
	public EntityRenderer(Class<? extends EntityLivingBase> entityClass){
		this.entityClass = entityClass;
		this.scale = 1F;
	}
	
	public EntityRenderer(Class<? extends EntityLivingBase> entityClass, float scale){
		this.entityClass = entityClass;
		this.scale = scale;
	}
	
	@Override
	public IRegistrationRenderer setTooltip(String tooltip){
		this.customTooltip = tooltip;
		return this;
	}
	
	public EntityRenderer setRotation(float rotation){
		this.rotation = rotation;
		return this;
	}
	
	public EntityRenderer setYOffset(float yOffset){
		this.yOffset = yOffset;
		return this;
	}
	
	public EntityRenderer setEntityInitializer(IEntityInitializer entityInitializer){
		this.entityInitializer = entityInitializer;
		return this;
	}
	
	@Override
	public String getTooltip(){
		return customTooltip == null?getEntity().getCommandSenderName():customTooltip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(Minecraft mc, int x, int y){
		EntityLivingBase e = getEntity();
		if (e == null)return;
		
		e.setWorld(mc.theWorld);
		
		float scale = 4F+this.scale*8F;
		
		GL11.glPushMatrix();
		GL11.glColor3f(1F,1F,1F);
		GL11.glEnable(32826);
		GL11.glEnable(2903);
		GL11.glPushMatrix();
		GL11.glTranslatef(x+8,y+17-yOffset,50F);
		GL11.glScalef(scale,scale,scale);
		GL11.glRotatef(180F,0F,0F,1F);
		GL11.glRotatef(rotation,0F,1F,0F);
		RenderHelper.enableStandardItemLighting();
		RenderManager.instance.renderEntityWithPosYaw(e,0D,0D,0D,e.rotationYawHead = 0F,0F);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
		GL11.glDisable(32826);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896);
		GL11.glPopMatrix();
		
		// 32826 = GL_RESCALE_NORMAL_EXT
		// 2903 = GL_COLOR_MATERIAL
		// 2896 = GL_LIGHTING
	}
	
	private EntityLivingBase getEntity(){
		if (entityCache != null)return entityCache;
		
		try{
			entityCache = entityClass.getConstructor(World.class).newInstance((World)null);
			if (entityInitializer != null)entityInitializer.spawnEntity(entityCache);
		}catch(Exception ex){}
		
		return entityCache;
	}
	
	public static interface IEntityInitializer{
		void spawnEntity(EntityLivingBase entity);
	}
}
