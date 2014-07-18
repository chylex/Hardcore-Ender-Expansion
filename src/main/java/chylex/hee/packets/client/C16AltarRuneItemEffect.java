package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.RuneItem;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C16AltarRuneItemEffect extends AbstractClientPacket{
	private int x,y,z;
	private byte essenceId,runeArrayIndex;
	
	public C16AltarRuneItemEffect(){}
	
	public C16AltarRuneItemEffect(TileEntityEssenceAltar altar, byte runeArrayIndex){
		this.x = altar.xCoord;
		this.y = altar.yCoord;
		this.z = altar.zCoord;
		this.essenceId = altar.getEssenceType().id;
		this.runeArrayIndex = runeArrayIndex;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(y).writeInt(z).writeByte(essenceId).writeByte(runeArrayIndex);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		essenceId = buffer.readByte();
		runeArrayIndex = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		RuneItem runeItem = EssenceType.getById(essenceId).itemsNeeded[runeArrayIndex];
		ItemStack is = runeItem.getShowcaseItem();
		World world = player.worldObj;
		
		for(int a = 0; a < 42; a++){
			double[] vec = DragonUtil.getNormalizedVector(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
			HardcoreEnderExpansion.fx.item(is,world,x+0.5D+rand.nextDouble()*0.4D-0.2D,y+1.1D+rand.nextDouble()*0.4D,z+0.5D+rand.nextDouble()*0.2D-0.1D,vec[0]*0.1D,0.1D,vec[1]*0.1D);
		}
		world.playSound(x,y,z,runeItem.soundEffect,1F,0.9F+rand.nextFloat()*0.1F,false);
	}
}
