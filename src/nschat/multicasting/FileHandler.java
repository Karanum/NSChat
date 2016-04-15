package nschat.multicasting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.SequenceNumbers;

/**
 * Helper class for sending and receiving files.
 * @author Dylan
 */
public class FileHandler {
	
	private Connection con;
	
	/**
	 * Creates a new FileHandler.
	 * @param con The Connection object
	 */
	public FileHandler(Connection con) {
		this.con = con;
	}
	
	/**
	 * Saves a received file on the system.
	 * @param filename The name of the file
	 * @param fileBytes The file contents
	 * @return The Path to the saved file
	 */
	public Path writeToFile(String filename, byte[] fileBytes) {
		try {			
			File file = new File("downloads");
			if (!file.exists()) {
				file.mkdir();
			}
			
			Path path = Paths.get(file.getAbsolutePath()).resolve(filename);
			
			Files.createFile(path);
			Files.write(path, fileBytes);
			System.out.println("File downloaded");
			return path;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (FileAlreadyExistsException e) {
			return writeToFile(filename.replace(".", "(1)."), fileBytes);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Converts a file to bytes and sends it.
	 * @param filePath The filepath of the file
	 */
	public void sendFile(String filePath) {
		PacketType type = PacketType.FILE;
		Packet packet = new Packet(type, (byte) 0, SequenceNumbers.get(type), (short) 0, null);
		int last1 = filePath.lastIndexOf('/');
		int last2 = filePath.lastIndexOf('\\');
		byte[] fileName;
		if (last1 < last2) {
			fileName = filePath.substring(last2 + 1).getBytes();
		} else {
			fileName = filePath.substring(last1 + 1).getBytes();
		}
		
		byte[] file;
		try {
			file = Files.readAllBytes(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		int nameLength = fileName.length;
		byte[] data = new byte[1 + nameLength + file.length];
		data[0] = (byte) nameLength;
		System.arraycopy(fileName, 0, data, 1, nameLength);
		System.arraycopy(file, 0, data, nameLength + 1, file.length);
		packet.setData(data);
		con.getSendingBuffer().add(type, packet.getSeqNumber(), packet.pack());
		con.getProgram().getUI().printFile(Paths.get(filePath), new String(fileName));
	}
	
	/**
	 * Should be called when a packet containing a file is received.
	 * @param packet The received packet
	 */
	public void receiveFile(Packet packet) {
		byte[] receivedData = packet.getData();
		int nameLength = receivedData[0];

		byte[] name = Arrays.copyOfRange(receivedData, 1, nameLength + 1);
		String filename = new String(name);
		byte[] data = Arrays.copyOfRange(receivedData, nameLength + 1, receivedData.length);
		try {
			if (!packet.getSenderAddress().equals(InetAddress.getLocalHost())) {
				con.getProgram().getUI().printFile(writeToFile(filename, data), filename);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
