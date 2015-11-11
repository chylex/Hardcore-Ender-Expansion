package chylex.hee.game.save.types.player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.causatum.Causatum.Actions;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventTypes;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.NBTUtil;

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
	
	public int getLevel(){
		return level;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setInteger("lvl",level);
		nbt.setByte("prog",(byte)progress.ordinal());
		NBTUtil.writeList(nbt,"uacts",ranUniqueActions.stream().map(action -> new NBTTagString(action.name())));
		NBTUtil.writeList(nbt,"evts",ranEvents.stream().map(event -> new NBTTagString(event.name())));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		level = nbt.getInteger("lvl");
		progress = CollectionUtil.get(Progress.values(),nbt.getByte("prog")).orElse(Progress.INITIAL);
		NBTUtil.readStringList(nbt,"uacts").map(name -> EnumUtils.getEnum(Actions.class,name)).filter(action -> action != null).forEach(ranUniqueActions::add);
		NBTUtil.readStringList(nbt,"evts").map(name -> EnumUtils.getEnum(EventTypes.class,name)).filter(event -> event != null).forEach(ranEvents::add);
	}
}
