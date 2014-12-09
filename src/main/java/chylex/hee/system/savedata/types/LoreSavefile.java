package chylex.hee.system.savedata.types;
import gnu.trove.list.array.TByteArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.mechanics.misc.LoreTexts;
import chylex.hee.system.savedata.WorldSavefile;
import chylex.hee.system.util.DragonUtil;

public class LoreSavefile extends WorldSavefile{
	private final Map<UUID,byte[]> unlockedPages = new HashMap<>();
	
	public LoreSavefile(){
		super("lore.nbt");
	}
	
	public byte[] getUnlockedPages(EntityPlayer player){
		byte[] pages = unlockedPages.get(player.getGameProfile().getId());
		return pages != null ? pages : ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	public int unlockNextPage(EntityPlayer player){
		byte[] alreadyUnlocked = getUnlockedPages(player);
		
		if (alreadyUnlocked.length < LoreTexts.pageAmount){
			TByteArrayList list = new TByteArrayList(LoreTexts.pageAmount);
			for(byte a = 1; a <= LoreTexts.pageAmount; a++)list.add(a);
			list.removeAll(alreadyUnlocked);
			
			if (list.isEmpty())return -1;
			list.sort();
			byte[] updated = new byte[alreadyUnlocked.length+1];
			for(int a = 0; a < alreadyUnlocked.length; a++)updated[a] = alreadyUnlocked[a];
			updated[alreadyUnlocked.length] = list.get(0);
			
			unlockedPages.put(player.getGameProfile().getId(),updated);
			setModified();
			return list.get(0);
		}
		else return -1;
	}
	
	public void unlockPage(EntityPlayer player, int page){
		byte[] alreadyUnlocked = getUnlockedPages(player);
		if (ArrayUtils.contains(alreadyUnlocked,(byte)page))return;
		
		byte[] updated = new byte[alreadyUnlocked.length+1];
		for(int a = 0; a < alreadyUnlocked.length; a++)updated[a] = alreadyUnlocked[a];
		updated[alreadyUnlocked.length] = (byte)page;
		
		unlockedPages.put(player.getGameProfile().getId(),updated);
		setModified();
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		for(Entry<UUID,byte[]> entry:unlockedPages.entrySet()){
			nbt.setByteArray(entry.getKey().toString(),entry.getValue());
		}
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		unlockedPages.clear();
		for(String key:(Set<String>)nbt.func_150296_c())unlockedPages.put(DragonUtil.convertNameToUUID(key),nbt.getByteArray(key));
	}
}
