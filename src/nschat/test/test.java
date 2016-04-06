package nschat.test;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
