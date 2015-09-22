package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.system.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpatialDashGem extends ItemAbstractEnergyAcceptor{
	@Override
	public int getEnergyAccepted(ItemStack is){
		return 2;
	}
	
	@Override
	public int getEnergyUsage(ItemStack is){
		return 1;
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (ItemUtil.getTagRoot(is,false).hasKey("cooldown")){
			byte cooldown = is.getTagCompound().getByte("cooldown");
			
			if (--cooldown <= 0)is.getTagCompound().removeTag("cooldown");
			else is.getTagCompound().setByte("cooldown",cooldown);
		}
		
		super.onUpdate(is,world,entity,slot,isHeld);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.getItemDamage() < getMaxDamage() && (!is.hasTagCompound() || !is.getTagCompound().hasKey("cooldown"))){
			if (!world.isRemote){
				// TODO CausatumUtils.increase(player,CausatumMeters.ITEM_USAGE,0.5F);
				useEnergy(is,player);
				world.spawnEntityInWorld(new EntityProjectileSpatialDash(world,player,EnhancementHandler.getEnhancements(is)));
				
				ItemUtil.getTagRoot(is,true).setByte("cooldown",(byte)18);
			}
			else world.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.spatialdash",0.8F,0.9F,false);
		}
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return is.getItemDamage() == 32766 || super.hasEffect(is,pass);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementHandler.appendEnhancementNames(is,textLines);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		return ItemUtil.getTagRoot(is,false).hasKey("cooldown") ? (192<<16|192<<8|192) : super.getColorFromItemStack(is,pass);
	}
}
