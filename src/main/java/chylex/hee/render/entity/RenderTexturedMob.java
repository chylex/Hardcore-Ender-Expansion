package chylex.hee.render.entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTexturedMob extends RenderLiving{
	private final ResourceLocation tex;
	private final float scale;
	
	public RenderTexturedMob(ModelBase model, float shadowSize, String texture, float scale){
		super(model,shadowSize);
		this.tex = new ResourceLocation("hardcoreenderexpansion:textures/entity/"+texture);
		this.scale = scale;
	}
	
	public RenderTexturedMob(ModelBase model, float shadowSize, String texture){
		this(model,shadowSize,texture,1F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(scale,scale,scale);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return tex;
	}
}
