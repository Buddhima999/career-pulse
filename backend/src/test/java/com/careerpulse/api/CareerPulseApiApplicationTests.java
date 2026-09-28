package com.careerpulse.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		properties = {
				"app.security.jwt.secret="
						+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
		}
)
class CareerPulseApiApplicationTests {

	@Test
	void contextLoads() {
	}
}