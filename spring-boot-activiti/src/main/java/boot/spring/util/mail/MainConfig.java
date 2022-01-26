package boot.spring.util.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 17:48
 * @Version 1.0
 */

@Configuration
public class MainConfig {
    @Bean
    public GroupTemplateFactoryBean carFactoryBean() {
        return new GroupTemplateFactoryBean();
    }
}

