package nschat.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import nschat.Program;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;

public class SymmetricEncryption {
	private byte[] keyByte = new byte[]{0b01101100 , 65 , 51 , 85 ,  127 , 99 ,(byte) 0x8c , 0b00111011 , 22 , 55 , 10 , 88 ,(byte) 0b11110000 ,(byte) 0b11001110 ,(byte) 0b11011000 ,(byte) 0xc2 };
	private static final int KEYSIZE = 16; //In bytes
	private Key key = new SecretKeySpec(keyByte, "AES");
	private byte[] localIV;
	private Map<Integer, byte[]> IVs = new HashMap<Integer , byte[]>();
	private Program program;
	
	//for testing only.
	public SymmetricEncryption() {
		
	}

	public SymmetricEncryption(Program program) {
		this.program = program;
		localIV = createIV();
		setup(true);
	}
	
	// for testing only
	public void setup(String test) {
		localIV = createIV();
	}
	
	
	/**
	 * Used to setup the secure connection.
	 */
	public void setup(boolean newIV) {
		Packet ivAuth = new Packet();
		ivAuth.setPacketType(PacketType.SECURITY);
		ivAuth.setSeqNumber((short) (Math.random()*Short.MAX_VALUE));
		if (newIV) {
			ivAuth.setFlags(Packet.NEW_FLAG);
		}
		try {
			Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] data = c.doFinal(localIV);
			ivAuth.setData(data);
			program.getConnection().getSendingBuffer().add(ivAuth.pack());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
	
	public void IVReceived(Packet packet) {
		if (!packet.isAck()) {
			if (!IVs.containsKey(packet.getSender()) || packet.isNew()) {
				setup(false);
			}
			Cipher c;
			try {
				c = Cipher.getInstance("AES/ECB/NoPadding");
				c.init(Cipher.DECRYPT_MODE, key);
				byte[] temp = c.doFinal(packet.getData());
				IVs.put(packet.getSender(), temp);

			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
			Packet ackPacket = new Packet();
			ackPacket.setFlags(Packet.ACK_FLAG);
			ackPacket.setRecipient(packet.getSenderAddress());
			program.getConnection().getSendingBuffer().add(ackPacket.pack());
		}
	}
	
	
	private byte[] encdec(byte[] plaintext, byte[] IV, short seq) {	
		byte[] tempKey = new byte[KEYSIZE];
		byte[] result = new byte[plaintext.length];
		
		for (int i = 0; i < plaintext.length; i++) {
			if (i % KEYSIZE == 0) {
				try {
					Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
					c.init(Cipher.ENCRYPT_MODE, key);
					tempKey = c.doFinal(getIV(IV, seq));
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					e.printStackTrace();
				}
			}
			result[i] = (byte) (tempKey[i % KEYSIZE] ^ plaintext[i]);
			//System.out.println("send IV: " + IV[15]); 
		}
		return result;
	}
	
	/**
	 * Decrypts a message
	 * @param ciphertext
	 * @return
	 */
	public byte[] decrypt(byte[] ciphertext, int sender, short seq) {
		return encdec(ciphertext, IVs.get(sender), seq);
	}
	
	public byte[] encrypt(byte[] plaintext, short seq) {
		return encdec(plaintext, localIV, seq);
	}
	
	private byte[] createIV() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[KEYSIZE];
		random.nextBytes(bytes);
		return bytes;
	}
	
	private byte[] getIV(byte[] baseIV, short seq) {
		int len = baseIV.length;
		byte[] finalIV = baseIV.clone();
		
		byte firstSeq = (byte) (seq >> 8);
		byte secondSeq = (byte) seq;
		
		int result = finalIV[len - 1] + secondSeq;
		finalIV[len - 1] += (byte) result;
		if ((result >> 8) > 0) {
			finalIV[len - 2] += (byte) (result >> 8);
		}
		
		result = finalIV[len - 2] + firstSeq;
		finalIV[len - 2] += (byte) result;
		if ((result >> 8) > 0) {
			finalIV[len - 3] += (byte) (result >> 8);
		}
		
		return finalIV;
	}
}
