package chylex.hee.render.entity;
import java.util.Random;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.entity.layer.LayerEndermanEyes;
import chylex.hee.render.entity.layer.LayerEndermanHeldItem;
import chylex.hee.render.model.ModelBaconmanHead;

@SideOnly(Side.CLIENT)
public abstract class AbstractRenderMobEnderman extends RenderLiving{
	protected static final ResourceLocation texEndermanEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	protected static final ResourceLocation texEndermanBody = new ResourceLocation("textures/entity/enderman/enderman.png");

	protected final ModelEnderman endermanModel;
	protected final Random rand = new Random();

	public AbstractRenderMobEnderman(RenderManager renderManager){
		super(renderManager,new ModelEnderman(0F),0.5F);
		endermanModel = (ModelEnderman)super.mainModel;
		if (ModCommonProxy.hardcoreEnderbacon)endermanModel.bipedHead = new ModelBaconmanHead(endermanModel,0,0);
		
		if (!ModCommonProxy.hardcoreEnderbacon)addLayer(new LayerEndermanEyes(this));
		addLayer(getHeldItemRenderer());
	}
	
	protected LayerRenderer getHeldItemRenderer(){
		return new LayerEndermanHeldItem(this);
	}
	
	protected final void superDoRender(IEndermanRenderer entity, double x, double y, double z, float yaw, float partialTickTime){
		super.doRender((EntityLiving)entity,x,y,z,yaw,partialTickTime);
	}

	public void renderEnderman(IEndermanRenderer entity, double x, double y, double z, float yaw, float partialTickTime){
		endermanModel.isCarrying = entity.isCarrying();
		endermanModel.isAttacking = entity.isScreaming();

		if (entity.isScreaming()){
			double spazzAmount = 0.02D;
			x += rand.nextGaussian()*spazzAmount;
			z += rand.nextGaussian()*spazzAmount;
		}

		superDoRender(entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return Baconizer.mobTexture(this,texEndermanBody);
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((IEndermanRenderer)entity,x,y,z,yaw,partialTickTime);
	}
}