package chylex.hee.mechanics.charms;
import static chylex.hee.mechanics.charms.RuneType.*;
import java.text.DecimalFormat;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.tuple.Pair;

public enum CharmType{
	BASIC_POWER(15, 0, new CharmRecipe[]{
		new CharmRecipe(0).rune(POWER,3).prop("dmg",1.25F),
		new CharmRecipe(1).rune(POWER,4).prop("dmg",1.45F),
		new CharmRecipe(2).rune(POWER,5).prop("dmg",1.60F)
	}, new String[]{ "perc-1,dmg" }),
	
	BASIC_AGILITY(2, 1, new CharmRecipe[]{
		new CharmRecipe(3).rune(AGILITY,3).prop("spd",1.15F),
		new CharmRecipe(4).rune(AGILITY,4).prop("spd",1.28F),
		new CharmRecipe(5).rune(AGILITY,5).prop("spd",1.40F)
	}, new String[]{ "perc-1,spd" }),
	
	BASIC_VIGOR(4, 2, new CharmRecipe[]{
		new CharmRecipe(6).rune(VIGOR,3).prop("regenspd",0.80F),
		new CharmRecipe(7).rune(VIGOR,4).prop("regenspd",0.60F),
		new CharmRecipe(8).rune(VIGOR,5).prop("regenspd",0.45F)
	}, new String[]{ "perc1-,regenspd" }),
	
	BASIC_DEFENSE(8, 3, new CharmRecipe[]{
		new CharmRecipe(9).rune(DEFENSE,3).prop("reducedmg",0.10F),
		new CharmRecipe(10).rune(DEFENSE,4).prop("reducedmg",0.20F),
		new CharmRecipe(11).rune(DEFENSE,5).prop("reducedmg",0.30F)
	}, new String[]{ "perc,reducedmg" }),
	
	BASIC_MAGIC(11, 7, new CharmRecipe[]{
		new CharmRecipe(12).rune(MAGIC,3).prop("exp",1.10F),
		new CharmRecipe(13).rune(MAGIC,4).prop("exp",1.20F),
		new CharmRecipe(14).rune(MAGIC,5).prop("exp",1.30F)
	}, new String[]{ "perc-1,exp" }),
	
	EQUALITY(16, 9, new CharmRecipe[]{
		new CharmRecipe(18).rune(POWER).rune(AGILITY).rune(VIGOR).rune(DEFENSE).rune(MAGIC).prop("dmg",1.08F).prop("spd",1.05F).prop("regenspd",0.94F).prop("reducedmg",0.03F).prop("exp",1.03F)
	}, new String[]{ "perc-1,dmg", "perc-1,spd", "perc1-,regenspd", "perc,reducedmg", "perc-1,exp" }),
	
	BLOCKING(0, 10, new CharmRecipe[]{
		new CharmRecipe(19).rune(POWER).rune(DEFENSE).rune(VOID).prop("reducedmgblock",0.20F),
		new CharmRecipe(20).rune(POWER).rune(DEFENSE,2).rune(VOID).prop("reducedmgblock",0.32F),
		new CharmRecipe(21).rune(POWER).rune(DEFENSE,3).rune(VOID).prop("reducedmgblock",0.45F)
	}, new String[]{ "perc,reducedmgblock" }),
	
