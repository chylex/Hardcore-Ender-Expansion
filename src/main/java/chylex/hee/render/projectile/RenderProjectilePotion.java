package chylex.hee.render.projectile;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.projectile.EntityProjectilePotion;

@SideOnly(Side.CLIENT)
public class RenderProjectilePotion extends RenderProjectileBase{
	public RenderProjectilePotion(RenderManager renderManager, RenderItem renderItem){
		super(renderManager,renderItem,Items.potionitem);
	}
	
	@Override
	protected Item getItem(Entity entity){
		return ((EntityProjectilePotion)entity).getPotionItem();
	}
	
	@Override
	protected int getItemDamage(Entity entity){
		return 1;
	}
}