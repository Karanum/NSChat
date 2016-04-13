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

public class Symetric {
	// TODO change IV to private after testing
	private byte[] keyByte = new byte[]{0b01101100 , 65 , 51 , 85 ,  127 , 99 ,(byte) 0x8c , 0b00111011 , 22 , 55 , 10 , 88 ,(byte) 0b11110000 ,(byte) 0b11001110 ,(byte) 0b11011000 ,(byte) 0xc2 };
	private static final int KEYSIZE = 16; //In bytes
	private Key key = new SecretKeySpec(keyByte, "AES");
	public byte[] localIV;
	private Map<Integer, byte[]> IVs = new HashMap<Integer , byte[]>();
	private Program program;
	
	//for testing only.
	public Symetric() {
		
	}

	public Symetric(Program program) {
		this.program = program;
		localIV = createIV();
	}
	
	// for testing only
	public void setup(String test) {
		localIV = createIV();
	}
	
	
	/**
	 * Used to setup the secure connection.
	 */
	public void setup() {
		Packet ivAuth = new Packet();
		ivAuth.setPacketType(PacketType.SECURITY);
		ivAuth.setSeqNumber((short) (Math.random()*Short.MAX_VALUE));
		try {
			Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] data = c.doFinal(localIV);
			ivAuth.setData(data);
			program.getConnection().getSendingBuffer().add(ivAuth.pack());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		System.out.print("send IV: ");
		for (int i = 0; i< KEYSIZE; i++) {
			System.out.print(localIV[i]);
		}
		System.out.print("\n");
	}
	
	public void IVReceived(Packet packet) {
		if (!packet.isAck()) {
			if (!IVs.containsKey(packet.getSender())) {
				setup();
			}
			Cipher c;
			try {
				c = Cipher.getInstance("AES/ECB/NoPadding");
				c.init(Cipher.DECRYPT_MODE, key);
				byte[] temp = c.doFinal(packet.getData());
				IVs.put(packet.getSender(), temp);
//				System.out.print("received IV: ");
//				for (int i = 0; i< KEYSIZE; i++) {
//					System.out.print(temp[i]);
//				}
//				System.out.print("\n");
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
			Packet ackPacket = new Packet();
			ackPacket.setFlags(Packet.ACK_FLAG);
			ackPacket.setRecipient(packet.getSenderAddress());
			program.getConnection().getSendingBuffer().add(ackPacket.pack());
		}
	}
	
	
	//TODO change to private after testing
	public byte[] encdec(byte[] plaintext, byte[] IV) {	
		byte[] tempKey = new byte[KEYSIZE];
		byte[] result = new byte[plaintext.length];
		
		for (int i = 0; i < plaintext.length; i++) {
			if (i % KEYSIZE == 0) {
				try {
					Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
					c.init(Cipher.ENCRYPT_MODE, key);
					tempKey = c.doFinal(IV);
					increaseIV(IV);
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					e.printStackTrace();
				}
			}
			result[i] = (byte) (tempKey[i % KEYSIZE] ^ plaintext[i]);
			//System.out.println("send IV: " + IV[15]); 
		}
		
		/*for (int j = 0; j < plaintext.length + KEYSIZE ; j += KEYSIZE) {
			try {
				Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
				c.init(Cipher.ENCRYPT_MODE, key);
				tempKey = c.doFinal(IV);		
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				
				e.printStackTrace();
			}
			for (int i = 0; i < KEYSIZE; i++) {
				result[i+j] = (byte) (tempKey[i] ^ plaintext[i+j]); //XOR the plaintext with the AES encrypted IV
			}
			increaseIV();
		}*/
		return result;
	}
	
	/**
	 * Used to decrypt the messages. messages need to be in order for the decryption to work.
	 * @param ciphertext
	 * @return
	 */
	public byte[] decrypt(byte[] ciphertext, int sender) {
		return encdec(ciphertext, IVs.get(sender));
	}
	
	public byte[] encrypt(byte[] plaintext) {
		return encdec(plaintext, localIV);
	}
	
	private byte[] createIV() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[KEYSIZE];
		random.nextBytes(bytes);
		return bytes;
	}
	
	//TODO change to private after testing
	public void increaseIV(byte[] IV) {
		for (int i = KEYSIZE -1; i >= 0; i--) {
			if ((IV[i] + 1) % Byte.MAX_VALUE == 0) {
				IV[i] = 0;
				increaseIV(i-1, IV);
			} else {
				IV[i]++;
				return;
			}
		}
	}
	private void increaseIV(int start , byte[] IV) {
		for (int i = start; i >= 0; i--) {
			if ((IV[i] + 1) % Byte.MAX_VALUE == 0) {
				IV[i] = 0;
				increaseIV(i-1, IV);
			} else {
				IV[i]++;
				return;
			}
		}
	}
}
