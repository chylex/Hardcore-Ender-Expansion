package chylex.hee.system.savedata.types;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.system.savedata.WorldSavefile;

public class InfestationSavefile extends WorldSavefile{
	private TObjectIntHashMap<UUID> infestationPower = new TObjectIntHashMap<>();
	private TObjectByteHashMap<UUID> infestationTimer = new TObjectByteHashMap<>();
	
	public InfestationSavefile(){
		super("infestation.nbt");
	}
	
	public void increaseInfestationPower(EntityPlayer player){
		UUID playerID = player.getGameProfile().getId();
		infestationPower.adjustOrPutValue(playerID,1,1);
		infestationTimer.put(playerID,(byte)8);
		setModified();
	}
	
	public int getInfestationPower(EntityPlayer player){
		int power = infestationPower.get(player.getGameProfile().getId());
		return power == infestationPower.getNoEntryValue() ? 0 : power;
	}
	
	public void resetInfestation(EntityPlayer player){
		UUID playerID = player.getGameProfile().getId();
		infestationPower.remove(playerID);
		infestationTimer.remove(playerID);
		setModified();
	}
	
	public byte decreaseInfestationStartTimer(EntityPlayer player){
		byte b = getInfestationStartTimer(player);
		if (b-- == 0)return 0;
		infestationTimer.put(player.getGameProfile().getId(),b);
		setModified();
		return b;
	}
	
	public byte getInfestationStartTimer(EntityPlayer player){
		byte timer = infestationTimer.get(player.getGameProfile().getId());
		return timer == infestationTimer.getNoEntryValue() ? 0 : timer;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		for(TObjectIntIterator<UUID> iter = infestationPower.iterator(); iter.hasNext();){
			iter.advance();
			nbt.setInteger(iter.key()+"_pow",iter.value());
		}
		
		for(TObjectByteIterator<UUID> iter = infestationTimer.iterator(); iter.hasNext();){
			iter.advance();
			nbt.setByte(iter.key()+"_tim",iter.value());
		}
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		for(String key:(Set<String>)nbt.func_150296_c()){
			if (key.endsWith("_pow")){
				infestationPower.put(UUID.fromString(key.substring(0,key.length()-4)),nbt.getInteger(key));
			}
			else if (key.endsWith("_tim")){
				infestationTimer.put(UUID.fromString(key.substring(0,key.length()-4)),nbt.getByte(key));
			}
		}
	}
}
