package com.mauvaisetroupe.synology;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class TestExplorer {

	public static void main(String[] args) throws Throwable {
		String pathToExplore = "\\\\DISKSTATION\\public\\DATA\\A.BACKUPER\\02-TO.CREATE.THUMB\\PHOTOS";
		//String pathToExplore = "C://Localapps";
		
		Path toto = Paths.get(pathToExplore);
		
		Files.walk(Paths.get(pathToExplore)).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path t) {
				System.out.println(t);
				
			}
			
		});
		
		System.out.println("777777777777777777777777777777777777777777777777777777777777777777");
		
		Files.walk(Paths.get(pathToExplore)).filter(Files::isRegularFile).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path t) {
				System.out.println(t);
				
			}
			
		});
				
		
		System.out.println("8888888888888888888888888888888888888888888888888888888888888888888888888888");
		
		Files.walk(Paths.get(pathToExplore)).filter(f -> f.toString().toLowerCase().endsWith("jpg")).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path t) {
				System.out.println(t);
				
			}
			
		});
		
		
		System.out.println("9999999999999999999999999999999999999999999999999999999999999999999999999999");
		
		Files.walk(Paths.get(pathToExplore)).filter(Files::isRegularFile).filter(f -> f.toString().toLowerCase().endsWith("jpg")).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path t) {
				System.out.println(t);
				
			}
			
		});

	}

}
