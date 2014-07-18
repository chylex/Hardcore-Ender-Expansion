package chylex.hee.mechanics.minions;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.handlers.AbstractAbilityHandler;
import chylex.hee.mechanics.minions.properties.MinionAbilities;
import chylex.hee.mechanics.minions.properties.MinionAttributes;
import chylex.hee.mechanics.minions.properties.MinionModifiers;
import chylex.hee.mechanics.minions.properties.MinionObsidianBase;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class MinionData{
	private final EntityMobMinion minion;
	private MinionObsidianBase base;
	private EnumMap<MinionModifiers,Byte> modifiers = new EnumMap<>(MinionModifiers.class);
	private TreeMap<MinionAbilities,AbstractAbilityHandler> abilities = new TreeMap<>();
	private EnumMap<MinionAttributes,Byte> attributes = new EnumMap<>(MinionAttributes.class);
	
	public MinionData(MinionObsidianBase base){
		this.minion = null;
		this.base = base;
	}
	
	public MinionData(EntityMobMinion minion){
		this.minion = minion;
	}
	
	/*
	 * GETTERS
	 */
	
	public MinionObsidianBase getObsidianBase(){
		return base;
	}
	
	public int getModifiersLeft(){
		return Math.max(base.maxModifiers-modifiers.size(),0);
	}
	
	public int getAbilitiesLeft(){
		return Math.max(base.maxAbilities+getModifierCount(MinionModifiers.ADD_ABILITY)-abilities.size(),0);
	}
	
	public int getAttributesLeft(){
		return Math.max(base.maxAttributes+getModifierCount(MinionModifiers.ADD_ATTRIBUTE)-attributes.size(),0);
	}
	
	/*
	 * MODIFIERS
	 */
	
	public void addModifier(MinionModifiers modifier){
		Byte b = modifiers.get(modifier);
		modifiers.put(modifier,(byte)(b == null?1:(b+1)));
	}
	
	public int getModifierCount(MinionModifiers modifier){
		Byte b = modifiers.get(modifier);
		return b == null?0:b;
	}
	
	/*
	 * ABILITIES
	 */
	
	public void addAbility(MinionAbilities ability){
		abilities.put(ability,ability.createHandler(minion));
	}
	
	public boolean hasAbility(MinionAbilities ability){
		return abilities.containsKey(ability);
	}
	
	public void updateAbilities(){
		if (minion == null)return;
		for(AbstractAbilityHandler handler:abilities.values())handler.onUpdate();
	}
	
	public void updateAbilitiesOnDeath(){
		if (minion == null)return;
		for(AbstractAbilityHandler handler:abilities.values())handler.onDeath();
	}
	
	@SideOnly(Side.CLIENT)
	public void render(){
		if (minion == null)return;
		for(AbstractAbilityHandler handler:abilities.values())handler.render();
	}
	
	/*
	 * ATTRIBUTES
	 */
	
	public void addAttribute(MinionAttributes attribute, int level){
		attributes.put(attribute,(byte)level);
	}
	
	public void increaseAttribute(MinionAttributes attribute){
		Byte b = attributes.get(attribute);
		attributes.put(attribute,(byte)(b == null?1:(b+1)));
	}
	
	public byte getAttributeLevel(MinionAttributes attribute){
		Byte b = attributes.get(attribute);
		return b == null?0:b;
	}
	
	/*
	 * SAVING
	 */
	
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setString("base",base.name());
		
		NBTTagCompound tagModifiers = new NBTTagCompound(),
					   tagAbilities = new NBTTagCompound(),
					   tagAttributes = new NBTTagCompound();
		
		for(Entry<MinionModifiers,Byte> entry:modifiers.entrySet()){
			tagModifiers.setByte(entry.getKey().name(),entry.getValue());
		}
		nbt.setTag("modifiers",tagModifiers);
		
		for(Entry<MinionAbilities,AbstractAbilityHandler> entry:abilities.entrySet()){
			NBTTagCompound tagAbility = new NBTTagCompound();
			if (entry.getValue() != null)entry.getValue().writeDataToNBT(tagAbility);
			tagAbilities.setTag(entry.getKey().name(),tagAbility);
		}
		nbt.setTag("abilities",tagAbilities);
		
		for(Entry<MinionAttributes,Byte> entry:attributes.entrySet()){
			tagAttributes.setByte(entry.getKey().name(),entry.getValue());
		}
		nbt.setTag("attributes",tagAttributes);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		try{
			base = MinionObsidianBase.valueOf(nbt.getString("base"));
		}catch(Exception e){
			e.printStackTrace();
			DragonUtil.severe("Unknown obsidian base for minion!",nbt.getString("base"));
		}
		
		NBTTagCompound tagModifiers = nbt.getCompoundTag("modifiers"),
					   tagAbilities = nbt.getCompoundTag("abilities"),
					   tagAttributes = nbt.getCompoundTag("attributes");
		
		for(MinionModifiers modifier:MinionModifiers.values()){
			if (tagModifiers.hasKey(modifier.name()))modifiers.put(modifier,tagModifiers.getByte(modifier.name()));
		}
		
		for(MinionAbilities ability:MinionAbilities.values()){
			if (tagAbilities.hasKey(ability.name())){
				AbstractAbilityHandler handler = ability.createHandler(minion);
				if (handler != null)handler.readDataFromNBT(tagAbilities.getCompoundTag(ability.name()));
				abilities.put(ability,handler);
			}
		}

		for(MinionAttributes attribute:MinionAttributes.values()){
			if (tagAttributes.hasKey(attribute.name()))attributes.put(attribute,tagAttributes.getByte(attribute.name()));
		}
	}
}
