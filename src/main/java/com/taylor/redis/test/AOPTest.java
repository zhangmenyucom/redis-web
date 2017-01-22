package com.taylor.redis.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/spring-application.xml","classpath:config/service-consumer.xml"})
public class AOPTest {   
    /** 
     * 测试正常调用 
     */  
    @Test
    public void testCall()  
    {  
        System.out.println("SpringTest JUnit test");  
    }  
}  