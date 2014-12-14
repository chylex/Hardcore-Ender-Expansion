package chylex.hee.mechanics.curse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class CurseEvents{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new CurseEvents());
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e){
		long tim = e.entity.getEntityData().getLong("HEE_C9_l");
		
		if (tim != 0L && e.entity.worldObj.getTotalWorldTime()-tim < 5 && e.source.getSourceOfDamage() instanceof EntityLivingBase){
			e.source.getSourceOfDamage().attackEntityFrom(DamageSource.causeMobDamage(e.entityLiving),e.ammount*0.5F);
			e.ammount = 0F;
			e.setCanceled(true);
			e.entity.getEntityData().setBoolean("HEE_C9_a",true);
		}
	}
	
	private CurseEvents(){}
}
