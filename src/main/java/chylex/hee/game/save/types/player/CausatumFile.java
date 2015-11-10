package chylex.hee.game.save.types.player;
import java.util.EnumSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.causatum.Causatum.Actions;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.NBTUtil;

public class CausatumFile extends PlayerFile{
	private final EnumSet<Actions> ranUniqueActions = EnumSet.noneOf(Actions.class);
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
	
	public int getLevel(){
		return level;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setInteger("lvl",level);
		nbt.setByte("prog",(byte)progress.ordinal());
		NBTUtil.writeList(nbt,"uacts",ranUniqueActions.stream().map(action -> new NBTTagString(action.name())));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		level = nbt.getInteger("lvl");
		progress = CollectionUtil.get(Progress.values(),nbt.getByte("prog")).orElse(Progress.INITIAL);
		NBTUtil.readStringList(nbt,"uacts").map(name -> EnumUtils.getEnum(Actions.class,name)).filter(action -> action != null).forEach(ranUniqueActions::add);
	}
}
