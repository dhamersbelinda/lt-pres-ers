package be.uclouvain.lt.pres.ers.server;

import be.uclouvain.lt.pres.ers.LongTermPreservationWithEvidenceRecordsApplication;
import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LongTermPreservationWithEvidenceRecordsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
public class ExampleForJETest {
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public BuildTreeTask buildTreeTask;

    @Test
    public void getAllER() throws Exception {
        // Test to generate the following tree and get all evidence records:
        /*
                                                 0  -> Second renewal
                                                 |
                                                 0  -> Premier renewal
                                                 |
                                                 0
                                    /                         \
                        1                                                  2
                  /           \                                      /           \
            3                       4                         5                        6
         /     \                 /     \                   /     \                   DUMMY/RANDOM
      7           8           9           10           11           12
   Sasha       Belinda  Jean-Emmanuel    Jean         Yves         DUMMY/RANDOM
         */
        // Perform five preservePO
        File f = getFile("PreservePORequests/Sasha.json");
        MvcResult resp = mockMvc.perform(post("/pres/PreservePO")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Files.readString(f.toPath()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String poidSasha = JsonPath.read(resp.getResponse().getContentAsString(), "$.poId");

        f = getFile("PreservePORequests/Belinda.json");
        resp = mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Files.readString(f.toPath()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String poidBelinda = JsonPath.read(resp.getResponse().getContentAsString(), "$.poId");

        f = getFile("PreservePORequests/Jean-Emmanuel.json");
        resp = mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Files.readString(f.toPath()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String poidJeanEmmanuel = JsonPath.read(resp.getResponse().getContentAsString(), "$.poId");

        f = getFile("PreservePORequests/Jean.json");
        resp = mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Files.readString(f.toPath()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String poidJean = JsonPath.read(resp.getResponse().getContentAsString(), "$.poId");

        f = getFile("PreservePORequests/Yves.json");
        resp = mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Files.readString(f.toPath()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String poidYves = JsonPath.read(resp.getResponse().getContentAsString(), "$.poId");

        // build the tree
        buildTreeTask.scheduledTask();

        // Get all ERs
        System.out.println("XML Sasha ==========");
        resp = mockMvc.perform(post("/pres/RetrievePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(  "{\"poId\":\""+poidSasha+"\"}"  )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(new String(Base64.getDecoder().decode((String) JsonPath.read(resp.getResponse().getContentAsString(), "$.po[0].xmlData.b64Content"))));

        System.out.println("XML Belinda ==========");
        resp = mockMvc.perform(post("/pres/RetrievePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(  "{\"poId\":\""+poidSasha+"\"}"  )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(new String(Base64.getDecoder().decode((String) JsonPath.read(resp.getResponse().getContentAsString(), "$.po[0].xmlData.b64Content"))));
    }

    private File getFile(String pathInResources) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL path = classLoader.getResource(pathInResources);
        if(path == null)
            fail("Could not find directory : "+pathInResources);
        return new File(path.getPath());
    }
}
