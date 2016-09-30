package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryEnvironment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C13TerritoryInfo extends AbstractClientPacket{
	private EndTerritory territory;
	private EnumSet<? extends Enum<?>> variations;
	
	public C13TerritoryInfo(){}
	
	public C13TerritoryInfo(EndTerritory territory, EnumSet<? extends Enum<?>> variations){
		this.territory = territory;
		this.variations = variations;
	}
	
	@Override
	public void write(ByteBuf buffer){
		if (variations.isEmpty())buffer.writeByte(-1);
		else buffer.writeByte(territory.ordinal()).writeInt(territory.properties.serialize(variations));
	}

	@Override
	public void read(ByteBuf buffer){
		territory = CollectionUtil.get(EndTerritory.values, buffer.readByte()).orElse(null);
		variations = territory == null ? EmptyEnumSet.get() : territory.properties.deserialize(buffer.readInt());
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		TerritoryEnvironment.updateCurrentVariations(variations);
	}
}
