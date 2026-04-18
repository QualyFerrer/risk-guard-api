package com.cesar.riskguard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:postgresql://localhost:5432/riskguard",
		"spring.datasource.username=riskguard_user",
		"spring.datasource.password=riskguard_pass"
})
class RiskguardApplicationTests {

	@Test
	void contextLoads() {
	}
}