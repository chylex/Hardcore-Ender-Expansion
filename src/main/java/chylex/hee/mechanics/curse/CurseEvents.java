package chylex.hee.mechanics.curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import chylex.hee.item.ItemList;

public final class CurseEvents{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new CurseEvents());
	}
	
	public static boolean hasAmulet(EntityLivingBase entity){
		ItemStack is = entity.getHeldItem();
		return is != null && is.getItem() == ItemList.curse_amulet;
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e){
		if (e.source.getSourceOfDamage() instanceof EntityLivingBase){
			EntityLivingBase source = (EntityLivingBase)e.source.getSourceOfDamage();
			NBTTagCompound nbt = source.getEntityData();
			long tim = nbt.getLong("HEE_C9_l");
			
			if (tim != 0L && e.entity.worldObj.getTotalWorldTime()-tim < 5){
				source.attackEntityFrom(DamageSource.causeMobDamage(e.entityLiving),e.ammount*0.5F);
				nbt.setBoolean("HEE_C9_a",true);
				e.ammount = 0F;
				e.setCanceled(true);
			}
		}
	}
	
	private CurseEvents(){}
}
