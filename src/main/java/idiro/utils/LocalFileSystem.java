package idiro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implements basic functionalities on the 
 * file system which does not exist by default
 * 
 * @author etienne
 *
 */
public class LocalFileSystem {

	/**
	 * The logger.
	 */
	private static Logger logger = Logger.getLogger(LocalFileSystem.class);

	public static void delete(File file)
			throws IOException{

		if(file.isDirectory()){

			//directory is empty, then delete it
			if(file.list().length==0){

				file.delete();
				logger.debug("Directory is deleted : " 
						+ file.getAbsolutePath());

			}else{

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					delete(fileDelete);
				}

				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
					logger.debug("Directory is deleted : " 
							+ file.getAbsolutePath());
				}
			}

		}else{
			//if file, then delete it
			file.delete();
			logger.debug("File is deleted : " + file.getAbsolutePath());
		}
	}

	public static boolean copyfile(String srFile, String dtFile){
		boolean ok = true;
		logger.debug("Try to copy: src "+srFile+" dest "+dtFile);

		File f1 = new File(srFile);
		File f2 = new File(dtFile);

		if(f1.isDirectory()){
			f2.mkdir();
			if(!f2.isDirectory()){
				logger.debug(srFile + " is a directory and "+dtFile+"is not");
				logger.error("fail to copy");
				return false;
			}
			File[] children = f1.listFiles();
			for(int i = 0; i < children.length; ++i){
				if(ok){
					try {
						ok &= copyfile(children[i].getCanonicalPath(),f2.getCanonicalPath()+File.separator+children[i].getName());
					} catch (IOException e) {
						logger.error(e.getMessage());
						ok = false;
					}
				}
			}

		}else{
			if(f2.isDirectory()){
				logger.debug(srFile + " is not a directory and "+dtFile+"is");
				logger.error("fail to copy");
				ok = false;
			}else{
				try{
					InputStream in = new FileInputStream(f1);

					//For Append the file.
					//  OutputStream out = new FileOutputStream(f2,true);

					//For Overwrite the file.
					OutputStream out = new FileOutputStream(f2);

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0){
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
					logger.debug("File copied");
				}catch(FileNotFoundException ex){
					logger.debug(ex.getMessage() + " in the specified directory.");
					ok = false;
				}
				catch(IOException e){
					logger.debug(e.getMessage());
					ok = false;
				}
			}
		}

		return ok;
	}

	public static String relativize(File reference,String ptoRel){
		
		logger.info("relativize ");
		
		String pRef = reference.getAbsolutePath()+"/";
		//String ptoRel = toRel.getAbsolutePath();

		logger.info("relativize pRef " + pRef);
		logger.info("relativize ptoRel " + ptoRel);
		
		List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0; i < pRef.length(); i++) {
			if (pRef.charAt(i) == '/') {
				pos.add(i);
			}
		}
		
		logger.info("relativize 1 ");
		
		int i = pos.size()-1;
		boolean end = false;
		while(i >=0 && !end){
			String common = pRef.substring(0,pos.get(i));
			
			logger.info("relativize common " + common);
			
			if(ptoRel.startsWith(common+"/")){
				end = true;
			}else{
				--i;
			}
		}
		
		String dot = "";
		for (int j = 0; j < pos.size()-1-i; j++) {
			dot = dot.concat("../");
		}
		
		String ans = dot+ptoRel.substring(pos.get(i)+1);

		logger.info("relativize " + ans);
		
		return ans;
	}

}
