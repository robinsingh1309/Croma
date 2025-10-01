package cromaEnum;

public enum CromaWebUrlEnum {

	CROMA_URL_TO_EXTRACT_CATEGORY_URL("https://api.croma.com/cmstemplate/allchannels/v1/page?pageType=ContentPage&pageLabelOrId=pwaHomePageFooter&fields=FULL"),
	CROMA_CATEGORY_URL("https://api.croma.com/searchservices/v1/category/%d?currentPage=%d&query=:undefined&fields=FULL&channel=WEB");	// for pagination purpose i have used the string format concept over here
	
	private final String url;
	
	private CromaWebUrlEnum(String val) 
	{
		this.url = val;
	}
	
	public String getValue() 
	{
		return url;
	}
	
}