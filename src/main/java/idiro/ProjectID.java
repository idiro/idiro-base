package idiro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Keep a track of the version of the software.
 * The name and the version has to be parsed 
 * from the main java method, it does not read into
 * a file.
 * @author etienne
 *
 */
public class ProjectID {

	/**
	 * Name of the program
	 */
	private String name = null;

	/**
	 * Version of the program
	 */
	private String version = null;

	private static ProjectID instance = new ProjectID();

	private ProjectID(){
		InputStream is = getClass().getResourceAsStream( "/META-INF/application.properties" );
		Properties prop = new Properties();
		try {
			prop.load(is);
			name = prop.getProperty("artifactId");
			version = prop.getProperty("version");
		} catch (IOException e) {
			System.out.println("IOException cannot load name and version: "+e.getMessage());
		}
	}


	public static String get(){
		return instance.name+"-"+instance.version;
	}

	public static boolean isInit(){
		return !instance.name.isEmpty() && !instance.version.isEmpty();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Now the project name have to be set from an application.properties
	 * file, in the META-INF directory of the jar
	 * @param name the name to set
	 */
	@Deprecated
	public void setName(String name) {
		if(instance.name == null)
			this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Now the project version have to be set from an application.properties
	 * file, in the META-INF directory of the jar
	 * @param version the version to set
	 */
	@Deprecated
	public void setVersion(String version) {
		if(instance.version == null)
			this.version = version;
	}

	/**
	 * @return the instance
	 */
	public static ProjectID getInstance() {
		return instance;
	}

}
