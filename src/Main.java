import java.io.IOException;

import extractor.CromaCatWebUrlExtractor;

public class Main {

	public static void main(String[] args) throws IOException {
		
		CromaCatWebUrlExtractor extractor = new CromaCatWebUrlExtractor();
		
		extractor.getCategoryWebUrl();
	}

}
