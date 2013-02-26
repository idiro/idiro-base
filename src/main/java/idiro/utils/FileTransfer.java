package idiro.utils;


public interface FileTransfer {

	boolean copyTo(String from, String to);

	boolean copyFrom(String from, String to);

	boolean rm(String filePath);
		
	boolean rmdir(String filePath);

	void close();
	
}
