package com.samtholiya.factory;

import com.samtholiya.factory.machine.Controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MachineApplicationTests {

	@Autowired
	private Controller machinController;

	@Test
	void contextLoads() {
		assertThat(machinController).isNotNull();
	}

}
