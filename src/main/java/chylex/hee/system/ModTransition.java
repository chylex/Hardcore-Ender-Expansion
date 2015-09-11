package chylex.hee.system;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import chylex.hee.gui.GuiModTransition;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ModTransition{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new ModTransition());
	}
	
	public static void updateMappings(FMLMissingMappingsEvent e){
		final String id = "HardcoreEnderExpansion:";
		final Set<String> discard = new HashSet<>();
		
		discard.add(id+"transport_beacon");
		discard.add(id+"temple_end_portal");
		discard.add(id+"biome_compass");
		discard.add(id+"adventurers_diary");
		discard.add(id+"altar_nexus");
		discard.add(id+"temple_caller");
		
		e.get().stream().filter(mapping -> discard.contains(mapping.name)).forEach(MissingMapping::ignore);
	}
	
	public static boolean shouldConvertWorld(File root){
		return !new File(root,"hee2").isDirectory() && new File(root,"DIM1").isDirectory(); // DIM1 should always be there, but convert vanilla worlds too
	}
	
	public static void doConvertWorld(File root) throws IOException{
		Pos worldSpawnPoint = new Pos(0,255,0);
		
		File levelDat = new File(root,"level.dat");
		NBTTagCompound levelDatNBT;
		
		try(FileInputStream fileStreamIn = new FileInputStream(levelDat)){
			levelDatNBT = CompressedStreamTools.readCompressed(fileStreamIn);
			
			NBTTagCompound data = levelDatNBT.getCompoundTag("Data");
			worldSpawnPoint = new Pos(data.getInteger("SpawnX"),data.getInteger("SpawnY"),data.getInteger("SpawnZ"));
		}
		
		FileUtils.deleteDirectory(new File(root,"DIM1"));
		FileUtils.deleteDirectory(new File(root,"hee"));
		
		for(File file:new File(root,"playerdata").listFiles((file, name) -> FilenameUtils.getExtension(name).equals("dat"))){
			try(FileInputStream fileStreamIn = new FileInputStream(file)){
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(fileStreamIn);
				convertPlayerTag(nbt,worldSpawnPoint);
				
				try(FileOutputStream fileStreamOut = new FileOutputStream(file)){
					CompressedStreamTools.writeCompressed(nbt,fileStreamOut);
				}
			}
		}
		
		NBTTagCompound dataTag = levelDatNBT.getCompoundTag("Data");
		
		if (dataTag.hasKey("Player")){
			NBTTagCompound playerNBT = dataTag.getCompoundTag("Player");
			convertPlayerTag(playerNBT,worldSpawnPoint);
			dataTag.setTag("Player",playerNBT);
		}
		
		
		try(FileOutputStream fileStreamOut = new FileOutputStream(levelDat)){
			CompressedStreamTools.writeCompressed(levelDatNBT,fileStreamOut);
		}
		
		new File(root,"hee2").mkdir();
	}
	
	private static void convertPlayerTag(NBTTagCompound nbt, Pos spawnPoint){
		nbt.removeTag("HEE_TB_locs");
		nbt.removeTag("HEE_VC_items");
		nbt.removeTag("HEE");
		
		if (nbt.getInteger("Dimension") == 1){
			nbt.setInteger("Dimension",0);
			
			if (nbt.hasKey("SpawnY"))spawnPoint = new Pos(nbt.getInteger("SpawnX"),nbt.getInteger("SpawnY"),nbt.getInteger("SpawnZ"));
			
			NBTTagList posTag = new NBTTagList();
			posTag.appendTag(new NBTTagDouble(spawnPoint.getX()+0.5D));
			posTag.appendTag(new NBTTagDouble(spawnPoint.getY()));
			posTag.appendTag(new NBTTagDouble(spawnPoint.getZ()+0.5D));
			nbt.setTag("Pos",posTag);
		}
	}
	
	public static void convertServer(){
		Log.warn("=====================================================");
		Log.warn("Hardcore Ender Expansion 2 is converting the world...");
		Log.warn("=====================================================");
		
		File root = DimensionManager.getCurrentSaveRootDirectory();
		
		if (shouldConvertWorld(root)){
			try{
				doConvertWorld(root);
			}catch(IOException ex){
				throw new RuntimeException("Could not convert the server world!",ex);
			}
		}
	}
	
	private ModTransition(){}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onGuiOpen(GuiOpenEvent e){
		if (e.gui instanceof GuiSelectWorld){
			Minecraft mc = Minecraft.getMinecraft();
			List<SaveFormatComparator> saveList;
			
			try{
				saveList = mc.getSaveLoader().getSaveList();
			}catch(AnvilConverterException ex){
				mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds",ex.getMessage()));
				return;
			}
			
			Set<File> toUpdate = new HashSet<>();
			
			for(SaveFormatComparator save:saveList){
				String name = save.getFileName();
				if (name == null)continue; // happens to WorldX from alpha days, is this happens the world is too old to not break anyways
				
				File root = new File(FMLClientHandler.instance().getSavesDir(),name);
				if (shouldConvertWorld(root))toUpdate.add(root);
			}
			
			if (!toUpdate.isEmpty())e.gui = new GuiModTransition(mc.currentScreen,e.gui,toUpdate);
		}
	}
}
