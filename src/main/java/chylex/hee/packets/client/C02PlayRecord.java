package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.sound.CustomMusicTicker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C02PlayRecord extends AbstractClientPacket{
	private int x,y,z;
	private byte diskDamage;
	
	public C02PlayRecord(){}
	
	public C02PlayRecord(int x, int y, int z, byte diskDamage){
		this.x = x;
		this.y = y;
		this.z = z;
		this.diskDamage = diskDamage;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(y).writeInt(z).writeByte(diskDamage);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		diskDamage = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		String[] recordData = ItemMusicDisk.getRecordData(diskDamage);
		Minecraft mc = Minecraft.getMinecraft();

		SoundHandler soundHandler = mc.getSoundHandler();
		ChunkCoordinates coords = new ChunkCoordinates(x,y,z);
		Map mapSoundPositions = mc.renderGlobal.mapSoundPositions;
		ISound currentSound = (ISound)mapSoundPositions.get(coords);

		if (currentSound != null){
			soundHandler.stopSound(currentSound);
			mapSoundPositions.remove(coords);
		}

		mc.ingameGUI.setRecordPlayingMessage("qwertygiy - "+recordData[0]);
		ResourceLocation resource = new ResourceLocation("hardcoreenderexpansion:"+recordData[1]);
		PositionedSoundRecord snd = PositionedSoundRecord.func_147675_a(resource,x,y,z);
		mapSoundPositions.put(coords,snd);
		
		CustomMusicTicker.stopMusicAndPlayJukebox(snd);
	}
}
