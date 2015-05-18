package test;

import org.apache.log4j.Logger;

public class Test1 {
	
	private static String name = Test1.class.getName();
	private static Logger log = Logger.getLogger(name);
	public void sub()
	{
		log.info("hello logging world!");
	}
	public static void main(String[] args)
	{
		Test1 t = new Test1();
		t.sub();
	}
}