	BLOCKING_REFLECTION(1, 10, new CharmRecipe[]{
		new CharmRecipe(22).rune(POWER).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.20F).prop("blockreflectdmg",0.10F),
		new CharmRecipe(23).rune(POWER).rune(DEFENSE).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.32F).prop("blockreflectdmg",0.10F),
		new CharmRecipe(24).rune(POWER).rune(POWER).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.20F).prop("blockreflectdmg",0.20F)
	}, new String[]{ "perc,reducedmgblock", "perc,blockreflectdmg" }),
	
	BLOCKING_REPULSION(2, 10, new CharmRecipe[]{
		new CharmRecipe(71).rune(POWER).rune(DEFENSE).rune(VOID).rune(AGILITY).prop("reducedmgblock",0.20F).prop("blockrepulsepower",1),
		new CharmRecipe(72).rune(POWER).rune(DEFENSE,2).rune(VOID).rune(AGILITY).prop("reducedmgblock",0.32F).prop("blockrepulsepower",1),
		new CharmRecipe(73).rune(POWER).rune(DEFENSE).rune(VOID).rune(AGILITY,2).prop("reducedmgblock",0.20F).prop("blockrepulsepower",2),
	}, new String[]{ "perc,reducedmgblock", "int,blockrepulsepower" }),
	
	DIGESTIVE_RECOVER(3, 5, new CharmRecipe[]{
		new CharmRecipe(25).rune(POWER).rune(VIGOR).rune(VOID).prop("healthperhunger",0.50F),
		new CharmRecipe(26).rune(POWER).rune(VIGOR,2).rune(VOID).prop("healthperhunger",1.00F),
		new CharmRecipe(27).rune(POWER).rune(VIGOR,3).rune(VOID).prop("healthperhunger",2.00F)
	}, new String[]{ "flopb,healthperhunger" }),
	
	LIFE_STEAL(5, 11, new CharmRecipe[]{
		new CharmRecipe(28).rune(POWER).rune(VIGOR).rune(MAGIC).rune(VOID).prop("stealhealth",2).prop("stealdealt",16),
		new CharmRecipe(29).rune(POWER).rune(VIGOR,2).rune(MAGIC).rune(VOID).prop("stealhealth",4).prop("stealdealt",16),
		new CharmRecipe(30).rune(POWER,2).rune(VIGOR,2).rune(VOID).prop("stealhealth",2).prop("stealdealt",10)
	}, new String[]{ "int,stealhealth", "int,stealdealt" }),
	
	DAMAGE_REDIRECTION(14, 3, new CharmRecipe[]{
		new CharmRecipe(31).rune(AGILITY).rune(DEFENSE).rune(MAGIC).prop("rediramt",0.15F).prop("redirmobs",1),
		new CharmRecipe(32).rune(AGILITY,2).rune(DEFENSE).rune(MAGIC).prop("rediramt",0.12F).prop("redirmobs",2),
		new CharmRecipe(33).rune(AGILITY,3).rune(DEFENSE).rune(MAGIC).prop("rediramt",0.14F).prop("redirmobs",3),
		new CharmRecipe(34).rune(AGILITY).rune(DEFENSE,2).rune(MAGIC).prop("rediramt",0.20F).prop("redirmobs",1),
		new CharmRecipe(35).rune(AGILITY,2).rune(DEFENSE,2).rune(MAGIC).prop("rediramt",0.18F).prop("redirmobs",2),
		new CharmRecipe(36).rune(AGILITY).rune(DEFENSE,3).rune(MAGIC).prop("rediramt",0.30F).prop("redirmobs",1)
	}, new String[]{ "perc,rediramt", "int,redirmobs" }),
	
	MAGIC_PENETRATION(13, 4, new CharmRecipe[]{
		new CharmRecipe(37).rune(POWER,2).rune(MAGIC).prop("dmgtomagic",0.10F),
		new CharmRecipe(38).rune(POWER,3).rune(MAGIC).prop("dmgtomagic",0.15F),
		new CharmRecipe(39).rune(POWER,4).rune(MAGIC).prop("dmgtomagic",0.20F),
		new CharmRecipe(40).rune(POWER,3).rune(MAGIC,2).prop("dmgtomagic",0.25F),
		new CharmRecipe(41).rune(POWER).rune(MAGIC,2).prop("dmgtomagic",0.15F),
		new CharmRecipe(42).rune(POWER).rune(MAGIC,3).prop("dmgtomagic",0.25F),
		new CharmRecipe(43).rune(POWER).rune(MAGIC,4).prop("dmgtomagic",0.32F)
	}, new String[]{ "perc,dmgtomagic" }),
	
	WITCHERY_HARM(12, 4, new CharmRecipe[]{
		new CharmRecipe(44).rune(POWER).rune(MAGIC).rune(VIGOR).rune(VOID).prop("badeffchance",0.08F).prop("badefflvl",1).prop("badefftime",5),
		new CharmRecipe(45).rune(POWER).rune(MAGIC).rune(VIGOR,2).rune(VOID).prop("badeffchance",0.09F).prop("badefflvl",2).prop("badefftime",5),
		new CharmRecipe(46).rune(POWER).rune(MAGIC,2).rune(VIGOR).rune(VOID).prop("badeffchance",0.14F).prop("badefflvl",1).prop("badefftime",5),
		new CharmRecipe(65).rune(POWER,2).rune(MAGIC).rune(VIGOR).rune(VOID).prop("badeffchance",0.09F).prop("badefflvl",1).prop("badefftime",10)
	}, new String[]{ "perc,badeffchance", "int,badefflvl", "int,badefftime" }),
	
	/*WITCHERY_DEFENSE(9, 5, new CharmRecipe[]{
		new CharmRecipe(47).rune(MAGIC).rune(VIGOR).rune(VOID).rune(DEFENSE).prop("badeffreduce",10),
		new CharmRecipe(48).rune(MAGIC).rune(VIGOR).rune(VOID).rune(DEFENSE,2).prop("badeffreduce",15)
	}, ""),*/
	
	MAGIC_DEFENSE(10, 3, new CharmRecipe[]{
		new CharmRecipe(74).rune(DEFENSE).rune(MAGIC).rune(VOID).prop("reducemagicdmg",0.30F),
		new CharmRecipe(75).rune(DEFENSE).rune(MAGIC).rune(VOID).prop("reducemagicdmg",0.50F),
		new CharmRecipe(76).rune(DEFENSE).rune(MAGIC).rune(VOID).prop("reducemagicdmg",0.70F)
	}, new String[]{ "perc,reducemagicdmg" }),
	
	FALLING_PROTECTION(10, 6, new CharmRecipe[]{
		new CharmRecipe(49).rune(AGILITY,3).rune(DEFENSE).prop("fallblocks",3),
		new CharmRecipe(50).rune(AGILITY,4).rune(DEFENSE).prop("fallblocks",6)
	}, new String[]{ "int,fallblocks" }),
	
	HASTE(1, 8, new CharmRecipe[]{
		new CharmRecipe(51).rune(AGILITY,2).rune(VOID).prop("breakspd",1.10F),
		new CharmRecipe(52).rune(AGILITY,3).rune(VOID).prop("breakspd",1.18F),
		new CharmRecipe(53).rune(AGILITY,4).rune(VOID).prop("breakspd",1.25F)
	}, new String[]{ "perc-1,breakspd" }),
	
	CRITICAL_STRIKE(14, 12, new CharmRecipe[]{
		new CharmRecipe(54).rune(POWER,2).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",1.50F),
		new CharmRecipe(55).rune(POWER,3).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",2.00F),
		new CharmRecipe(56).rune(POWER,4).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",2.60F),
		new CharmRecipe(57).rune(POWER,2).rune(AGILITY,2).prop("critchance",0.15F).prop("critdmg",1.50F),
		new CharmRecipe(58).rune(POWER,2).rune(AGILITY,3).prop("critchance",0.22F).prop("critdmg",1.50F),
		new CharmRecipe(59).rune(POWER,3).rune(AGILITY,3).prop("critchance",0.15F).prop("critdmg",2.00F)
	}, new String[]{ "perc,critchance", "perc,critdmg" }),
	
	SECOND_DURABILITY(12, 8, new CharmRecipe[]{
		new CharmRecipe(60).rune(VIGOR).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.28F).prop("recdurabilityamt",0.10F),
		new CharmRecipe(61).rune(VIGOR,2).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.25F).prop("recdurabilityamt",0.18F),
		new CharmRecipe(62).rune(VIGOR,3).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.22F).prop("recdurabilityamt",0.25F),
		new CharmRecipe(63).rune(VIGOR).rune(MAGIC,2).rune(VOID).prop("recdurabilitychance",0.32F).prop("recdurabilityamt",0.16F),
		new CharmRecipe(64).rune(VIGOR).rune(MAGIC,3).rune(VOID).prop("recdurabilitychance",0.44F).prop("recdurabilityamt",0.12F)
	}, new String[]{ "perc,recdurabilitychance", "perc,recdurabilityamt" }),
	
	/*VOID_RESCUE(16, 0, new CharmRecipe[]{
		new CharmRecipe(15).rune(DEFENSE).rune(VOID,3).prop("voidrescue",30),
		new CharmRecipe(16).rune(DEFENSE,2).rune(VOID,3).prop("voidrescue",20)		
	}, ""),*/
	
	SLAUGHTER_IMPACT(15, 12, new CharmRecipe[]{
		new CharmRecipe(17).rune(POWER).rune(MAGIC).rune(VOID).prop("impactamt",0.60F).prop("impactrad",4F),
		new CharmRecipe(66).rune(POWER,2).rune(MAGIC).rune(VOID).prop("impactamt",0.90F).prop("impactrad",4F),
		new CharmRecipe(67).rune(POWER,3).rune(MAGIC).rune(VOID).prop("impactamt",1.40F).prop("impactrad",4F),
		new CharmRecipe(68).rune(POWER).rune(MAGIC,2).rune(VOID).prop("impactamt",0.70F).prop("impactrad",6F),
		new CharmRecipe(69).rune(POWER).rune(MAGIC,3).rune(VOID).prop("impactamt",0.70F).prop("impactrad",8F),
		new CharmRecipe(70).rune(POWER,2).rune(MAGIC,2).rune(VOID).prop("impactamt",1.00F).prop("impactrad",6F)
	}, new String[]{ "perc,impactamt", "int,impactrad" }),
	
	LAST_RESORT(9, 7, new CharmRecipe[]{
		new CharmRecipe(77).rune(DEFENSE).rune(AGILITY).rune(MAGIC).rune(VOID).prop("lastresortblocks",6).prop("lastresortcooldown",10),
		new CharmRecipe(78).rune(DEFENSE).rune(AGILITY,2).rune(MAGIC).rune(VOID).prop("lastresortblocks",6).prop("lastresortcooldown",6),
		new CharmRecipe(79).rune(DEFENSE).rune(AGILITY).rune(MAGIC,2).rune(VOID).prop("lastresortblocks",12).prop("lastresortcooldown",10),
	}, new String[]{ "int,lastresortblocks", "int,lastresortcooldown" });
	
	// last used id: 79

	private static final DecimalFormat formatOnePlace = new DecimalFormat("0.0");
	private static final DecimalFormat formatTwoPlaces = new DecimalFormat("0.00");
	
	public static Pair<CharmType,CharmRecipe> getFromDamage(int damage){
		for(CharmType type:values()){
			for(CharmRecipe recipe:type.recipes){
				if (recipe.id == damage)return Pair.of(type,recipe);
			}
		}
		
		return Pair.of(null,null);
	}
	
	public static Pair<CharmType,CharmRecipe> findRecipe(RuneType[] runes){
		if (runes.length < 3 || runes.length > 5)return null;
		
		for(CharmType type:values()){
			for(CharmRecipe recipe:type.recipes){
				if (recipe.checkRunes(runes))return Pair.of(type,recipe);
			}
		}
		
		return Pair.of(null,null);
	}
	
	public static String getTooltip(int damage){
		for(CharmType type:values()){
			for(CharmRecipe recipe:type.recipes){
				if (recipe.id == damage){
					String tooltip = StatCollector.translateToLocal(new StringBuilder().append("item.charm.").append(type.name().toLowerCase().replace("_","")).append(".tooltip").toString());
					
					for(int arg = 0; arg < type.tooltipArgs.length; arg++){
						String[] args = type.tooltipArgs[arg].split(",");
						if (args.length < 2)continue;
						
						String replacement;
						
						switch(args[0]){
							case "float": replacement = formatTwoPlaces.format(recipe.getProp(args[1])); break;
							case "floatnf": replacement = String.valueOf(recipe.getProp(args[1])); break;
							case "flopb": float val = recipe.getProp(args[1]); replacement = val < 1F ? formatOnePlace.format(val) : String.valueOf(Math.round(val)); break;
							case "perc": replacement = Math.round(100F*recipe.getProp(args[1]))+"%"; break;
							case "perc1-": replacement = Math.round(100F*(1F-recipe.getProp(args[1])))+"%"; break;
							case "perc-1": replacement = Math.round(100F*(recipe.getProp(args[1])-1F))+"%"; break;
							case "int": replacement = String.valueOf(Math.round(recipe.getProp(args[1]))); break;
							default: continue;
						}
						
						tooltip = tooltip.replace("$"+arg,replacement);
					}
					
					if (!type.canBeStacked())tooltip += "\\n\u00a78"+StatCollector.translateToLocal("item.charm.cannotstack");
					
					return tooltip;
				}
			}
		}
		
		return "";
	}
	
	public final CharmRecipe[] recipes;
	public final byte backIcon;
	public final byte foregroundIcon;
	private final String[] tooltipArgs;
	
	CharmType(int backIcon, int foregroundIcon, CharmRecipe[] recipes, String[] tooltipArgs){
		this.recipes = recipes;
		this.backIcon = (byte)backIcon;
		this.foregroundIcon = (byte)foregroundIcon;
		this.tooltipArgs = tooltipArgs;
	}
	
	public boolean canBeStacked(){
		return this != LIFE_STEAL && this != LAST_RESORT;
	}
}
