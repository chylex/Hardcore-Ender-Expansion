package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPotion;
import chylex.hee.entity.projectile.EntityProjectilePotion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectilePotion extends RenderProjectileBase{
	@Override
	protected void render(Entity entity){
		renderIcon(Tessellator.instance,((EntityProjectilePotion)entity).getPotionItem().getIconFromDamageForRenderPass(1,0));
		renderIcon(Tessellator.instance,ItemPotion.func_94589_d("bottle_splash")); // OBFUSCATED get potion icon
	}
}