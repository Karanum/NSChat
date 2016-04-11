package nschat.test;

import static org.junit.Assert.*;

import nschat.security.Symetric;

import org.junit.Before;
import org.junit.Test;

public class EncryptionTest {
	
	private final String INPUTA = "De eerste test string";
	private final int INPUTB = 541;
	private Symetric enc = new Symetric();

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void IVLengthTest() {
		assertEquals(enc.IV.length, 16);
	}
	
	@Test
	public void IVIncreaseTest() {
		byte[] startIV = enc.IV.clone();
		enc.increaseIV();
		byte[] endIV = enc.IV.clone();
		
		
	}
	
	@Test
	public void EncryptDecryptCorrect() {
		byte[] input = INPUTA.getBytes();
		byte[] encrypted = enc.encrypt(input);
		assertEquals(encrypted, input); //TODO change to not equals
		byte[] result = enc.decrypt(encrypted);
		assertEquals(result, input);
	}

}
