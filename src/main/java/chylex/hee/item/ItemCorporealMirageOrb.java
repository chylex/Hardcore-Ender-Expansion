package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileCorporealMirageOrb;

public class ItemCorporealMirageOrb extends Item{
	public ItemCorporealMirageOrb(){
		super();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		world.playSoundAtEntity(player,"random.bow",0.5F,0.4F/(itemRand.nextFloat()*0.4F+0.8F));

		if (!world.isRemote)world.spawnEntityInWorld(new EntityProjectileCorporealMirageOrb(world,player));
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		return is;
	}
}
