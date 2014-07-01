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
