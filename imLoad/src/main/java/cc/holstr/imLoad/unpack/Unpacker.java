package cc.holstr.imLoad.unpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class Unpacker {

	private static Path storePath; 
	private static Path config; 
	private static File configFile;
	
	public static String apikey;
	
	public static void unpack() {
		storePath  = Paths.get(System.getProperty("user.home"),".store","imLoad","data");
		config = Paths.get(storePath.toString(),"key.txt");
		configFile = config.toFile();
		if(!storePath.toFile().exists()) {
			try {
				Files.createDirectories(storePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Scanner s = new Scanner(configFile);
				if(s.hasNext()) {
					apikey = s.nextLine();
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void writeAPIKey(String apikey) {
		try {
			FileUtils.writeStringToFile(configFile, apikey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
