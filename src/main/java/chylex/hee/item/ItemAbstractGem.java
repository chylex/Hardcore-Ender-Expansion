package chylex.hee.item;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemAbstractGem extends ItemAbstractEnergyAcceptor{
	protected abstract byte getCooldown();
	
	@Override
	public boolean canUse(ItemStack is){
		return super.canUse(is) && !NBT.item(is, false).hasKey("cooldown");
	}
	
	@Override
	public void useEnergy(ItemStack is, EntityLivingBase owner){
		super.useEnergy(is, owner);
		NBT.item(is, true).setByte("cooldown", getCooldown());
	}
	
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		NBTCompound tag = NBT.item(is, false);
		
		if (tag.hasKey("cooldown")){
			byte cooldown = tag.getByte("cooldown");
			
			if (--cooldown <= 0)tag.removeTag("cooldown");
			else tag.setByte("cooldown", cooldown);
		}
		
		super.onUpdate(is, world, entity, slot, isHeld);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return is.getItemDamage() == 32766 || !EnhancementRegistry.getEnhancementList(is).isEmpty() || super.hasEffect(is, pass);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		EnhancementRegistry.getEnhancementList(is).addTooltip(textLines, EnumChatFormatting.YELLOW);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		int shade = 255-MathUtil.ceil(95*(NBT.item(is, false).getByte("cooldown")/(float)getCooldown()));
		return shade<<16|shade<<8|shade;
	}
}
