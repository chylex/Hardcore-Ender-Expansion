package chylex.hee.game.save.handlers;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import chylex.hee.game.save.ISaveDataHandler;
import chylex.hee.game.save.types.PlayerFile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerDataHandler implements ISaveDataHandler{
	private static final String dataIdentifier = "HardcoreEnderExpansion2";
	
	public static final String getID(EntityPlayer player){
		return ((PlayerIdProperty)player.getExtendedProperties(dataIdentifier)).id;
	}
	
	private final Map<String,PlayerFile> cache = new HashMap<>();
	private File root;
	
	// ISaveDataHandler
	
	@Override
	public void register(){
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void clear(File root){
		cache.clear();
		this.root = new File(root,"players");
		if (!this.root.exists())this.root.mkdirs();
	}
	
	public <T extends PlayerFile> T get(EntityPlayer player, Class<T> cls){
		String id = getID(player);
		String cacheKey = cls.getSimpleName()+"~"+id;
		
		PlayerFile savefile = cache.get(cacheKey);
		
		if (savefile == null){
			try{
				cache.put(cacheKey,savefile = cls.getConstructor(String.class).newInstance(id+".nbt"));
				savefile.loadFromNBT(root);
			}catch(Exception e){
				throw new RuntimeException("Could not construct a new instance of PlayerFile - "+cls.getName(),e);
			}
		}
		
		return (T)savefile;
	}

	@Override
	public void save(){
		cache.values().stream().filter(savefile -> savefile.wasModified()).forEach(savefile -> savefile.saveToNBT(root));
	}
	
	// Events
	
	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing e){
		if (e.entity.worldObj != null && e.entity instanceof EntityPlayer){
			if (!e.entity.registerExtendedProperties(dataIdentifier,new PlayerIdProperty()).equals(dataIdentifier)){
				throw new IllegalStateException("Could not register extended player properties, likely due to the properties already being registered by another mod!");
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone e){
		NBTTagCompound nbt = new NBTTagCompound();
		e.original.getExtendedProperties(dataIdentifier).saveNBTData(nbt);
		e.entityPlayer.getExtendedProperties(dataIdentifier).loadNBTData(nbt);
	}
	
	// IExtendedEntityProperties
	
	private static class PlayerIdProperty implements IExtendedEntityProperties{
		private String id;
		
		@Override
		public void init(Entity entity, World world){
			id = UUID.randomUUID().toString().replace("-","");
		}
		
		@Override
		public void saveNBTData(NBTTagCompound nbt){
			nbt.setString("HEE2_PID",id);
		}
		
		@Override
		public void loadNBTData(NBTTagCompound nbt){
			if (nbt.hasKey("HEE2_PID"))id = nbt.getString("HEE2_PID");
		}
	}
}
