package idiro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


/**
 * Transfer file between local/remote
 * 
 * The transfer has to be made without password
 * (Using rsa-keys for example)
 * NOTE: this version is available to work with passphrase,
 * however it does not works, it seems the issue is jsch.
 * jsch library cannot be updated because it has to be the 
 * same as hadoop.
 * 
 * @author etienne
 *
 */
public class SFTPFileTransfer implements FileTransfer{

	
	private Logger logger = Logger.getLogger(getClass());
	protected Session session;
	protected String 	user,
						hostname;

	public static String getUser(String host){
		int index = host.indexOf('@');
		if(index == -1){
			return System.getProperty("user.name");
		}
		
		return host.substring(0, index);
	}
	
	public static String getHostname(String host){
		int index = host.indexOf('@');
		if(index == -1){
			return host;
		}
		return host.substring(index+1);
	}
	
	
	public SFTPFileTransfer(String user, Password passphrase, String hostname, String pubKeyPath, String privKeyPath) 
			throws FileNotFoundException, IOException, JSchException{
		init(user,passphrase,hostname,pubKeyPath,privKeyPath);
	}
	
	
	public SFTPFileTransfer(String user, String hostname, String pubKeyPath, String privKeyPath) 
			throws FileNotFoundException, IOException, JSchException{
		init(user,null,hostname,pubKeyPath,privKeyPath);
	}
	
	/**
	 * Constructor to use on linux only 
	 * (there is no default path for keys on windows, have to specifies the key path)
	 * 
	 * @param user
	 * @param passphrase
	 * @param hostname
	 * @throws JSchException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SFTPFileTransfer(String user, Password passphrase, String hostname) 
			throws JSchException, FileNotFoundException, IOException{
		init(user,passphrase,hostname);
	}
	
	/**
	 * Constructor to use on linux only 
	 * (there is no default path for keys on windows, have to specifies the key path)
	 * 
	 * @param user
	 * @param hostname
	 * @throws JSchException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SFTPFileTransfer(String user, String hostname) 
			throws JSchException, FileNotFoundException, IOException{
		init(user,null,hostname);
	}
	
	private void init(String user, Password passphraseO, String hostname) 
			throws JSchException, FileNotFoundException, IOException{
		String pubKeyPath = "/home/"+System.getProperty("user.name")+"/.ssh/id_rsa.pub";
		String privKeyPath = "/home/"+System.getProperty("user.name")+"/.ssh/id_rsa";
		init(user, passphraseO, hostname,pubKeyPath,privKeyPath);
	}
	
	private void init(String user, Password passphraseO, String hostname,String pubKeyPath,String privKeyPath) 
			throws FileNotFoundException, IOException, JSchException{
		JSch jsch=new JSch();
		byte [] privateKey = IOUtils.toByteArray(
				new FileInputStream(privKeyPath));
		byte [] publicKey = IOUtils.toByteArray(
				new FileInputStream(pubKeyPath));
		
		byte [] passphrase = "".getBytes();
		
		if(passphraseO != null){
			passphrase = passphraseO.getPassword().getBytes();
		}
		
		jsch.addIdentity(user, privateKey, publicKey, passphrase);
		   
		this.user = user;
		this.hostname = hostname;
		session= jsch.getSession(user, hostname);
		Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
		session.connect();
	}

	public boolean copyTo(String from, String to){
		boolean ok = true;
		ChannelSftp channel;
		try {
			channel = (ChannelSftp)session.openChannel("sftp");

			channel.connect();
			channel.put(from,to);
			//channel.put(new FileInputStream(localFile), filename);
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Cannot initialise connection with '"+user+"@"+hostname+"'");
			logger.error(e.getMessage());
			ok = false;
		} catch (SftpException e) {
			logger.error("Cannot not copy accross file from "+
					from+" to "+to);
			logger.error(e.getMessage());
			ok = false;
		}
		return ok;
	}

	public boolean copyFrom(String from, String to){
		boolean ok = true;
		ChannelSftp channel;
		try {
			channel = (ChannelSftp)session.openChannel("sftp");

			File fTo = new File(to);
			fTo.getParentFile().mkdirs();
			channel.connect();
			channel.get(from, to);
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Cannot initialise connection with '"+user+"@"+hostname+"'");
			logger.error(e.getMessage());
			ok = false;
		} catch (SftpException e) {
			logger.error("Cannot not copy accross file from "+
					from+" to "+to);
			logger.error(e.getMessage());
			ok = false;
		}
		return ok;
	}
	
	public boolean rm(String filePath){
		boolean ok = true;
		ChannelSftp channel;
		try {
			channel = (ChannelSftp)session.openChannel("sftp");

			channel.connect();
			channel.rm(filePath);
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Cannot initialise connection with '"+user+"@"+hostname+"'");
			logger.error(e.getMessage());
			ok = false;
		} catch (SftpException e) {
			logger.error("Cannot not remove the file"+
					filePath);
			logger.error(e.getMessage());
			ok = false;
		}
		return ok;
	}
	
	public boolean rmdir(String filePath){
		boolean ok = true;
		ChannelSftp channel;
		try {
			channel = (ChannelSftp)session.openChannel("sftp");

			channel.connect();
			channel.rmdir(filePath);
			channel.disconnect();
		} catch (JSchException e) {
			logger.error("Cannot initialise connection with '"+user+"@"+hostname+"'");
			logger.error(e.getMessage());
			ok = false;
		} catch (SftpException e) {
			logger.error("Cannot not remove the directory"+
					filePath);
			logger.error(e.getMessage());
			ok = false;
		}
		return ok;
	}
	public void close(){
		session.disconnect();
	}
}
