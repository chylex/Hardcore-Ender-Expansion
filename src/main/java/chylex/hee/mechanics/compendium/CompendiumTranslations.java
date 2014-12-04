package chylex.hee.mechanics.compendium;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.util.StringTranslate;
import org.apache.commons.io.FileUtils;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class CompendiumTranslations{
	private static final Pattern ecLangPattern = Pattern.compile("assets/hardcoreenderexpansion/lang_ec/(.*?)\\.lang");
	
	public static void load(File sourceFile){
		Stopwatch.time("CompendiumTranslations - load");
		
		if (sourceFile.isDirectory()){
			try{
				for(File file:FileUtils.listFiles(sourceFile,new String[]{ "lang" },true)){
					String path = file.getAbsolutePath().replace('\\','/');
					
					int index = path.indexOf("assets/hardcoreenderexpansion/lang_ec/");
					if (index == -1)continue;
					
					Matcher match = ecLangPattern.matcher(path.substring(index));
					if (match.matches())LanguageRegistry.instance().injectLanguage(match.group(1),StringTranslate.parseLangFile(new FileInputStream(file)));
				}
			}
			catch(Exception e){
				Log.throwable(e,"Could not load Compendium language files.");
			}
		}
		else{
			try(ZipFile zip = new ZipFile(sourceFile)){
				for(ZipEntry entry:Collections.list(zip.entries())){
					Matcher match = ecLangPattern.matcher(entry.getName());
					if (match.matches())LanguageRegistry.instance().injectLanguage(match.group(1),StringTranslate.parseLangFile(zip.getInputStream(entry)));
				}
			}catch(Exception e){
				Log.throwable(e,"Could not load Compendium language files.");
			}
		}
		
		Stopwatch.finish("CompendiumTranslations - load");
	}
	
	private CompendiumTranslations(){}
}
