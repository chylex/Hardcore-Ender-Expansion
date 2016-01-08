package chylex.hee.world.end.server;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.global.WorldFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.tick.ITerritoryBehavior;

public final class TerritoryTicker{
	private final NBTTagCompound nbt;
	
	private final List<ITerritoryBehavior> behaviorList = new ArrayList<>(4);
	
	public TerritoryTicker(EndTerritory territory, Pos centerPos, long hash){
		WorldFile file = SaveData.global(WorldFile.class);
		
		this.nbt = file.getTerritoryData(hash);
		territory.properties.setupBehaviorList(behaviorList,territory,file.getTerritoryVariations(hash),centerPos,file.isTerritoryRare(hash));
	}
	
	public void onTick(World world){
		for(ITerritoryBehavior behavior:behaviorList){
			behavior.tick(world,nbt);
		}
	}
}
