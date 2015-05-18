package rgscenter.domain;

import java.util.ResourceBundle;

public class Config {
	private static Config conf = new Config();
	private ResourceBundle rb = null;
	private Config()
	{
		rb = ResourceBundle.getBundle("conf");
	}
	public static Config newInstance()
	{
		return conf;
	}
	public String getAttribute(String name)
	{
		return rb.getString(name);
	}
	public static void main(String[] args)
	{
		System.out.println(Config.newInstance().getAttribute("a"));
	}
}
