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
	private final EndTerritory territory;
	private final Pos centerPos;
	private final NBTTagCompound nbt;
	
	private final List<ITerritoryBehavior> behaviorList = new ArrayList<>(4);
	
	public TerritoryTicker(EndTerritory territory, Pos centerPos, long hash){
		this.territory = territory;
		this.centerPos = centerPos;
		this.nbt = SaveData.global(WorldFile.class).getTerritoryData(hash);
		
		// TODO territory.properties.setupBehaviorList(behaviorList,variations,isRare);
	}
	
	public void onTick(World world){
		for(ITerritoryBehavior behavior:behaviorList){
			behavior.tick(territory,centerPos,nbt,world);
		}
	}
}
