package chylex.hee.system.integration.handlers;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import chylex.hee.system.integration.IIntegrationHandler;
import com.google.common.base.Objects;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ArsMagicaFixIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "arsmagica2";
	}

	@Override
	public void integrate(){
		MinecraftForge.EVENT_BUS.register(this);
		
		FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable(){
			@Override
			public String getLabel(){
				return "Hardcore Ender Expansion";
			}
			
			@Override
			public String call() throws Exception{
				return "CAUTION! Ars Magica 2 is not supported by HEE, if the crash is caused by a conflict of the two mods, it will very likely not be possible to fix.";
			}
		});
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void am2NullWorldConstructionWorkaround(EntityConstructing e){
		if (e.entity.worldObj == null && e.entity instanceof EntityLivingBase && Objects.firstNonNull(EntityList.getEntityString(e.entity),"").startsWith("HardcoreEnderExpansion")){
			e.entity.getExtendedProperties("ArsMagicaExProps").init(e.entity,DimensionManager.getWorld(1));
		}
	}
}
