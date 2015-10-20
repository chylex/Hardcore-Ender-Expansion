package chylex.hee.game.save.types.global;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.NBTUtil;

public class StrongholdFile extends SaveFile{
	private Set<Pos> chunkPositions = new HashSet<>();
	
	public StrongholdFile(){
		super("strongholds.nbt");
	}
	
	public void addChunkPos(int chunkX, int chunkZ){
		chunkPositions.add(Pos.at(chunkX,0,chunkZ));
		setModified();
	}
	
	public boolean checkChunkPos(int chunkX, int chunkZ){
		return chunkPositions.contains(Pos.at(chunkX,0,chunkZ));
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		NBTUtil.writeList(nbt,"list",chunkPositions.stream().map(pos -> new NBTTagLong(pos.toLong())));
	}
	
	@Override
	protected void onLoad(NBTTagCompound nbt){
		chunkPositions = NBTUtil.readNumericList(nbt,"list").map(tag -> Pos.at(tag.func_150291_c())).collect(Collectors.toSet());
	}
}
