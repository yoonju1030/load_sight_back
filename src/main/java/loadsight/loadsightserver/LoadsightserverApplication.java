package loadsight.loadsightserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "loadsight.loadsightserver.mybatis")
public class LoadsightserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadsightserverApplication.class, args);
	}

}
