package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C19CompendiumData extends AbstractClientPacket{
	private PlayerCompendiumData data;
	
	public C19CompendiumData(){}
	
	public C19CompendiumData(EntityPlayer player){
		data = CompendiumEvents.getPlayerData(player);
	}
	
	public C19CompendiumData(PlayerCompendiumData compendiumData){
		data = compendiumData;
	}

	@Override
	public void write(ByteBuf buffer){
		NBTTagCompound nbt = new NBTTagCompound();
		data.saveNBTData(nbt);
		
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
			data = new PlayerCompendiumData(CompressedStreamTools.func_152457_a(compressed,new NBTSizeTracker(2097152L)));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (data != null)CompendiumEventsClient.loadClientData(data);
	}
}
