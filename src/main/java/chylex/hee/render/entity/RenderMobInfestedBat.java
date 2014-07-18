package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobInfestedBat extends RenderBat{
    private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/entity/bat_infested.png");
    
	public RenderMobInfestedBat(){
		super();
	}
	
	@Override
	protected void preRenderCallback(EntityBat bat, float partialTickTime){
		float scale = ((EntityMobInfestedBat)bat).getScale();
		GL11.glScalef(scale,scale,scale);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return tex;
	}
}
