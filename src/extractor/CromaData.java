package extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cromaEnum.CromaWebUrlEnum;
import service.ConnectToCroma;

public class CromaData {
	
    private static final Logger logger = Logger.getLogger(CromaData.class.getName());

    private final String filePath = "/home/robin/eclipse-workspace/TataCroma/src/csv/category_urls.csv";

    private final CromaDataExtractor cromaDataExtractor;
    private final ConnectToCroma urlConnection;

    public CromaData() {
    	
        this.cromaDataExtractor = new CromaDataExtractor();
        this.urlConnection = new ConnectToCroma();
    }

    public void getData() {
    	
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) 
        {

            String line;
            while ( (line = reader.readLine()) != null ) 
            {
                try {
                	
                    String[] parts = line.split("/");
                    int catCode = Integer.parseInt(parts[parts.length - 1]);   // last part = category code
                    String fileName = parts[parts.length - 3];                 // category name before "/c/"

                    String writeDataFilePath = "/home/robin/eclipse-workspace/TataCroma/src/csv/%s.csv";
                    String outputFile = String.format(writeDataFilePath, fileName);

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) 
                    {
                        // Header row
                        writer.write("product_name,product_manufacturer,product_mrp,type,quickViewDesc\n");

                        int totalPages = 1;
                        int page = 0;

                        while (page < totalPages) {
                        	
                            String webUrl = String.format(CromaWebUrlEnum.CROMA_CATEGORY_URL.getValue(), catCode, page);

                            logger.info("Fetching data from: " + webUrl);

                            String jsonResponse = urlConnection.getJsonResponseOfCategoryUrl(webUrl);

                            // passing the writer directly and writing into that file
                            cromaDataExtractor.getCromaData(jsonResponse, writer);

                            if (page == 0) {
                            	
                                totalPages = cromaDataExtractor.getPageCount(jsonResponse);
                                logger.info("Total pages found for category " + catCode + ": " + totalPages);
                                
                            }

                            page++;
                            logger.info("Current Page: " + page);
                            
                            Thread.sleep(2000); // wait for 2 seconds between requests
                        }
                    }

                    logger.info("Data written successfully for file: " + outputFile);

                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error processing line: " + line, ex);
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading category URLs file: " + filePath, e);
        }
    }
}