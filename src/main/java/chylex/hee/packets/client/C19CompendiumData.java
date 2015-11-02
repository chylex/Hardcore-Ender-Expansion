package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.render.OverlayManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C19CompendiumData extends AbstractClientPacket{
	private CompendiumFile file;
	private KnowledgeObject<?> discovered;
	
	public C19CompendiumData(){}
	
	public C19CompendiumData(EntityPlayer player){
		this.file = CompendiumEvents.getPlayerData(player);
	}
	
	public C19CompendiumData(CompendiumFile compendiumFile){
		this.file = compendiumFile;
	}
	
	public C19CompendiumData(CompendiumFile compendiumFile, KnowledgeObject<?> discovered){
		this.file = compendiumFile;
		this.discovered = discovered;
	}

	@Override
	public void write(ByteBuf buffer){
		NBTTagCompound nbt = new NBTTagCompound();
		file.onSave(nbt);
		
		try{
			byte[] compressed = CompressedStreamTools.compress(nbt);
			buffer.writeShort(compressed.length);
			buffer.writeBytes(compressed);
		}catch(IOException e){
			buffer.writeShort(-1);
			e.printStackTrace();
		}
		
		buffer.writeShort(discovered == null ? -1 : discovered.globalID);
	}

	@Override
	public void read(ByteBuf buffer){
		short len = buffer.readShort();
		if (len == -1)return;
		
		byte[] compressed = buffer.readBytes(len).array();
		
		try{
			file = new CompendiumFile(CompressedStreamTools.func_152457_a(compressed,new NBTSizeTracker(2097152L)));
		}catch(IOException e){
			e.printStackTrace();
		}
		
		int discoveredId = buffer.readShort();
		if (discoveredId != -1)discovered = KnowledgeObject.fromID(discoveredId);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (file != null)CompendiumEventsClient.loadClientData(file);
		
		if (discovered != null){
			OverlayManager.addNotification(discovered);
			player.worldObj.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.pageflip",0.25F,0.5F*((player.getRNG().nextFloat()-player.getRNG().nextFloat())*0.7F+1.6F),false);
		}
		
		GuiEnderCompendium.pausesGame = GuiEnderCompendium.wasPaused;System.out.println("set to "+GuiEnderCompendium.wasPaused);
	}
}
