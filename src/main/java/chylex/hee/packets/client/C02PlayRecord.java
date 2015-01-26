package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.sound.CustomMusicTicker;

public class C02PlayRecord extends AbstractClientPacket{
	private BlockPos pos;
	private byte diskDamage;
	
	public C02PlayRecord(){}
	
	public C02PlayRecord(BlockPos pos, byte diskDamage){
		this.pos = pos;
		this.diskDamage = diskDamage;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(pos.toLong()).writeByte(diskDamage);
	}

	@Override
	public void read(ByteBuf buffer){
		pos = BlockPos.fromLong(buffer.readLong());
		diskDamage = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		String[] recordData = ItemMusicDisk.getRecordData(diskDamage);
		Minecraft mc = Minecraft.getMinecraft();

		SoundHandler soundHandler = mc.getSoundHandler();
		Map mapSoundPositions = mc.renderGlobal.mapSoundPositions;
		ISound currentSound = (ISound)mapSoundPositions.get(pos);

		if (currentSound != null){
			soundHandler.stopSound(currentSound);
			mapSoundPositions.remove(pos);
		}

		mc.ingameGUI.setRecordPlayingMessage("qwertygiy - "+recordData[0]);
		ResourceLocation resource = new ResourceLocation("hardcoreenderexpansion:"+recordData[1]);
		PositionedSoundRecord snd = PositionedSoundRecord.create(resource,pos.getX()+0.5F,pos.getY()+0.5F,pos.getZ()+0.5F);
		mapSoundPositions.put(pos,snd);
		
		CustomMusicTicker.stopMusicAndPlayJukebox(snd);
	}
}
