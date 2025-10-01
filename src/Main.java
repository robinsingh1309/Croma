import java.io.IOException;

import extractor.CromaCatWebUrlExtractor;
import extractor.CromaData;

public class Main {

	public static void main(String[] args) throws IOException {
		
		CromaCatWebUrlExtractor extractor = new CromaCatWebUrlExtractor();
		extractor.getCategoryWebUrl();
		
		
		CromaData cromaData = new CromaData();
		cromaData.getData();
		
		
		System.out.println("Process Finished...");
	
	
	}

}
