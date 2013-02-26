package idiro;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

/**
 * Class to look in a jar file.
 * It uses for doing reflection over Block or
 * subclass of Block objects
 * @author etienne
 *
 */
public class BlockManager {

	/**
	 * The logger.
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * The jarFileName from which the classes are searched
	 */
	private String jarFiles = System.getProperties().getProperty("java.class.path", null);

	/**
	 * 
	 * @return the list of non abstract task classes in the project
	 */
	public List<String> getNonAbstractClassesFromSuperClass(String superClassName){
		List<String> tasks = new ArrayList<String>();
		List<String> classNames = new ArrayList<String>();
		classNames.addAll(getClasseNamesInPackage(jarFiles, getRootPackage()));
		for (String className : classNames) {
			// This is the class to load
			logger.debug("Class in package: "+className);
			try{
				if(!Modifier.isAbstract(Class.forName(className).getModifiers())){
					if (isInstance(Class.forName(className),Class.forName(superClassName))){
						logger.debug("Class extending "+superClassName+": "+className);
						tasks.add(className);
					}
				}
			}catch(ClassNotFoundException e){
				getClassNotLoadLogMessage(className);
			}
		}
		return tasks;
	}


	/**
	 * 
	 * @param jarName
	 *            The jar to check
	 * @param packageName
	 *            The package to look in
	 * @return the list of classes in the specified package and sub packages
	 */
	private List<String> getClasseNamesInPackage(final String jarsName, final String packageName) {
		List<String> classes = new ArrayList<String>();
		String pName = packageName.replaceAll("\\.", "/");

		logger.debug("Jars " + jarsName + " looking for " + packageName);
		String[] jars = jarsName.split(":");
		for(String jarName:jars){
			File jFile = new File(jarName);
			if (!jFile.exists()) {
				logger.warn("No jar file found named '"+jarName+"'");
			}else{
				if(jFile.isFile()){
					try {
						JarInputStream jarFileCur = new JarInputStream(new FileInputStream(jFile));
						JarEntry jarEntry;
						while ((jarEntry = jarFileCur.getNextJarEntry()) != null) {
							if ((jarEntry.getName().startsWith(pName)) && (jarEntry.getName().endsWith(".class"))) {
								String className = jarEntry.getName().replaceAll("/", "\\.");
								//6: number of letter in ".class"
								className = className.substring(0, className.length() - 6);
								classes.add(className);
							}
						}
						jarFileCur.close();
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
		return classes;
	}

	/**
	 * Returns true if a className is an instance of a superclass
	 * @param className the className to check
	 * @return true if a className is an instance of superClass
	 */
	public boolean isInstance(Class<?> className, Class<?> superClassName){
		boolean found = false;
		if(className == null){
			found = false;
		}else{
			Class<?> nextClass = className.getSuperclass();
			if( nextClass !=null){
				if (nextClass.equals(superClassName)){
					found = true;
				}else{
					if(nextClass.getSuperclass() != null)
						found = isInstance(nextClass,superClassName);
				}
			}
			if(!found){
				Class<?>[] interfaces = className.getInterfaces();
				for(int i=0; i < interfaces.length; ++i){
					if(interfaces[i].equals(superClassName)){
						found = true;
					}else{
						found |= isInstance(interfaces[i],superClassName);
					}
				}
			}
		}
		if(found){
			logger.debug(superClassName+", extends or implements "+className);
		}
		return found;
	}


	/**
	 * Get message class not load
	 */
	private void getClassNotLoadLogMessage(String className){
		logger.warn("The class "+className+", has been found in the jar files "+
				jarFiles +", package "+getRootPackage()+", but cannot be loaded");
	}

	/**
	 * Gets the package name, we suppose that the block manager class root package is in common
	 * with the block implementation 
	 */
	public String getRootPackage(){
		return this.getClass().getPackage().toString().split(" ")[1];
	}


	/**
	 * Display the block available to run for the user 
	 * 
	 * @return true if it has been displayed successfully
	 */
	public boolean help(){
		boolean ok = true;
		String rootPackageWithPoint = this.getRootPackage()+".";
		List<String> classeNames = getNonAbstractClassesFromSuperClass(BlockInt.class.getCanonicalName());
		StringBuilder builder = new StringBuilder();
		builder.append("The blocks present in this package are:\n");
		Collections.sort(classeNames);
		String lastPackage = "";

		for (String className : classeNames) {

			Class<?> c = null;
			try{

				// This is the class to load
				c = Class.forName(className);
				String command = className.replaceFirst(rootPackageWithPoint, "").toLowerCase();
				BlockInt b = (BlockInt) c.newInstance();
				String packageName = className.substring(0, className.lastIndexOf("."));
				packageName = packageName.replaceFirst(rootPackageWithPoint, "");
				packageName = packageName.replaceAll("[.]", " ");
				if (!packageName.equals(lastPackage)) {
					builder.append("\n\n" + packageName + "\n");
				}
				builder.append("\n       " + command + getSpaces(40 - command.length()) + b.getDescription());
				lastPackage = packageName;
			}catch(Exception e){
				logger.error("The block "+c.getCanonicalName()+" newInstance return an exception: the default constructor is it implemented ?");
				ok = false;
			}
		}
		builder.append("\n\n");
		logger.error(builder.toString());

		return ok;
	}




	/**
	 * @param jarFile the jarFile to set
	 */
	public void setJarFiles(String jarFiles) {
		this.jarFiles = jarFiles;
	}


	/**
	 * @return the jarFile
	 */
	public String getJarFiles() {
		return jarFiles;
	}

	/**
	 * 
	 * @param count
	 *            The number of spaces
	 * @return That number of spaces.
	 */
	protected String getSpaces(final int count) {
		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < count; i++) {
			spaces.append(" ");
		}
		return spaces.toString();
	}

}
