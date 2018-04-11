package spring.common;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath*:spring-config.xml"})
@ContextConfiguration(locations={"classpath*:resources/spring-test-config.xml"})
public abstract class SpringJUnitTest {
}
