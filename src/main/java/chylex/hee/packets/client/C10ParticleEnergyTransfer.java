package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C10ParticleEnergyTransfer extends AbstractClientPacket{
	private double startX, startY, startZ;
	private double targetX, targetY, targetZ;
	private byte red, green, blue, effType = 0, density = 3;
	private float spacing = 0.2F;
	
	public C10ParticleEnergyTransfer(){}
	
	public C10ParticleEnergyTransfer(TileEntity tile, TileEntityEnergyCluster cluster){
		this(tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,cluster.xCoord+0.5D,cluster.yCoord+0.5D,cluster.zCoord+0.5D,cluster.getColorRaw(0),cluster.getColorRaw(1),cluster.getColorRaw(2));
	}
	
	public C10ParticleEnergyTransfer(TileEntity tile, double targetX, double targetY, double targetZ, byte red, byte green, byte blue){
		this(tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D,targetX,targetY,targetZ,red,green,blue);
		effType = 1;
	}
	
	public C10ParticleEnergyTransfer(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, byte red, byte green, byte blue){
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(startX).writeDouble(startY).writeDouble(startZ);
		buffer.writeDouble(targetX).writeDouble(targetY).writeDouble(targetZ);
		buffer.writeByte(red).writeByte(green).writeByte(blue).writeByte(effType);
	}

	@Override
	public void read(ByteBuf buffer){
		startX = buffer.readDouble();
		startY = buffer.readDouble();
		startZ = buffer.readDouble();
		targetX = buffer.readDouble();
		targetY = buffer.readDouble();
		targetZ = buffer.readDouble();
		red = buffer.readByte();
		green = buffer.readByte();
		blue = buffer.readByte();
		
		if (buffer.readByte() == 1){
			density = 1;
			spacing = 0.65F;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Random rand = player.worldObj.rand;
		
		Vec vec = Vec.xyz(targetX-startX,targetY-startY,targetZ-startZ);
		int steps = MathUtil.floor(vec.length()*(1F/spacing));
		vec = vec.normalized();
		
		for(int a = 0; a < steps; a++){
			for(int b = 0; b < density; b++)HardcoreEnderExpansion.fx.global("energy",startX+rand(rand,0.05D),startY+rand(rand,0.05D),startZ+rand(rand,0.05D),rand(rand,0.02D),rand(rand,0.02D),rand(rand,0.02D),(red+128F)/255F,(green+128F)/255F,(blue+128F)/255F);
			startX += vec.x*spacing;
			startY += vec.y*spacing;
			startZ += vec.z*spacing;
		}
	}
	
	/**
	 * Helper method that returns random number between -1 and 1 multiplied by number provided.
	 */
	private double rand(Random rand, double mp){
		return (rand.nextDouble()-rand.nextDouble())*mp;
	}
}
