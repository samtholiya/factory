package com.samtholiya.factory.machine;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samtholiya.factory.machine.service.repository.Machine;
import com.samtholiya.factory.machine.service.repository.Parameter;
import com.samtholiya.factory.machine.service.repository.MachineRepository;
import com.samtholiya.factory.machine.service.repository.ParameterRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MachineRepository machineRepository;

    @MockBean
    private ParameterRepository parameterRepository;

    @Test
    @DisplayName(value = "Machine should be saved")
    public void positiveSaveMachine() throws Exception {
        when(machineRepository.save(new Machine("h1", "HH"))).thenReturn(new Machine());
        this.mockMvc.perform(post("/api/v1/machine").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"HH\",\"key\":\"h1\"}")).andDo(print()).andExpect(status().isCreated());
    }

    @Test
    @DisplayName(value = "Machine should not be saved")
    public void negativeSaveMachine() throws Exception {
        this.mockMvc.perform(post("/api/v1/machine").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"\",\"key\":\"h1\"}")).andDo(print()).andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/api/v1/machine").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"dd\",\"key\":\"\"}")).andDo(print()).andExpect(status().isBadRequest());

        when(machineRepository.save(new Machine("h1", "HH"))).thenThrow(new RuntimeException());
        this.mockMvc.perform(post("/api/v1/machine").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"name\":\"HH\",\"key\":\"h1\"}")).andDo(print()).andExpect(status().isConflict());
    }

    @Test
    @DisplayName(value = "Parameter should be saved")
    public void positiveSaveParameter() throws Exception {
        Machine machine = new Machine("h1", "JJ");
        when(machineRepository.findByMachineKey("h1")).thenReturn(Optional.of(machine));
        when(parameterRepository.save(new Parameter(machine, "core_diameter", 3f))).thenReturn(new Parameter());
        when(parameterRepository.save(new Parameter(machine, "speed", 20f))).thenReturn(new Parameter());
        this.mockMvc
                .perform(post("/api/v1/machine/parameter").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"machineKey\":\"h1\",\"parameters\":{\"core_diameter\":3,\"speed\":20}}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName(value = "Parameter should not be saved")
    public void negativeSaveParameter() throws Exception {
        this.mockMvc
                .perform(post("/api/v1/machine/parameter").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"machineKey\":\"\",\"parameters\":{\"core_diameter\":3,\"speed\":20}}"))
                .andExpect(status().isBadRequest());
        this.mockMvc
                .perform(post("/api/v1/machine/parameter").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"machineKey\":\"sd\",\"parameters\":{\"\":3,\"speed\":20}}"))
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(post("/api/v1/machine/parameter").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"machineKey\":\"sd\",\"parameters\":{}}")).andExpect(status().isBadRequest());

        when(machineRepository.findByMachineKey("h1")).thenReturn(Optional.empty());
        this.mockMvc
                .perform(post("/api/v1/machine/parameter").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"machineKey\":\"h1\",\"parameters\":{\"core_diameter\":3,\"speed\":20}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Machines with latest parameters should be returned")
    public void positiveMachineWithLatestParameters() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        TypeReference<?> typeRef = new TypeReference<RestPageImpl<Machine>>() {
        };
        Page<Machine> page = (Page<Machine>) objectMapper.readValue(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"latestParameters\":[{\"id\":4,\"name\":\"TS_setpoint_tail_length\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:28.920218\"},{\"id\":5,\"name\":\"perforation_length\",\"value\":16.5,\"createdDate\":\"2021-10-25T02:05:29.041948\"},{\"id\":6,\"name\":\"core_interference\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.135288\"},{\"id\":7,\"name\":\"number_of_sheets\",\"value\":17.7,\"createdDate\":\"2021-10-25T02:05:29.214913\"}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"latestParameters\":[{\"id\":8,\"name\":\"log_diameter\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.29172\"},{\"id\":9,\"name\":\"speed\",\"value\":35.6,\"createdDate\":\"2021-10-25T02:05:29.350256\"}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"latestParameters\":[{\"id\":10,\"name\":\"core_interference\",\"value\":25.7,\"createdDate\":\"2021-10-25T02:05:29.416821\"},{\"id\":11,\"name\":\"speed\",\"value\":27.7,\"createdDate\":\"2021-10-25T02:05:29.503451\"}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}",
                typeRef);
        when(machineRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        this.mockMvc.perform(get("/api/v1/machine")).andDo(print()).andExpect(status().isOk()).andExpect(content().json(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"latestParameters\":[{\"id\":4,\"name\":\"TS_setpoint_tail_length\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:28.920218\"},{\"id\":5,\"name\":\"perforation_length\",\"value\":16.5,\"createdDate\":\"2021-10-25T02:05:29.041948\"},{\"id\":6,\"name\":\"core_interference\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.135288\"},{\"id\":7,\"name\":\"number_of_sheets\",\"value\":17.7,\"createdDate\":\"2021-10-25T02:05:29.214913\"}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"latestParameters\":[{\"id\":8,\"name\":\"log_diameter\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.29172\"},{\"id\":9,\"name\":\"speed\",\"value\":35.6,\"createdDate\":\"2021-10-25T02:05:29.350256\"}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"latestParameters\":[{\"id\":10,\"name\":\"core_interference\",\"value\":25.7,\"createdDate\":\"2021-10-25T02:05:29.416821\"},{\"id\":11,\"name\":\"speed\",\"value\":27.7,\"createdDate\":\"2021-10-25T02:05:29.503451\"}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}"));
    }

    @Test
    @DisplayName(value = "Bad request parameters for machine should fail")
    public void negativeMachineWithLatestParameters() throws Exception {
        this.mockMvc.perform(get("/api/v1/machine").param("page", "-1").param("size", "1")).andDo(print())
                .andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/api/v1/machine").param("page", "1").param("size", "-1")).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Machines should be returned with parameter summary with correct calculations")
    public void positiveMachineSummary() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        TypeReference<?> typeRef = new TypeReference<RestPageImpl<Machine>>() {
        };
        Page<Machine> machinePage = (Page<Machine>) objectMapper.readValue(
                "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"latestParameters\":[{\"id\":4,\"name\":\"TS_setpoint_tail_length\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:28.920218\"},{\"id\":5,\"name\":\"perforation_length\",\"value\":16.5,\"createdDate\":\"2021-10-25T02:05:29.041948\"},{\"id\":6,\"name\":\"core_interference\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.135288\"},{\"id\":7,\"name\":\"number_of_sheets\",\"value\":17.7,\"createdDate\":\"2021-10-25T02:05:29.214913\"}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"latestParameters\":[{\"id\":8,\"name\":\"log_diameter\",\"value\":15.0,\"createdDate\":\"2021-10-25T02:05:29.29172\"},{\"id\":9,\"name\":\"speed\",\"value\":35.6,\"createdDate\":\"2021-10-25T02:05:29.350256\"}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"latestParameters\":[{\"id\":10,\"name\":\"core_interference\",\"value\":25.7,\"createdDate\":\"2021-10-25T02:05:29.416821\"},{\"id\":11,\"name\":\"speed\",\"value\":27.7,\"createdDate\":\"2021-10-25T02:05:29.503451\"}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"totalElements\":3,\"totalPages\":1,\"last\":true,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":3,\"first\":true,\"empty\":false}",
                typeRef);
        when(machineRepository.findAll(PageRequest.of(0, 10))).thenReturn(machinePage);
        ArrayList<Machine> machineList = new ArrayList<>(machinePage.get().collect(Collectors.toList()));
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "h1");
        map.put("minimum", 10f);
        map.put("maximum", 10f);
        map.put("average", 10.0);
        list.add(map);
        for (var machine : machineList) {
            ArrayList<Parameter> pList = new ArrayList<>();
            pList.add(new Parameter(machine, "h1", 8.0f));
            pList.add(new Parameter(machine, "h1", 9.0f));
            pList.add(new Parameter(machine, "h1", 10.0f));
            when(parameterRepository.findParameterSummaryForMachine(eq(machine), any(LocalDateTime.class)))
                    .thenReturn(list);
            when(parameterRepository.findParameterAfterDateTime(eq(machine), eq("h1"), any(LocalDateTime.class)))
                    .thenReturn(pList);
        }
        this.mockMvc.perform(get("/api/v1/machine/summary").param("minutes", "10")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().json(
                        "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":1,\"totalElements\":3,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":3,\"empty\":false}"));

        for (var machine : machineList) {
            ArrayList<Parameter> pList = new ArrayList<>();
            pList.add(new Parameter(machine, "h1", 8.0f));
            pList.add(new Parameter(machine, "h1", 10.0f));
            when(parameterRepository.findParameterSummaryForMachine(eq(machine), any(LocalDateTime.class)))
                    .thenReturn(list);
            when(parameterRepository.findParameterAfterDateTime(eq(machine), eq("h1"), any(LocalDateTime.class)))
                    .thenReturn(pList);
        }
        this.mockMvc.perform(get("/api/v1/machine/summary").param("minutes", "10")).andDo(print())
                .andExpect(status().isOk()).andExpect(content().json(
                        "{\"content\":[{\"id\":1,\"machineKey\":\"ajoparametrit\",\"name\":\"Ajoparametrit\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]},{\"id\":2,\"machineKey\":\"aufwickler\",\"name\":\"Aufwickler\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]},{\"id\":3,\"machineKey\":\"wickelkopf\",\"name\":\"Wickelkopf\",\"parameters\":[{\"name\":\"h1\",\"minimum\":10.0,\"maximum\":10.0,\"average\":10.0,\"median\":9.0}]}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":10,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalPages\":1,\"totalElements\":3,\"size\":10,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":3,\"empty\":false}"));
    }

    @Test
    @DisplayName(value = "Bad request parameters for machine summary should fail")
    public void negativeMachineSummary() throws Exception {
        this.mockMvc
                .perform(get("/api/v1/machine/summary").param("minutes", "10").param("page", "-1").param("size", "1"))
                .andDo(print()).andExpect(status().isBadRequest());
        this.mockMvc
                .perform(get("/api/v1/machine/summary").param("minutes", "-10").param("page", "1").param("size", "1"))
                .andDo(print()).andExpect(status().isBadRequest());
        this.mockMvc
                .perform(get("/api/v1/machine/summary").param("minutes", "10").param("page", "1").param("size", "-1"))
                .andDo(print()).andExpect(status().isBadRequest());
    }
}
