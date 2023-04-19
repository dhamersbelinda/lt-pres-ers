package be.uclouvain.lt.pres.ers.mode.deserializer;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DigestListDeserializerTest {

    @Test
    public void testAllValid() throws IOException {
        String testCasesDirectory = "DigestLists/valid";
        File[] files = getFilesInDirectory(testCasesDirectory);

        for (File file : files) {
            try {
                readAndParseFile(file);
            } catch(Exception e){
                fail("Test failed for file: "+file+"   Reason:"+e);
            }
        }
    }

    @Test
    public void testAllWrong() throws IOException {
        String testCasesDirectory = "DigestLists/wrong";
        File[] files = getFilesInDirectory(testCasesDirectory);

        for (File file : files) {
            try {
                readAndParseFile(file);
                fail("Test failed for file: "+file+"  Reason : No exception thrown ...");
            } catch(Exception ignored) {}
        }
    }

    // TODO : for each wrong JSON file check the actual exception thrown

    // Utilities

    private File getFile(String pathInResources) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL path = classLoader.getResource(pathInResources);
        if(path == null)
            fail("Could not find directory : "+pathInResources);
        return new File(path.getPath());
    }
    private File[] getFilesInDirectory(String directoryPathInResources) throws IOException {
        return getFile(directoryPathInResources).listFiles();
    }

    private DigestListDto readAndParseFile(File file) throws IOException, IllegalArgumentException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file,DigestListDto.class);
    }
}
