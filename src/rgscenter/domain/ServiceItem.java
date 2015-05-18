package rgscenter.domain;

public class ServiceItem {
	String name;
	int serviceamount;
	String description;
	String publisher;
	String parameter;
	int paramAmount;
	
	public String toString(){
		return name+serviceamount+description+publisher+parameter+paramAmount;
	}
	
	public String getName(){
		return name;
	}
}
