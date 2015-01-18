package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.AbstractClientPacket;

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
			CompressedStreamTools.write(nbt,new ByteBufOutputStream(buffer));
		}catch(IOException e){
			buffer.writeByte(0);
			e.printStackTrace();
		}
	}

	@Override
	public void read(ByteBuf buffer){
		int index = buffer.readerIndex();
		if (buffer.readByte() == 0)return;
		
		try{
			buffer.readerIndex(index);
			data = new PlayerCompendiumData(CompressedStreamTools.func_152456_a(new ByteBufInputStream(buffer),new NBTSizeTracker(2097152L)));
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
