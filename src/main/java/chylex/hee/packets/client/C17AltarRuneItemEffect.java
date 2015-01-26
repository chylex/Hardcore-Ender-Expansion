package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.essence.RuneItem;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class C17AltarRuneItemEffect extends AbstractClientPacket{
	private BlockPos pos;
	private byte essenceId,runeArrayIndex;
	
	public C17AltarRuneItemEffect(){}
	
	public C17AltarRuneItemEffect(TileEntityEssenceAltar altar, byte runeArrayIndex){
		this.pos = altar.getPos();
		this.essenceId = altar.getEssenceType().getId();
		this.runeArrayIndex = runeArrayIndex;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(pos.toLong()).writeByte(essenceId).writeByte(runeArrayIndex);
	}

	@Override
	public void read(ByteBuf buffer){
		pos = BlockPos.fromLong(buffer.readLong());
		essenceId = buffer.readByte();
		runeArrayIndex = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		RuneItem runeItem = EssenceType.getById(essenceId).itemsNeeded[runeArrayIndex];
		ItemStack is = runeItem.getShowcaseItem();
		World world = player.worldObj;
		
		for(int a = 0; a < 42; a++){
			double[] vec = DragonUtil.getNormalizedVector(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D);
			HardcoreEnderExpansion.fx.item(is,world,pos.getX()+0.5D+rand.nextDouble()*0.4D-0.2D,pos.getY()+1.1D+rand.nextDouble()*0.4D,pos.getZ()+0.5D+rand.nextDouble()*0.2D-0.1D,vec[0]*0.1D,0.1D,vec[1]*0.1D);
		}
		
		world.playSound(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,runeItem.soundEffect,1F,0.9F+rand.nextFloat()*0.1F,false);
	}
}
