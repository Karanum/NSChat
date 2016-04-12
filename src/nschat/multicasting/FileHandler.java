package nschat.multicasting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.SequenceNumbers;

public class FileHandler {
	
	public void writeToFile(String filename, byte[] fileBytes) {
		
		FileOutputStream fileOutputStream;
		try {
			String filePath = new File("downloads/" + filename).getAbsolutePath();
			fileOutputStream = new FileOutputStream(filePath);
			fileOutputStream.write(fileBytes);
		    fileOutputStream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFile(SendingBuffer buffer, String filePath) {
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
		buffer.add(type, packet.getSeqNumber(), packet.pack());
	}
	

	public void receiveFile(Packet packet) {
		byte[] receivedData = packet.getData();
		int nameLength = receivedData[0];

		byte[] name = Arrays.copyOfRange(receivedData, 1, nameLength + 1);
		String filename = new String(name);
		byte[] data = Arrays.copyOfRange(receivedData, nameLength + 1, receivedData.length);
		
		writeToFile(filename, data);
	}

}
