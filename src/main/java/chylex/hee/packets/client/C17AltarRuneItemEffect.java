package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.RuneItem;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C17AltarRuneItemEffect extends AbstractClientPacket{
	private Pos pos;
	private byte essenceId, runeArrayIndex;
	
	public C17AltarRuneItemEffect(){}
	
	public C17AltarRuneItemEffect(TileEntityEssenceAltar altar, byte runeArrayIndex){
		this.pos = Pos.at(altar.xCoord,altar.yCoord,altar.zCoord);
		this.essenceId = altar.getEssenceType().id;
		this.runeArrayIndex = runeArrayIndex;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(pos.toLong()).writeByte(essenceId).writeByte(runeArrayIndex);
	}

	@Override
	public void read(ByteBuf buffer){
		pos = Pos.at(buffer.readLong());
		essenceId = buffer.readByte();
		runeArrayIndex = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		RuneItem runeItem = EssenceType.getById(essenceId).itemsNeeded[runeArrayIndex];
		ItemStack is = runeItem.getShowcaseItem();
		Random rand = player.worldObj.rand;
		
		for(int a = 0; a < 42; a++){
			Vec vec = Vec.xzRandom(rand);
			HardcoreEnderExpansion.fx.item(is,pos.getX()+0.5D+rand.nextDouble()*0.4D-0.2D,pos.getY()+1.1D+rand.nextDouble()*0.4D,pos.getZ()+0.5D+rand.nextDouble()*0.2D-0.1D,vec.x*0.1D,0.1D,vec.z*0.1D);
		}
		
		player.worldObj.playSound(pos.getX(),pos.getY(),pos.getZ(),runeItem.soundEffect,1F,0.9F+rand.nextFloat()*0.1F,false);
	}
}
