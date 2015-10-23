package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.mechanics.compendium_old.events.CompendiumEvents;
import chylex.hee.mechanics.compendium_old.events.CompendiumEventsClient;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C19CompendiumData extends AbstractClientPacket{
	private CompendiumFile file;
	
	public C19CompendiumData(){}
	
	public C19CompendiumData(EntityPlayer player){
		file = CompendiumEvents.getPlayerData(player);
	}
	
	public C19CompendiumData(CompendiumFile compendiumFile){
		file = compendiumFile;
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
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (file != null)CompendiumEventsClient.loadClientData(file);
	}
}
