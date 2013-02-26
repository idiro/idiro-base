package idiro;

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
	
	private ProjectID(){}

	
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
	 * @param name the name to set
	 */
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
	 * @param version the version to set
	 */
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
