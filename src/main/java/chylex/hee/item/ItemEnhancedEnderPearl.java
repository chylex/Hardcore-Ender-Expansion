package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileEnhancedEnderPearl;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnhancedEnderPearl extends ItemEnderPearl{
	// TODO remove eventually
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (!world.isRemote && is.stackTagCompound != null && !is.stackTagCompound.hasKey("HEE_enhancements")){
			for(EnderPearlEnhancements enhancement:EnderPearlEnhancements.getOldEnhancements(is))EnhancementHandler.addEnhancementToItemStack(is,enhancement);
			if (is.stackTagCompound.hasKey("display"))is.stackTagCompound.getCompoundTag("display").removeTag("Lore");
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		world.playSoundAtEntity(player,"random.bow",0.5F,0.4F/(itemRand.nextFloat()*0.4F+0.8F));

		if (!world.isRemote)world.spawnEntityInWorld(new EntityProjectileEnhancedEnderPearl(world,player));
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementHandler.appendEnhancementNames(is,textLines);
	}
}
