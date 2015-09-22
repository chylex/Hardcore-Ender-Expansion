package chylex.hee.game.save.types.player;
import java.util.EnumSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.causatum.Causatum;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;

public class CausatumFile extends PlayerFile{
	private final EnumSet<Causatum.Actions> ranUniqueActions = EnumSet.noneOf(Causatum.Actions.class);
	private Causatum.Progress progress = Causatum.Progress.START;
	private int level;
	
	public CausatumFile(String filename){
		super("causatum",filename);
	}
	
	public boolean tryProgress(Causatum.Progress target){
		if (target.ordinal() > progress.ordinal()){
			progress = target;
			setModified();
			return true;
		}
		else return false;
	}
	
	public void trigger(Causatum.Actions action){
		if (!action.canRepeat && ranUniqueActions.contains(action))return;
		
		if (!action.canRepeat)ranUniqueActions.add(action);
		level += action.levelIncrease;
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
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		level = nbt.getInteger("lvl");
		progress = Causatum.Progress.values()[MathUtil.clamp(nbt.getByte("prog"),0,Causatum.Progress.values().length-1)];
		NBTUtil.readStringList(nbt,"uacts").map(name -> EnumUtils.getEnum(Causatum.Actions.class,name)).filter(action -> action != null).forEach(ranUniqueActions::add);
	}
}
