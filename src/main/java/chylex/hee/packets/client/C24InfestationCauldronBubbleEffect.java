package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C24InfestationCauldronBubbleEffect extends AbstractClientPacket{
	private int x,y,z;
	
	public C24InfestationCauldronBubbleEffect(){}
	
	public C24InfestationCauldronBubbleEffect(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(y).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		for(int a = 0; a < 10; a++){
			HardcoreEnderExpansion.fx.bubble(player.worldObj,x+0.5D+(rand.nextFloat()-0.5D)*0.5D,y+0.5F+rand.nextFloat()*0.4F,z+0.5D+(rand.nextFloat()-0.5D)*0.5D,0D,0.03D,0D);
		}
		player.worldObj.playSound(x+0.5D,y+0.8D,z+0.5D,"hardcoreenderexpansion:environment.random.bubble",1F,rand.nextFloat()*0.1F+0.95F,false);
	}
}
