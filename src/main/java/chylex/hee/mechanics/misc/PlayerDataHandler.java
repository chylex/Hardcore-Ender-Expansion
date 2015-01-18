package chylex.hee.mechanics.misc;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDataHandler{
	private static final PlayerDataHandler instance = new PlayerDataHandler();
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void registerProperty(String identifier, IExtendedPropertyInitializer<?> initializer){
		instance.registrations.put(identifier,initializer);
	}
	
	private Map<String,IExtendedPropertyInitializer<?>> registrations = new HashMap<>();
	
	private PlayerDataHandler(){}

	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing e){
		if (e.entity.worldObj != null && e.entity instanceof EntityPlayer){
			for(Entry<String,IExtendedPropertyInitializer<?>> entry:registrations.entrySet()){
				if (!e.entity.registerExtendedProperties(entry.getKey(),entry.getValue().createNew(e.entity)).equals(entry.getKey())){
					throw new IllegalStateException("Could not register extended player properties, likely due to the properties already being registered by another mod!");
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone e){
		for(Entry<String,IExtendedPropertyInitializer<?>> entry:registrations.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			e.original.getExtendedProperties(entry.getKey()).saveNBTData(tag);
			e.entityPlayer.getExtendedProperties(entry.getKey()).loadNBTData(tag);
		}
	}
	
	public static interface IExtendedPropertyInitializer<T extends IExtendedEntityProperties>{
		T createNew(Entity entity);
	}
}
