package chylex.hee.render.projectile;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.projectile.EntityProjectileCurse;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.curse.CurseType;

@SideOnly(Side.CLIENT)
public class RenderProjectileCurse extends RenderProjectileBase{
	public RenderProjectileCurse(RenderManager renderManager, RenderItem renderItem){
		super(renderManager,renderItem,ItemList.curse);
	}
	
	@Override
	protected int getItemDamage(Entity entity){
		EntityProjectileCurse curse = (EntityProjectileCurse)entity;
		CurseType type = curse.getType();
		return type == null ? 0 : type.damage;
	}
}