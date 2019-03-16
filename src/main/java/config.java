import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

class config {
    private static final String PROPERTIES_FILE = "properties";
    //private static final String PROPERTIES_FILE = "/home/ura/bin/properties";

    static int PORT_SEND;
    static int PORT;



    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile = null;

        try {
            propertiesFile = new FileInputStream(PROPERTIES_FILE);
            properties.load(propertiesFile);
            PORT_SEND       = Integer.parseInt(properties.getProperty("PORT_SEND"));    //  порт сетевого сокета
            PORT            = Integer.parseInt(properties.getProperty("PORT"));         //  порт postgresql



        } catch (FileNotFoundException ex) {
            System.err.println("Не найден файл " + PROPERTIES_FILE);
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        } finally {
            try {
                propertiesFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
