package chylex.hee.system.savedata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.mechanics.misc.LoreTexts;

public class LoreSavefile extends Savefile{
	public LoreSavefile(WorldData worldData){
		super(worldData,"lore.nbt");
	}
	
	public byte[] getUnlockedPages(EntityPlayer player){
		return nbt.getByteArray(player.getCommandSenderName());
	}
	
	public int unlockNextPage(EntityPlayer player){
		byte[] alreadyUnlocked = nbt.getByteArray(player.getCommandSenderName());
		
		if (alreadyUnlocked.length < LoreTexts.pageAmount){
			List<Byte> list = new ArrayList<>();
			for(int a = 1; a <= LoreTexts.pageAmount; a++)list.add((byte)a);
			for(byte b:alreadyUnlocked)list.remove(new Byte(b));
			
			if (list.size() == 0)return -1;
			Collections.sort(list);
			byte[] updated = new byte[alreadyUnlocked.length+1];
			for(int a = 0; a < alreadyUnlocked.length; a++)updated[a] = alreadyUnlocked[a];
			updated[alreadyUnlocked.length] = list.get(0);
			
			nbt.setByteArray(player.getCommandSenderName(),updated);
			save();
			return list.get(0);
		}
		return -1;
	}
	
	public void unlockPage(EntityPlayer player, int page){
		byte[] alreadyUnlocked = nbt.getByteArray(player.getCommandSenderName());
		for(byte b:alreadyUnlocked){
			if (b == page)return;
		}
		
		byte[] updated = new byte[alreadyUnlocked.length+1];
		for(int a = 0; a < alreadyUnlocked.length; a++)updated[a] = alreadyUnlocked[a];
		updated[alreadyUnlocked.length] = (byte)page;
		
		nbt.setByteArray(player.getCommandSenderName(),updated);
		save();
	}
}
