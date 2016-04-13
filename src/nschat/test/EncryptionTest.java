package nschat.test;

import static org.junit.Assert.*;

import nschat.security.Symetric;

import org.junit.Before;
import org.junit.Test;

public class EncryptionTest {
	
	private final String INPUTA = "De eerste test string";
	//private final int INPUTB = 541;
	private Symetric enc = new Symetric();

	@Before
	public void setUp() throws Exception {
		enc.setup("a");
	}
	
	@Test
	public void IVLengthTest() {
		assertEquals(enc.localIV.length, 16);
	}
	
	@Test
	public void EncryptDecryptCorrect() {
		byte[] input = INPUTA.getBytes();
		byte[] IV = enc.localIV.clone();
		byte[] encrypted = enc.encrypt(input);
		//assertEquals(encrypted, input); //TODO change to not equals
		byte[] result = enc.encdec(encrypted, IV);
//		assertEquals(result.length, input.length);
//		for (int i = 0; i < result.length; ++i) {
//			assertEquals(result[i], input[i]);
//		}
		//System.out.println(new String(result));
		assertTrue((new String(result)).equals(INPUTA));
	}
}
