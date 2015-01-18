package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import chylex.hee.gui.GuiTransportBeacon;
import chylex.hee.mechanics.misc.PlayerTransportBeacons.LocationXZ;
import chylex.hee.packets.AbstractClientPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class C13TransportBeaconData extends AbstractClientPacket{
	private Set<LocationXZ> offsets;
	private boolean hasEnergy, noTampering;
	
	public C13TransportBeaconData(){}
	
	public C13TransportBeaconData(Set<LocationXZ> offsets, boolean hasEnergy, boolean noTampering){
		this.offsets = offsets;
		this.hasEnergy = hasEnergy;
		this.noTampering = noTampering;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeBoolean(hasEnergy).writeBoolean(noTampering);
		buffer.writeShort(offsets.size());
		for(LocationXZ offset:offsets)buffer.writeShort(offset.x).writeShort(offset.z);
	}

	@Override
	public void read(ByteBuf buffer){
		hasEnergy = buffer.readBoolean();
		noTampering = buffer.readBoolean();
		
		int amt = buffer.readShort();
		offsets = new HashSet<>();
		for(int a = 0; a < amt; a++)offsets.add(new LocationXZ(buffer.readShort(),buffer.readShort()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		
		if (gui instanceof GuiTransportBeacon){
			((GuiTransportBeacon)gui).loadOffsets(offsets);
			((GuiTransportBeacon)gui).setStatus(hasEnergy,noTampering);
		}
	}
}