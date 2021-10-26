package com.samtholiya.factory.machine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.samtholiya.factory.MachineApplication;
import com.samtholiya.factory.machine.service.model.ParameterRequest;
import com.samtholiya.factory.machine.service.model.MachineRequest;
import com.samtholiya.factory.machine.service.model.MachineSummaryResponse;
import com.samtholiya.factory.machine.service.repository.Machine;
import com.samtholiya.factory.machine.service.repository.Parameter;
import com.samtholiya.factory.machine.service.repository.MachineRepository;
import com.samtholiya.factory.machine.service.repository.ParameterRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = MachineApplication.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(Lifecycle.PER_CLASS)
public class ControllerIT {
    @Autowired
    private TestRestTemplate template;

    @Autowired
    ApplicationContext applicationContext;

    @LocalServerPort
    Integer localPort;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private MachineRepository machineRepository;

    private static final Logger LOG = LoggerFactory.logger(DataLoader.class);

    private boolean LoadUsers() {
        InputStream file = null;
        file = this.getResourceFileAsInputStream("./BE_data/machines.csv");
        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file)).withSkipLines(1).build();) {
            // Reading Records One by One in a String array
            LOG.info("Adding data to machine table");
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Machine m = new Machine(nextRecord[0], nextRecord[1]);
                machineRepository.save(m);
            }
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    private boolean LoadParameters() {
        InputStream file = null;
        file = this.getResourceFileAsInputStream("./BE_data/parameters.csv");
        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file)).withSkipLines(1).build();) {
            // Reading Records One by One in a String array
            LOG.info("Adding data to parameters table");
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Float value = Float.parseFloat(nextRecord[1]);
                var machine = machineRepository.findByMachineKey(nextRecord[2]);
                if (!machine.isPresent())
                    continue;
                var parameter = new Parameter(machine.get(), nextRecord[0], value);
                var p = parameterRepository.save(parameter);
                LOG.infof("Saved with id:", p.getID());
            }
            return true;
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    public InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = DataLoader.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    @BeforeAll
    public void before() throws Exception {
        resetTables();
        DBTestUtil.resetAutoIncrementColumns(applicationContext);
        if (LoadUsers()) {
            LoadParameters();
        }
    }

    public void resetTables() {
        parameterRepository.deleteAll();
        machineRepository.deleteAll();
    }

    @Test
    @DisplayName(value = "Get Machine Latest Parameters")
    public void getMachineWithLatestParameters() throws Exception {
        ResponseEntity<String> response = template.getForEntity("/api/v1/machine", String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        TypeReference<?> typeRef = new TypeReference<RestPageImpl<Machine>>() {
        };
        Page<Machine> want = (Page<Machine>) objectMapper.readValue(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"latestParameters\":[{\"id\":4,\"name\":\"TS_setpoint_tail_length\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:28.920218\"},{\"id\":5,\"name\":\"perforation_length\",\"value\":16.5,\"createdDate\":\"2021-10-25T02:05:29.041948\"},{\"id\":6,\"name\":\"core_interference\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.135288\"},{\"id\":7,\"name\":\"number_of_sheets\",\"value\":17.7,\"createdDate\":\"2021-10-25T02:05:29.214913\"}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"latestParameters\":[{\"id\":8,\"name\":\"log_diameter\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.29172\"},{\"id\":9,\"name\":\"speed\",\"value\":35.6,\"createdDate\":\"2021-10-25T02:05:29.350256\"}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"latestParameters\":[{\"id\":10,\"name\":\"core_interference\",\"value\":25.7,\"createdDate\":\"2021-10-25T02:05:29.416821\"},{\"id\":11,\"name\":\"speed\",\"value\":27.7,\"createdDate\":\"2021-10-25T02:05:29.503451\"}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}",
                typeRef);
        Page<Machine> got = (Page<Machine>) objectMapper.readValue(response.getBody(), typeRef);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(got).isEqualTo(want);
    }

    @Test
    @DisplayName(value = "Get Machine Parameters Summary")
    public void getMachineSummary() throws Exception {
        TypeReference<?> typeRef = new TypeReference<RestPageImpl<MachineSummaryResponse>>() {
        };
        ResponseEntity<String> response = template.getForEntity("/api/v1/machine/summary?minutes={minutes}",
                String.class, "0");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        response = template.getForEntity("/api/v1/machine/summary?minutes={minutes}", String.class, "5000");
        Page<MachineSummaryResponse> want = (Page<MachineSummaryResponse>) objectMapper.readValue(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"parameters\":[{\"name\":\"TS_setpoint_tail_length\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"perforation_length\",\"minimum\":16.5,\"maximum\":16.5,\"average\":16.5,\"median\":16.5},{\"name\":\"core_interference\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"number_of_sheets\",\"minimum\":17.7,\"maximum\":17.7,\"average\":17.7,\"median\":17.7}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"parameters\":[{\"name\":\"log_diameter\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"speed\",\"minimum\":35.6,\"maximum\":35.6,\"average\":35.6,\"median\":35.6}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"parameters\":[{\"name\":\"core_interference\",\"minimum\":25.7,\"maximum\":25.7,\"average\":25.7,\"median\":25.7},{\"name\":\"speed\",\"minimum\":27.7,\"maximum\":27.7,\"average\":27.7,\"median\":27.7}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageSize\":10,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}",
                typeRef);
        Page<MachineSummaryResponse> got = (Page<MachineSummaryResponse>) objectMapper.readValue(response.getBody(),
                typeRef);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(got).isEqualTo(want);

        response = template.getForEntity("/api/v1/machine/summary", String.class);
        want = (Page<MachineSummaryResponse>) objectMapper.readValue(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"parameters\":[{\"name\":\"TS_setpoint_tail_length\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"perforation_length\",\"minimum\":16.5,\"maximum\":16.5,\"average\":16.5,\"median\":16.5},{\"name\":\"core_interference\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"number_of_sheets\",\"minimum\":17.7,\"maximum\":17.7,\"average\":17.7,\"median\":17.7}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"parameters\":[{\"name\":\"log_diameter\",\"minimum\":15.0,\"maximum\":15.0,\"average\":15.0,\"median\":15.0},{\"name\":\"speed\",\"minimum\":35.6,\"maximum\":35.6,\"average\":35.6,\"median\":35.6}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"parameters\":[{\"name\":\"core_interference\",\"minimum\":25.7,\"maximum\":25.7,\"average\":25.7,\"median\":25.7},{\"name\":\"speed\",\"minimum\":27.7,\"maximum\":27.7,\"average\":27.7,\"median\":27.7}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageSize\":10,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}",
                typeRef);
        got = (Page<MachineSummaryResponse>) objectMapper.readValue(response.getBody(), typeRef);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(got).isEqualTo(want);
    }

    @Test
    @DisplayName(value = "Save Machine Parameters")
    public void setMachineParameters() throws Exception {
        var parameterRequest = new ParameterRequest();
        parameterRequest.machineKey = "ajoparametrit";
        parameterRequest.parameters = new HashMap<String, Float>();
        parameterRequest.parameters.put("TS_setpoint_tail_length", 2.3f);
        parameterRequest.parameters.put("perforation_length", 2.3f);
        ResponseEntity<String> response = template.postForEntity("/api/v1/machine/parameter", parameterRequest,
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        parameterRepository.deleteByIdGreaterThan(11);
    }

    @Test
    @DisplayName(value = "Save Machine")
    public void saveMachine() throws Exception {
        var machineRequest = new MachineRequest();
        machineRequest.key = "h1";
        machineRequest.name = "HH";
        ResponseEntity<String> responseEntity = template.postForEntity("/api/v1/machine", machineRequest, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        machineRepository.deleteByIdGreaterThan(3);
    }

}
