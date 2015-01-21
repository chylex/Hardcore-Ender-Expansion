package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.render.entity.layer.LayerLouseTop;
import chylex.hee.render.model.ModelLouse;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAttribute;

@SideOnly(Side.CLIENT)
public class RenderMobLouse extends RenderLiving{
	private static final ResourceLocation texLouse = new ResourceLocation("hardcoreenderexpansion:textures/entity/louse.png");

	public RenderMobLouse(RenderManager renderManager){
		super(renderManager,new ModelLouse(),0.4F);
		addLayer(new LayerLouseTop(this,0));
		addLayer(new LayerLouseTop(this,1));
	}
	
	public enum RuneColor{
		RED(0.4922F,0F,0F), YELLOW(0.4922F,0.4922F,0F), GREEN(0F,0.4922F,0F), BLUE(0F,0.369F,0.4922F), PURPLE(0.327F,0F,0.4922F), GRAY(0.4922F,0.4922F,0.4922F);
		
		public final float red, green, blue;
		
		RuneColor(float red, float green, float blue){
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}
	
	public RuneColor getColorFromAttribute(EnumLouseAttribute attribute){
		switch(attribute){
			case ATTACK: return RuneColor.RED;
			case SPEED: return RuneColor.YELLOW;
			case HEALTH: return RuneColor.GREEN;
			case ARMOR: return RuneColor.BLUE;
			case TELEPORT: return RuneColor.PURPLE;
			default: return RuneColor.RED;
		}
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(0.6F,0.6F,0.6F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texLouse;
	}
}
