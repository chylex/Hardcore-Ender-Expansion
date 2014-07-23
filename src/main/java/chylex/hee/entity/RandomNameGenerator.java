package chylex.hee.entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.StatCollector;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;

public class RandomNameGenerator{
	private static final WeightedList<Char> samohlasky = new WeightedList<>(
		new Char('a',10), new Char('e',10), new Char('i',8), new Char('o',7), new Char('u',6), new Char('y',3)
	);
	
	private static final WeightedList<Char> souhlasky = new WeightedList<>(
		new Char('b',6), new Char('c',5), new Char('d',6), new Char('f',4), new Char('g',2), new Char('h',4),
		new Char('j',4), new Char('k',7), new Char('l',7), new Char('m',7), new Char('n',7), new Char('p',7),
		new Char('q',1), new Char('r',6), new Char('s',7), new Char('t',7), new Char('v',6), new Char('w',1),
		new Char('x',1), new Char('z',3)
	);
	
	private static final Map<Character,char[]> matches = new HashMap<>();
	
	static{
		matches.put('c', new char[]{ 'l','r','z' });
		matches.put('d', new char[]{ 'l','r' });
		matches.put('f', new char[]{ 'l','r' });
		matches.put('g', new char[]{ 'n','p','r' });
		matches.put('k', new char[]{ 'l','r' });
		matches.put('n', new char[]{ 'c','g' });
		matches.put('p', new char[]{ 'l','r' });
		matches.put('s', new char[]{ 'h','k','l','t' });
		matches.put('t', new char[]{ 'r' });
		matches.put('z', new char[]{ 'd' });
	}
	
	public static void generateEntityName(EntityLiving entity, int length){
		if (!entity.hasCustomNameTag())entity.setCustomNameTag(generate(entity.getRNG(),length)+StatCollector.translateToLocal("namegenerator.middle")+entity.getCommandSenderName());
	}
	
	public static String generate(Random rand, int length){
		StringBuilder build = new StringBuilder();
		boolean useVowel = false;
		
		for(int a = 0; a < length; ++a){
			if (useVowel){
				build.append(samohlasky.getRandomItem(rand).getChar());
				useVowel = !useVowel;
			}
			else{
				char c = souhlasky.getRandomItem(rand).getChar();
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
		private char character;
		private byte weight;

		Char(char character, int weight){
			this.character = character;
			this.weight = (byte)weight;
		}

		public char getChar(){
			return character;
		}

		@Override
		public int getWeight(){
			return weight;
		}
	}
}
