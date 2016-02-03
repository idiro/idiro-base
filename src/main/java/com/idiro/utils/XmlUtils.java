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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
	
	public static void checkForNullTextNodes(Node e,String path) throws Exception{
		 if (e.getNodeType() == org.w3c.dom.Node.TEXT_NODE && e.getNodeValue() == null) {
		     throw new Exception("Text node with null content: " +path+"/"+e.getNodeName());
		 }else if(e.getNodeType() == Node.ELEMENT_NODE){
			 path += "/"+e.getNodeName();
			 NodeList nodeList = e.getChildNodes();
			 for (int i = 0; i < nodeList.getLength(); i++) {
				 checkForNullTextNodes(nodeList.item(i),path);
			 }
		 }
	}

}
