package chylex.hee.entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.StatCollector;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Log;

public class RandomNameGenerator{
	private static final WeightedList<Char> vowels = new WeightedList<>(
		new Char('a',10), new Char('e',10), new Char('i',8), new Char('o',7), new Char('u',6), new Char('y',3)
	);
	
	private static final WeightedList<Char> consonants = new WeightedList<>(
		new Char('b',6), new Char('c',5), new Char('d',6), new Char('f',4), new Char('g',2), new Char('h',4),
		new Char('j',4), new Char('k',7), new Char('l',7), new Char('m',7), new Char('n',7), new Char('p',7),
		new Char('q',1), new Char('r',6), new Char('s',7), new Char('t',7), new Char('v',6), new Char('w',1),
		new Char('x',1), new Char('z',3)
	);
	
	private static final Map<Character,char[]> matches = new HashMap<>();
	
	static{
		matches.put('b', new char[]{ 'l','r' });
		matches.put('c', new char[]{ 'h','l','r','z' });
		matches.put('d', new char[]{ 'l','r' });
		matches.put('f', new char[]{ 'l','r' });
		matches.put('g', new char[]{ 'n','p','r' });
		matches.put('k', new char[]{ 'l','r' });
		matches.put('l', new char[]{ 'b','t' });
		matches.put('n', new char[]{ 'c','g' });
		matches.put('p', new char[]{ 'h','l','r' });
		matches.put('r', new char[]{ 'c','k','m','n','s' });
		matches.put('s', new char[]{ 'c','h','k','l','n','q','t' });
		matches.put('t', new char[]{ 'h','l','r','w' });
		matches.put('z', new char[]{ 'd' });
	}
	
	public static void generateEntityName(EntityLiving entity, int length){
		if (!entity.hasCustomName())entity.setCustomNameTag(generate(entity.getRNG(),length)+StatCollector.translateToLocal("namegenerator.middle")+entity.getName());
	}
	
	public static String generate(Random rand, int length){
		StringBuilder build = new StringBuilder();
		boolean useVowel = rand.nextInt(3) == 0;
		
		for(int a = 0; a < length; a++){
			if (useVowel){
				char c = vowels.getRandomItem(rand).character;
				build.append(a == 0 ? Character.toUpperCase(c) : c);
				useVowel = !useVowel;
			}
			else{
				char c = consonants.getRandomItem(rand).character;
				build.append(a == 0 ? Character.toUpperCase(c) : c);
				
				if (rand.nextInt(3) == 0 && a+1 < length && matches.containsKey(c)){
					char[] matchArray = matches.get(c);
					build.append(matchArray[rand.nextInt(matchArray.length)]);
					++a;
				}
				
				useVowel = !useVowel;
			}
		}
		
		return build.toString();
	}

	static class Char implements IWeightProvider{
		final char character;
		private byte weight;

		Char(char character, int weight){
			this.character = character;
			this.weight = (byte)weight;
		}

		@Override
		public int getWeight(){
			return weight;
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			for(int a = 0; a < 50; a++)Log.debug("> "+RandomNameGenerator.generate(world.rand,4+world.rand.nextInt(8)));
		}
	};
}
