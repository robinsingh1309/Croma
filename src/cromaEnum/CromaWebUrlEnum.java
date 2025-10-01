package cromaEnum;

public enum CromaWebUrlEnum {

	CROMA_URL_TO_EXTRACT_CATEGORY_URL("https://api.croma.com/cmstemplate/allchannels/v1/page?pageType=ContentPage&pageLabelOrId=pwaHomePageFooter&fields=FULL");
	
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