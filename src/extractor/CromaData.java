package extractor;

import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("/");
                    int catCode = Integer.parseInt(parts[parts.length - 1]);   // last part = category code
                    String fileName = parts[parts.length - 3];                 // category name before "/c/"

                    String excelFilePath = String.format(
                            "/home/robin/eclipse-workspace/TataCroma/src/csv/productBeanData/%s.xlsx", fileName);

                    // Create workbook and sheet
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Products");

                    // Create header row
                    Row header = sheet.createRow(0);
                    String[] headers = {"Product_Type","Name", "Description", "Image_URL", "Brand", "product_mrp"};
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = header.createCell(i);
                        cell.setCellValue(headers[i]);
                    }

                    int rowNum = 1;
                    int totalPages = 1;
                    int page = 0;

                    while (page < totalPages) {

                        String webUrl = String.format(CromaWebUrlEnum.CROMA_CATEGORY_URL.getValue(), catCode, page);

                        logger.info("Fetching data from: " + webUrl);

                        String jsonResponse = urlConnection.getJsonResponseOfCategoryUrl(webUrl);

                        // Pass workbook and sheet instead of BufferedWriter
                        rowNum = cromaDataExtractor.getCromaData(jsonResponse, sheet, rowNum);

                        if (page == 0) {
                            totalPages = cromaDataExtractor.getPageCount(jsonResponse);
                            logger.info("Total pages found for category " + catCode + ": " + totalPages);
                        }

                        page++;
                        logger.info("Current Page: " + page);

                        Thread.sleep(2000);
                    }

                    // Write workbook to file
                    try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                        workbook.write(outputStream);
                    }
                    workbook.close();

                    logger.info("Data written successfully for file: " + excelFilePath);

                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Error processing line: " + line, ex);
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading category URLs file: " + filePath, e);
        }
    }
}
