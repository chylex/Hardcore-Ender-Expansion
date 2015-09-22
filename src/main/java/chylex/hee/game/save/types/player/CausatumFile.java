package chylex.hee.game.save.types.player;
import java.util.EnumSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.causatum.CausatumData;
import chylex.hee.system.util.NBTUtil;

public class CausatumFile extends PlayerFile{
	private final EnumSet<CausatumData.Actions> ranUniqueActions = EnumSet.noneOf(CausatumData.Actions.class);
	private int level;
	
	public CausatumFile(String filename){
		super("causatum",filename);
	}
	
	public void trigger(CausatumData.Actions action){
		if (!action.canRepeat && ranUniqueActions.contains(action))return;
		
		if (!action.canRepeat)ranUniqueActions.add(action);
		level += action.levelIncrease;
		setModified();
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setInteger("lvl",level);
		NBTUtil.writeList(nbt,"uacts",ranUniqueActions.stream().map(action -> new NBTTagString(action.name())));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		level = nbt.getInteger("lvl");
		NBTUtil.readStringList(nbt,"uacts").map(name -> EnumUtils.getEnum(CausatumData.Actions.class,name)).filter(action -> action != null).forEach(ranUniqueActions::add);
	}
}
