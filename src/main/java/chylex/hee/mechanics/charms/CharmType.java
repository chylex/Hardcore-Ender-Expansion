package chylex.hee.mechanics.charms;
import static chylex.hee.mechanics.charms.RuneType.*;
import org.apache.commons.lang3.tuple.Pair;

public enum CharmType{	
	BASIC_POWER(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(0).rune(POWER,3).prop("dmg",1.25F),
		new CharmRecipe(1).rune(POWER,4).prop("dmg",1.45F),
		new CharmRecipe(2).rune(POWER,5).prop("dmg",1.60F)
	}),
	
	BASIC_AGILITY(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(3).rune(AGILITY,3).prop("spd",1.15F),
		new CharmRecipe(4).rune(AGILITY,4).prop("spd",1.28F),
		new CharmRecipe(5).rune(AGILITY,5).prop("spd",1.40F)
	}),
	
	BASIC_VIGOR(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(6).rune(VIGOR,3).prop("regenspd",20),
		new CharmRecipe(7).rune(VIGOR,4).prop("regenspd",40),
		new CharmRecipe(8).rune(VIGOR,5).prop("regenspd",55)
	}),
	
	BASIC_DEFENSE(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(9).rune(DEFENSE,3).prop("reducedmg",0.10F),
		new CharmRecipe(10).rune(DEFENSE,4).prop("reducedmg",0.20F),
		new CharmRecipe(11).rune(DEFENSE,5).prop("reducedmg",0.30F)
	}),
	
	BASIC_MAGIC(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(12).rune(MAGIC,3).prop("exp",1.10F),
		new CharmRecipe(13).rune(MAGIC,4).prop("exp",1.20F),
		new CharmRecipe(14).rune(MAGIC,5).prop("exp",1.30F)
	}),
	
	BASIC_VOID(0,0x000000,0x000000,new CharmRecipe[]{
		
	}),
	
	EQUALITY(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(18).rune(POWER).rune(AGILITY).rune(VIGOR).rune(DEFENSE).rune(MAGIC).prop("dmg",1.08F).prop("spd",1.05F).prop("regenspd",6).prop("reducedmg",0.03F).prop("exp",1.03F)
	}),
	
	BLOCKING(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(19).rune(POWER).rune(DEFENSE).rune(VOID).prop("reducedmgblock",0.20F),
		new CharmRecipe(20).rune(POWER).rune(DEFENSE,2).rune(VOID).prop("reducedmgblock",0.32F),
		new CharmRecipe(21).rune(POWER).rune(DEFENSE,3).rune(VOID).prop("reducedmgblock",0.45F)
	}),
	
	BLOCKING_REFLECTION(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(22).rune(POWER).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.20F).prop("blockreflectdmg",0.10F),
		new CharmRecipe(23).rune(POWER).rune(DEFENSE).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.32F).prop("blockreflectdmg",0.10F),
		new CharmRecipe(24).rune(POWER).rune(POWER).rune(DEFENSE).rune(VOID).rune(MAGIC).prop("reducedmgblock",0.20F).prop("blockreflectdmg",0.20F)
	}),
	
	DIGESTIVE_RECOVER(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(25).rune(POWER).rune(VIGOR).rune(VOID).prop("healthperhunger",5), // x10
		new CharmRecipe(26).rune(POWER).rune(VIGOR,2).rune(VOID).prop("healthperhunger",10),
		new CharmRecipe(27).rune(POWER).rune(VIGOR,3).rune(VOID).prop("healthperhunger",20)
	}),
	
	LIFE_STEAL(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(28).rune(POWER).rune(VIGOR).rune(MAGIC).rune(VOID).prop("stealhealth",2).prop("stealdealt",10),
		new CharmRecipe(29).rune(POWER).rune(VIGOR,2).rune(MAGIC).rune(VOID).prop("stealhealth",4).prop("stealdealt",10),
		new CharmRecipe(30).rune(POWER,2).rune(VIGOR,2).rune(VOID).prop("stealhealth",2).prop("stealdealt",8)
	}),
	
	DAMAGE_REDIRECTION(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(31).rune(AGILITY).rune(DEFENSE).rune(MAGIC).prop("rediramt",15).prop("redirmobs",1),
		new CharmRecipe(32).rune(AGILITY,2).rune(DEFENSE).rune(MAGIC).prop("rediramt",12).prop("redirmobs",2),
		new CharmRecipe(33).rune(AGILITY,3).rune(DEFENSE).rune(MAGIC).prop("rediramt",14).prop("redirmobs",3),
		new CharmRecipe(34).rune(AGILITY).rune(DEFENSE,2).rune(MAGIC).prop("rediramt",20).prop("redirmobs",1),
		new CharmRecipe(35).rune(AGILITY,2).rune(DEFENSE,2).rune(MAGIC).prop("rediramt",18).prop("redirmobs",2),
		new CharmRecipe(36).rune(AGILITY).rune(DEFENSE,3).rune(MAGIC).prop("rediramt",30).prop("redirmobs",1)
	}),
	
	MAGIC_PENETRATION(0,0x000000,0x000000,new CharmRecipe[]{ // TODO check balance
		new CharmRecipe(37).rune(POWER,2).rune(MAGIC).prop("dmgtomagic",10),
		new CharmRecipe(38).rune(POWER,3).rune(MAGIC).prop("dmgtomagic",15),
		new CharmRecipe(39).rune(POWER,4).rune(MAGIC).prop("dmgtomagic",20),
		new CharmRecipe(40).rune(POWER,3).rune(MAGIC,2).prop("dmgtomagic",25),
		new CharmRecipe(41).rune(POWER).rune(MAGIC,2).prop("dmgtomagic",15),
		new CharmRecipe(42).rune(POWER).rune(MAGIC,3).prop("dmgtomagic",25),
		new CharmRecipe(43).rune(POWER).rune(MAGIC,4).prop("dmgtomagic",32)
	}),
	
	WITCHERY_HARM(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(44).rune(POWER).rune(MAGIC).rune(VIGOR).rune(VOID).prop("badeffchance",0.08F).prop("badefflvl",1).prop("badefftime",5),
		new CharmRecipe(45).rune(POWER).rune(MAGIC).rune(VIGOR,2).rune(VOID).prop("badeffchance",0.09F).prop("badefflvl",2).prop("badefftime",5),
		new CharmRecipe(46).rune(POWER).rune(MAGIC,2).rune(VIGOR).rune(VOID).prop("badeffchance",0.14F).prop("badefflvl",1).prop("badefftime",5),
		new CharmRecipe(65).rune(POWER,2).rune(MAGIC).rune(VIGOR).rune(VOID).prop("baddeffchance",0.09F).prop("badefflvl",1).prop("badefftime",10)
	}),
	
	WITCHERY_DEFENSE(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(47).rune(MAGIC).rune(VIGOR).rune(VOID).rune(DEFENSE).prop("badeffreduce",10),
		new CharmRecipe(48).rune(MAGIC).rune(VIGOR).rune(VOID).rune(DEFENSE,2).prop("badeffreduce",15)
	}),
	
	FALLING_PROTECTION(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(49).rune(AGILITY,3).rune(DEFENSE).prop("fallblocks",3),
		new CharmRecipe(50).rune(AGILITY,4).rune(DEFENSE).prop("fallblocks",6)
	}),
	
	HASTE(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(51).rune(AGILITY,2).rune(VOID).prop("breakspd",10),
		new CharmRecipe(52).rune(AGILITY,3).rune(VOID).prop("breakspd",18),
		new CharmRecipe(53).rune(AGILITY,4).rune(VOID).prop("breakspd",25)
	}),
	
	CRITICAL_STRIKE(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(54).rune(POWER,2).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",1.50F),
		new CharmRecipe(55).rune(POWER,3).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",2.00F),
		new CharmRecipe(56).rune(POWER,4).rune(AGILITY).prop("critchance",0.10F).prop("critdmg",2.60F),
		new CharmRecipe(57).rune(POWER,2).rune(AGILITY,2).prop("critchance",0.15F).prop("critdmg",1.50F),
		new CharmRecipe(58).rune(POWER,2).rune(AGILITY,3).prop("critchance",0.22F).prop("critdmg",1.50F),
		new CharmRecipe(59).rune(POWER,3).rune(AGILITY,3).prop("critchance",0.15F).prop("critdmg",2.00F)
	}),
	
	SECOND_DURABILITY(0,0x000000,0x000000,new CharmRecipe[]{
		new CharmRecipe(60).rune(VIGOR).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.28F).prop("recdurabilityamt",0.10F),
		new CharmRecipe(61).rune(VIGOR,2).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.25F).prop("recdurabilityamt",0.18F),
		new CharmRecipe(62).rune(VIGOR,3).rune(MAGIC).rune(VOID).prop("recdurabilitychance",0.22F).prop("recdurabilityamt",0.25F),
		new CharmRecipe(63).rune(VIGOR).rune(MAGIC,2).rune(VOID).prop("recdurabilitychance",0.32F).prop("recdurabilityamt",0.16F),
		new CharmRecipe(64).rune(VIGOR).rune(MAGIC,3).rune(VOID).prop("recdurabilitychance",0.44F).prop("recdurabilityamt",0.12F)
	});
	
	// last used id: 65
	
	public static Pair<CharmType,CharmRecipe> getFromDamage(int damage){
		for(CharmType type:values()){
			for(CharmRecipe recipe:type.recipes){
				if (recipe.id == damage)return Pair.of(type,recipe);
			}
		}
		
		return null;
	}
	
	public static Pair<CharmType,CharmRecipe> findRecipe(RuneType[] runes){
		if (runes.length < 3 || runes.length > 5)return null;
		
		for(CharmType type:values()){
			for(CharmRecipe recipe:type.recipes){
				if (recipe.checkRunes(runes))return Pair.of(type,recipe);
			}
		}
		
		return null;
	}
	
	private final CharmRecipe[] recipes;
	public final byte foregroundIcon;
	public final int dropColor;
	public final int foreColor;
	
	CharmType(int foregroundIcon, int dropColor, int foreColor, CharmRecipe...recipes){
		this.recipes = recipes;
		this.foregroundIcon = (byte)foregroundIcon;
		this.dropColor = dropColor;
		this.foreColor = foreColor;
	}
}
