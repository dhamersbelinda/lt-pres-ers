package be.uclouvain.lt.pres.ers.server;

import be.uclouvain.lt.pres.ers.LongTermPreservationWithEvidenceRecordsApplication;
import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import be.uclouvain.lt.pres.ers.server.delegates.MinEnum;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LongTermPreservationWithEvidenceRecordsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
public class SystemTest {
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public BuildTreeTask buildTreeTask;

    @Test
    public void testErrorPreservePO() throws Exception{
        // Error test preservePO without PO
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0"
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.maj").value(DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR.getValue()))
                .andExpect(jsonPath("$.result.min").value(MinEnum.PARAMETER_ERROR.getUri().toString()));

        // Error test preservePO without profile
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "po": [
                                    {
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    }
                                  ]
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Could not easily get a custom response with maj min and reqId
//                .andExpect(jsonPath("$.result").exists())
//                .andExpect(jsonPath("$.result.maj").value(DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR.getValue()))
//                .andExpect(jsonPath("$.result.min").value(MinEnum.PARAMETER_ERROR.getUri().toString()));

        // Error test po present but empty list
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                                  "po": []
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.maj").value(DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR.getValue()))
                .andExpect(jsonPath("$.result.min").value(MinEnum.PARAMETER_ERROR.getUri().toString()));

        // Error test po present but more than one PO
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                                  "po": [{
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    },
                                    {
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    }]
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result.maj").value(DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR.getValue()))
                .andExpect(jsonPath("$.result.min").value(MinEnum.PARAMETER_ERROR.getUri().toString()));

        // TODO:
        // wrong/unknwon PO format
        // different ways of having a wrong digestList
    }

    @Test
    public void testPreservePO() throws Exception {
        // Testing poId existence
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                                  "po": [
                                    {
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    }
                                  ]
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").exists());

        // Tesing reqId value
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reqId": "request_42",
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                                  "po": [
                                    {
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    }
                                  ]
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").exists())
                .andExpect(jsonPath("$.reqId").value("request_42"));

        // Tesing reqId value
        mockMvc.perform(post("/pres/PreservePO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reqId": "request_42",
                                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                                  "po": [
                                    {
                                      "binaryData": {
                                        "value": "ICAgICAgewogICAgICAicHJlcy1EaWdlc3RMaXN0VHlwZSI6IHsKICAgICAgICAiZGlnQWxnIjoiMi4xNi44NDAuMS4xMDEuMy40LjIuMSIsCiAgICAgICAgImRpZ1ZhbCI6WyIrcnl0UHhGRUtKWUhDb0JQV20rbXlTblc5Z3Z4ZXJvTUlZOTEzN2xNaSs0PSJdCiAgICAgIH0KICAgIH0K"
                                      },
                                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                                    }
                                  ]
                                }""")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.poId").exists())
                .andExpect(jsonPath("$.reqId").value("request_42"));
    }
}
