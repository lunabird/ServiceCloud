package rgscenter.domain;

public class Comp {
	private int id;
	private String name;
	private String type;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Comp clone(){
		Comp ret = new Comp();
		ret.setId(id);
		ret.setName(name);
		ret.setType(type);
		
		return ret;
	}
	
}
