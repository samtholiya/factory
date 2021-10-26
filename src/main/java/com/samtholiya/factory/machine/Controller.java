package com.samtholiya.factory.machine;

import com.samtholiya.factory.machine.service.Service;
import com.samtholiya.factory.machine.service.model.MachineRequest;
import com.samtholiya.factory.machine.service.model.ParameterRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController()
@RequestMapping("/api/v1/machine")
public class Controller {

    @Autowired
    private Service service;

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveMachine(@RequestBody MachineRequest machineRequest) {
        if (machineRequest.key.equals("") || machineRequest.name.equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!service.saveMachine(machineRequest)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMachine(@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (page < 0 || size <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var machines = this.service.getAllMachine(page, size);
        return new ResponseEntity<>(machines, HttpStatus.OK);
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMachineSummary(@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "minutes", defaultValue = "10") Integer minutes) {
        if (page < 0 || size <= 0 || minutes <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var machineSummary = this.service.getAllMachineSummary(page, size, minutes);
        return new ResponseEntity<>(machineSummary, HttpStatus.OK);
    }

    @PostMapping(value = "/parameter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveParameter(@RequestBody ParameterRequest entity) {
        if (entity.machineKey.equals("") || entity.parameters.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (var parameter : entity.parameters.entrySet()) {
            if (parameter.getKey().equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!this.service.saveParameters(entity)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.CREATED);
    }
}
