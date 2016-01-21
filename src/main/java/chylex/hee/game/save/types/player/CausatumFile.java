package chylex.hee.game.save.types.player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.causatum.Causatum.Actions;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventTypes;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.collections.CollectionUtil;

public class CausatumFile extends PlayerFile{
	private final EnumSet<Actions> ranUniqueActions = EnumSet.noneOf(Actions.class);
	private final EnumSet<EventTypes> ranEvents = EnumSet.noneOf(EventTypes.class);
	private Progress progress = Progress.INITIAL;
	private int level;
	
	public CausatumFile(String filename){
		super("causatum",filename);
	}
	
	public boolean tryProgress(Progress target){
		if (target.ordinal() > progress.ordinal()){
			progress = target;
			setModified();
			return true;
		}
		else return false;
	}
	
	public boolean tryTrigger(Actions action){
		if (!action.canRepeat && ranUniqueActions.contains(action))return false;
		
		if (!action.canRepeat)ranUniqueActions.add(action);
		level += action.levelIncrease;
		setModified();
		return true;
	}
	
	public @Nullable EventTypes findRandomEvent(Random rand){
		Set<EventTypes> all = Arrays.stream(EventTypes.values()).filter(event -> event.canTrigger(progress,level) && event.isRandomEvent()).collect(Collectors.toSet());
		if (all.isEmpty())return null;
		
		List<EventTypes> available = new ArrayList<>(all);
		available.removeAll(ranEvents);
		
		if (available.isEmpty()){
			ranEvents.removeAll(all);
			setModified();
			available = new ArrayList<>(all);
		}
		
		return CollectionUtil.randomOrNull(available,rand);
	}
	
	public void finishEvent(EventTypes event){
		ranEvents.add(event);
		level -= event.requiredLevel*1.5F;
		setModified();
	}
	
	public Progress getStage(){
		return progress;
	}
	
	public int getLevel(){
		return level;
	}

	@Override
	protected void onSave(NBTCompound nbt){
		nbt.setInt("lvl",level);
		nbt.setByte("prog",(byte)progress.ordinal());
		nbt.writeList("uacts",ranUniqueActions.stream().map(Actions::name).map(NBTTagString::new));
		nbt.writeList("evts",ranEvents.stream().map(EventTypes::name).map(NBTTagString::new));
	}

	@Override
	protected void onLoad(NBTCompound nbt){
		level = nbt.getInt("lvl");
		progress = CollectionUtil.get(Progress.values(),nbt.getByte("prog")).orElse(Progress.INITIAL);
		nbt.getList("uacts").readStrings().map(name -> EnumUtils.getEnum(Actions.class,name)).filter(Objects::nonNull).forEach(ranUniqueActions::add);
		nbt.getList("evts").readStrings().map(name -> EnumUtils.getEnum(EventTypes.class,name)).filter(Objects::nonNull).forEach(ranEvents::add);
	}
}
