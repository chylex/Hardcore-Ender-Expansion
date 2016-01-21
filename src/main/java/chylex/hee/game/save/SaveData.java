package chylex.hee.game.save;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import chylex.hee.game.save.handlers.GlobalDataHandler;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class SaveData{
	private static final SaveData instance = new SaveData();
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(instance);
		for(ISaveDataHandler handler:instance.handlers)handler.register();
	}
	
	public static <T extends SaveFile> T global(Class<T> cls){
		return instance.global.get(cls);
	}
	
	public static <T extends PlayerFile> T player(EntityPlayer player, Class<T> cls){
		return instance.player.get(player,cls);
	}
	
	public static <T extends PlayerFile> T player(String playerID, Class<T> cls){
		return instance.player.get(playerID,cls);
	}
	
	private final GlobalDataHandler global;
	private final PlayerDataHandler player;
	private final ISaveDataHandler[] handlers;
	
	private File worldSaveDir;
	private String worldIdentifier = "";
	
	private SaveData(){
		this.global = new GlobalDataHandler();
		this.player = new PlayerDataHandler();
		
		this.handlers = new ISaveDataHandler[]{
			global, player
		};
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e){
		if (e.world.isRemote)return;
		
		String id = e.world.getSaveHandler().getWorldDirectoryName()+e.world.getWorldInfo().getWorldName()+e.world.getWorldInfo().getSeed();
		
		if (!worldIdentifier.equals(id)){
			worldIdentifier = id;
			
			File root = DimensionManager.getCurrentSaveRootDirectory();
			if (root == null)throw new IllegalStateException("Could not determine world save directory!");
			
			worldSaveDir = new File(root,"hee2");
			if (!worldSaveDir.exists())worldSaveDir.mkdirs();
			
			for(ISaveDataHandler handler:handlers)handler.clear(worldSaveDir);
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e){
		if (worldSaveDir == null)return;
		
		for(ISaveDataHandler handler:handlers)handler.save();
	}
	
	public static NBTCompound readFile(File file){
		if (file.exists()){
			try(FileInputStream fileStream = new FileInputStream(file)){
				return NBT.wrap(CompressedStreamTools.readCompressed(fileStream));
			}catch(IOException ioe){
				Log.throwable(ioe,"Error reading NBT file - $0",file);
			}
		}
		
		return new NBTCompound();
	}
	
	public static boolean saveFile(File file, NBTCompound nbt){
		try(FileOutputStream fileStream = new FileOutputStream(file)){
			CompressedStreamTools.writeCompressed(nbt.getUnderlyingTag(),fileStream);
			return true;
		}catch(Exception ex){
			Log.throwable(ex,"Error writing NBT file $0",file);
			return false;
		}
	}
}
