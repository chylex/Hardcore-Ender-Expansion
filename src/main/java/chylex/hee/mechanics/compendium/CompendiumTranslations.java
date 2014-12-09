package chylex.hee.mechanics.compendium;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.util.StringTranslate;
import org.apache.commons.io.FilenameUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.registry.LanguageRegistry;

public final class CompendiumTranslations{
	private static final Pattern ecLangPattern = Pattern.compile("assets/hardcoreenderexpansion/lang_ec/(.*?)\\.lang");
	
	public static void load(){
		Stopwatch.time("CompendiumTranslations - load");
		
		File sourceFile = HardcoreEnderExpansion.sourceFile;
		
		if (sourceFile.isDirectory()){
			sourceFile = Paths.get(sourceFile.getPath(),"assets","hardcoreenderexpansion","lang_ec").toFile();
			
			try{
				for(File file:sourceFile.listFiles()){
					LanguageRegistry.instance().injectLanguage(FilenameUtils.removeExtension(file.getName()),StringTranslate.parseLangFile(new FileInputStream(file)));
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
