package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.sound.CustomMusicTicker;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C02PlayRecord extends AbstractClientPacket{
	private BlockPosM pos;
	private byte diskDamage;
	
	public C02PlayRecord(){}
	
	public C02PlayRecord(BlockPosM pos, byte diskDamage){
		this.pos = pos.copy();
		this.diskDamage = diskDamage;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(pos.toLong()).writeByte(diskDamage);
	}

	@Override
	public void read(ByteBuf buffer){
		pos = new BlockPosM(buffer.readLong());
		diskDamage = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (!CustomMusicTicker.canPlayMusic()){
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation("music.notEnabled"));
			return;
		}
		
		String[] recordData = ItemMusicDisk.getRecordData(diskDamage);
		Minecraft mc = Minecraft.getMinecraft();

		SoundHandler soundHandler = mc.getSoundHandler();
		ChunkCoordinates coords = new ChunkCoordinates(pos.x,pos.y,pos.z);
		Map mapSoundPositions = mc.renderGlobal.mapSoundPositions;
		ISound currentSound = (ISound)mapSoundPositions.get(coords);

		if (currentSound != null){
			soundHandler.stopSound(currentSound);
			mapSoundPositions.remove(coords);
		}

		mc.ingameGUI.setRecordPlayingMessage("qwertygiy - "+recordData[0]);
		
		ResourceLocation resource = ItemMusicDisk.getRecordResource(diskDamage);
		PositionedSoundRecord snd = PositionedSoundRecord.func_147675_a(resource,pos.x,pos.y,pos.z);
		
		mapSoundPositions.put(coords,snd);
		CustomMusicTicker.stopMusicAndPlayJukebox(snd);
	}
}
