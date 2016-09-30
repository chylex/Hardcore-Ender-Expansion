package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C04EndPortalAnimation extends AbstractClientPacket{
	private Pos centerPos;
	
	public C04EndPortalAnimation(){}
	
	public C04EndPortalAnimation(Pos centerPos){
		this.centerPos = centerPos;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(centerPos.toLong());
	}

	@Override
	public void read(ByteBuf buffer){
		centerPos = Pos.at(buffer.readLong());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Pos.forEachBlock(centerPos.offset(-1, 0, -1), centerPos.offset(1, 0, 1), pos -> {
			pos.castTileEntity(player.worldObj, TileEntityEndPortalCustom.class).ifPresent(tile -> tile.startAnimation());
		});
	}
}
