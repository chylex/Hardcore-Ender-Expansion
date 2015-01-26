package chylex.hee.packets;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractClientPacket extends AbstractPacket{
	@Override
	public void handle(Side side, EntityPlayer player){
		if (side == Side.CLIENT)handle((AbstractClientPlayer)player);
		else throw new UnsupportedOperationException("Tried to handle client packet on server side! Packet class: "+getClass().getSimpleName());
	}
	
	@SideOnly(Side.CLIENT)
	protected abstract void handle(AbstractClientPlayer player);
}
