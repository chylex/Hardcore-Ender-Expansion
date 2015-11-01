package chylex.hee.packets.server;
import gnu.trove.set.hash.TShortHashSet;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.packets.AbstractServerPacket;

public class S01CompendiumReadFragments extends AbstractServerPacket{
	private TShortHashSet fragments = new TShortHashSet();
	
	public S01CompendiumReadFragments(){}
	
	public S01CompendiumReadFragments(Collection<KnowledgeFragment> fragments){
		fragments.forEach(fragment -> this.fragments.add((short)fragment.globalID));
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(fragments.size());
		for(short val:fragments.toArray())buffer.writeShort(val);
	}

	@Override
	public void read(ByteBuf buffer){
		int amt = buffer.readByte();
		for(int a = 0; a < amt; a++)fragments.add(buffer.readShort());
	}

	@Override
	protected void handle(EntityPlayerMP player){
		for(short id:fragments.toArray())SaveData.player(player,CompendiumFile.class).markFragmentAsRead(id);
	}
}
