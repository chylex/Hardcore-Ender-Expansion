package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.gui.ContainerEndPowderEnhancements;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementData.EnhancementInfo;
import chylex.hee.packets.AbstractServerPacket;

public class S01EnhanceItem extends AbstractServerPacket{
	private byte index;
	
	public S01EnhanceItem(){}
	
	public S01EnhanceItem(EnhancementData<?>.EnhancementInfo selectedEnhancement){
		this.index = (byte)selectedEnhancement.getEnhancement().ordinal();
	}

	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(index);
	}

	@Override
	public void read(ByteBuf buffer){
		index = buffer.readByte();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		if (player.openContainer instanceof ContainerEndPowderEnhancements){
			ContainerEndPowderEnhancements container = (ContainerEndPowderEnhancements)player.openContainer;
			
			List<EnhancementInfo> info = container.listEnhancementInfo();
			if (index >= 0 && index < info.size())container.tryUpgradeEnhancement(info.get(index)); // same order as enums
		}
	}
}
