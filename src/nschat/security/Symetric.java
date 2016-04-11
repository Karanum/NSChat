package nschat.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyRep;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;

public class Symetric {
	// TODO change IV to private after testing
	private byte[] keyByte = new byte[]{0b01101100 , 65 , 51 , 85 ,  127 , 99 ,(byte) 0x8c , 0b00111011 , 22 , 55 , 10 , 88 ,(byte) 0b11110000 ,(byte) 0b11001110 ,(byte) 0b11011000 ,(byte) 0xc2 };
	private static final int KEYSIZE = 16; //In bytes
	private Key key = new SecretKeySpec(keyByte, "AES/CBC");
	public byte[] IV;

	
	/**
	 * Used to setup the secure connection.
	 */
	public void setup() {
		IV = createIV();
		Packet ivAuth = new Packet();
		ivAuth.setPacketType(PacketType.SECURITY);
		ivAuth.setSeqNumber((short) 10);
		try {
			Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] data = c.doFinal(IV);
			ivAuth.setData(data);
			//TODO send the packet
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}
	
	public void IVReceived(Packet packet) {
		if (!packet.isAck()) {
			byte[] local = new byte[4];
			try {
				local = InetAddress.getLocalHost().getAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (packet.getSender() < ((local[0] << 24) + (local[1] << 16) + (local[2] << 8) + local[3])) {
				Cipher c;
				try {
					c = Cipher.getInstance("AES/CBC/NoPadding");
					c.init(Cipher.DECRYPT_MODE, key);
					IV = c.doFinal(packet.getData());
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Packet ackPacket = new Packet();
				ackPacket.setFlags(Packet.ACK_FLAG);
				ackPacket.setRecipient(packet.getSenderAddress());
				//TODO send the ack packet
			}
		}
	}
	
	/**
	 * Used to encrypt data.
	 * @param plaintext
	 * @return
	 */
	public byte[] encrypt(byte[] plaintext) {	
		byte[] tempKey = new byte[KEYSIZE];
		byte[] result = new byte[plaintext.length];
		
		for (int j = 0; j < plaintext.length + KEYSIZE ; j += KEYSIZE) {
			try {
				Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
				c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(IV, "AES/ECB/NoPadding"));
				tempKey = c.doFinal(IV);			
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
			for (int i = 0; i < KEYSIZE; i++) {
				result[i+j] = (byte) (tempKey[i] ^ plaintext[i+j]); //XOR the plaintext with the AES encrypted IV
			}
			increaseIV();
		}
		return result;
	}
	
	/**
	 * Used to decrypt the messages. messages need to be in order for the decryption to work.
	 * @param ciphertext
	 * @return
	 */
	public byte[] decrypt(byte[] ciphertext) {
		return encrypt(ciphertext);
	}
	
	private byte[] createIV() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[KEYSIZE];
		random.nextBytes(bytes);
		return bytes;
	}
	
	//TODO change to private after testing
	public void increaseIV() {
		for (int i = KEYSIZE -1; i >= 0; i--) {
			if ((IV[i] + 1) % Byte.MAX_VALUE == 0) {
				IV[i] = 0;
				increaseIV(i+1);
			}
		}
	}
	private void increaseIV(int start) {
		for (int i = start; i >= 0; i--) {
			if ((IV[i] + 1) % Byte.MAX_VALUE == 0) {
				IV[i] = 0;
				increaseIV(i+1);
			}
		}
	}
}
