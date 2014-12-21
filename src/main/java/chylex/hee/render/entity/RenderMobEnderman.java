package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobEnderman extends RenderEnderman{
	@Override
	protected int shouldRenderPass(EntityEnderman enderman, int pass, float partialTickTime){
		if (pass != 0 || ModCommonProxy.hardcoreEnderbacon)return -1;
		else return super.shouldRenderPass(enderman,pass,partialTickTime);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityEnderman enderman){
		return Baconizer.mobTexture(this,super.getEntityTexture(enderman));
	}
}
