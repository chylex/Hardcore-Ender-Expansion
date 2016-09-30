package chylex.hee.game.save.types.global;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import chylex.hee.game.save.SaveFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public class StrongholdFile extends SaveFile{
	private Set<Pos> chunkPositions = new HashSet<>();
	
	public StrongholdFile(){
		super("strongholds.nbt");
	}
	
	public void addChunkPos(int chunkX, int chunkZ){
		chunkPositions.add(Pos.at(chunkX, 0, chunkZ));
		setModified();
	}
	
	public boolean checkChunkPos(int chunkX, int chunkZ){
		return chunkPositions.contains(Pos.at(chunkX, 0, chunkZ));
	}

	@Override
	protected void onSave(NBTCompound nbt){
		nbt.writeList("list", chunkPositions.stream().mapToLong(Pos::toLong));
	}
	
	@Override
	protected void onLoad(NBTCompound nbt){
		chunkPositions = nbt.getList("list").readLongs().mapToObj(Pos::at).collect(Collectors.toSet());
	}
}
