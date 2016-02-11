package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import chylex.hee.entity.projectile.EntityProjectileCurse;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileCurse extends RenderProjectileBase{
	@Override
	protected void render(Entity entity){
		EntityProjectileCurse curse = (EntityProjectileCurse)entity;
		CurseType type = curse.getType();
		if (type == null)return;
		
		int col = type.getColor(0);
		GL.color(((col>>16)&255)/255F,((col>>8)&255)/255F,(col&255)/255F);
		renderIcon(Tessellator.instance,ItemList.curse.getIconFromDamageForRenderPass(type.damage,0));
		
		col = type.getColor(1);
		GL.color(((col>>16)&255)/255F,((col>>8)&255)/255F,(col&255)/255F);
		renderIcon(Tessellator.instance,ItemList.curse.getIconFromDamageForRenderPass(type.damage,1));
	}
}