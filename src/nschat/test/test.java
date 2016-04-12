package nschat.test;

import nschat.security.*;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String InputA = "De eerste test string"; //"testing encryption, It seems that it works, lets make the test working now!!!";
		Symetric enc = new Symetric();
		enc.setup();
		byte[] IV = enc.localIV.clone();
		byte[] ciph = enc.encrypt(InputA.getBytes());
		System.out.println("InputA from bytes: " + new String(InputA.getBytes()));
		byte[] dec = enc.encdec(ciph, IV);
		System.out.println("decrypted: " + new String(dec));
		
		//BigInteger key = new BigInteger
		
		/*
		byte[] test = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		String a = new String(test);
		System.out.println(a);
		*/
		
		/*
		byte[] test = new byte[16];
		byte[] a = test.clone();
		byte[] b = test;
		System.out.printf("Before change: test[15] = %d, a[15] = %d, b[15] = %d\n", test[15], a[15], b[15]);
		test[15] = 10;
		System.out.printf("After change: test[15] = %d, a[15] = %d, b[15] = %d\n", test[15], a[15], b[15]);
		*/
		
		//System.out.println(0b10 ^ 0b10);
		
		/*final int d = 16;
		int a = 31;
		for (a = 0; a<100000; a+=48) {
			System.out.printf("rounded up division for %d: ", a);
			if (a%d==0) {
				System.out.println(a/d);
			} else {
				System.out.println(a/d+1);
			}
		}*/
		
		//System.out.println(System.getProperty("os.name"));
		
		/*
		short a = -1;
		Short c = new Short(a);
		int z = a >> 8;
		byte b = c.byteValue();
		
		System.out.println(b);
		System.out.println((new Integer(z)).byteValue());
		
		byte[] ba = {57, 58, 59, 60, 65, 66, 67, 68};
		System.out.println(new String(ba));
		
		int i = 50;
		String is = "" + i;
		byte[] iba = is.getBytes();
		System.out.println(new String(iba));
		*/
		
		
		/*
		byte test = 57;
		Byte t = new Byte(test);
		
		System.out.println(t);
		
		System.out.println(t.toString());
		
		System.out.println(Byte.decode(t.toString()));
		
		Byte z = Byte.decode(t.toString());

		char c = (char) test;
		System.out.println(c);
		
		String s = "";
		s += c;
		System.out.println(s.getBytes()[0]);
		*/
	}

}
