package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.gui.GuiKnowledgeNote;
import chylex.hee.mechanics.compendium.content.LoreTexts;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C03KnowledgeNote extends AbstractClientPacket{
	private LoreTexts category;
	private int index, points;
	
	public C03KnowledgeNote(){}
	
	public C03KnowledgeNote(LoreTexts category, int index, int points){
		this.category = category;
		this.index = index;
		this.points = points;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(category.ordinal()).writeByte(index).writeShort(points);
	}

	@Override
	public void read(ByteBuf buffer){
		category = CollectionUtil.get(LoreTexts.values(), buffer.readByte()).orElse(LoreTexts.UNKNOWN);
		index = buffer.readByte();
		points = buffer.readShort();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Minecraft.getMinecraft().displayGuiScreen(new GuiKnowledgeNote(category.getUnlocalizedName(index), points));
	}
}
