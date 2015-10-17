package chylex.hee.game.save.types.player;
import java.util.Optional;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.system.abstractions.Pos;

public class StrongholdFile extends PlayerFile{
	private Pos portalPos;
	
	public StrongholdFile(String filename){
		super("stronghold",filename);
	}
	
	public void setPortalPos(Pos pos){
		this.portalPos = pos.immutable();
		setModified();
	}
	
	public Optional<Pos> getPortalPos(){
		return Optional.ofNullable(portalPos);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setLong("portal",portalPos.toLong());
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		portalPos = nbt.hasKey("portal") ? Pos.at(nbt.getLong("portal")) : null;
	}
}
