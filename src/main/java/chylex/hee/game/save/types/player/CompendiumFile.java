package chylex.hee.game.save.types.player;
import gnu.trove.set.hash.TIntHashSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagString;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.util.KnowledgeSerialization;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompendiumFile extends PlayerFile{
	private int points;
	private final TIntHashSet secretFragments = new TIntHashSet();
	private final Set<KnowledgeObject<? extends IObjectHolder<?>>> discoveredObjects = new HashSet<>(32);
	
	public CompendiumFile(String filename){
		super("compendium",filename);
	}
	
	@SideOnly(Side.CLIENT)
	public CompendiumFile(NBTTagCompound nbt){
		super("","");
		onLoad(nbt);
	}
	
	public int getPoints(){
		return points;
	}
	
	public void givePoints(int amount){
		points = MathUtil.clamp(points+amount,0,Short.MAX_VALUE);
		setModified();
	}
	
	public void payPoints(int amount){
		points = Math.max(0,points-amount);
		setModified();
	}
	
	public void reset(){
		onLoad(new NBTTagCompound());
		setModified();
	}
	
	@Override
	public void onSave(NBTTagCompound nbt){
		nbt.setShort("pts",(short)points);
		nbt.setTag("sf",new NBTTagIntArray(secretFragments.toArray()));
		NBTUtil.writeList(nbt,"obj",discoveredObjects.stream().map(obj -> new NBTTagString(KnowledgeSerialization.serialize(obj))));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		points = nbt.getShort("pts");
		secretFragments.clear();
		secretFragments.addAll(nbt.getIntArray("sf"));
		discoveredObjects.clear();
		NBTUtil.readStringList(nbt,"obj").map(KnowledgeSerialization::deserialize).filter(Objects::nonNull).forEach(discoveredObjects::add);
	}
}
