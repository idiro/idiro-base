package com.idiro.utils;

import java.util.Random;

public class RandomString {

	public static String getRandomName(int length){
		return getRandomName(length, 
				"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
	}
	
	public static String getRandomName(int length, String characters){
		Random rnd = new Random();
		char[] new_name = new char[length];
		for (int i = 0; i < length; ++i){
			new_name[i] =  characters.charAt(rnd.nextInt(characters.length()));
		}
		return new String(new_name);
	}
	
	public static String getRandomNameStartByLetter(int length){
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		Random rnd = new Random();
		char[] new_name = new char[length];
		new_name[0] =  characters.charAt(rnd.nextInt(characters.length()-10));
		for (int i = 1; i < length; i++){
			new_name[i] =  characters.charAt(rnd.nextInt(characters.length()));
		}
		return new String(new_name);
	}
}
