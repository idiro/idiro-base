/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Utility Library developed by Idiro
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Utility Library developed by Idiro IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */

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
