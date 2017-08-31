package cn.eovie.socks5.boot;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by earayu on 2017/8/31.
 */
@SpringBootApplication
@EnableConfigurationProperties(LocalServerConfig.class)
public class ToysApplication implements ApplicationContextAware {


    public static void main(String[] args) {
        SpringApplication.run(ToysApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBean(LocalSever.class).start();
    }
}
