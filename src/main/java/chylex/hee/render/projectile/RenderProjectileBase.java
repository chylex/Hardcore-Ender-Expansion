package chylex.hee.render.projectile;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class RenderProjectileBase extends RenderSnowball{
	private ItemStack cachedItemStack;
	
	public RenderProjectileBase(RenderManager renderManager, RenderItem renderItem, Item item){
		super(renderManager,item,renderItem);
		cachedItemStack = new ItemStack(item);
	}
	
	protected Item getItem(Entity entity){
		return cachedItemStack.getItem();
	}
	
	protected int getItemDamage(Entity entity){
		return 0;
	}
	
	@Override
	public final ItemStack func_177082_d(Entity entity){
		cachedItemStack.setItem(getItem(entity));
		cachedItemStack.setItemDamage(getItemDamage(entity));
		return cachedItemStack;
	}
}
