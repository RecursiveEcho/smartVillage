package com.backend.common.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author chenyang
 * @date 2026/4/14
 * @description Jackson 配置类
 */
@Configuration
public class JacksonConfig  {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
   
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");
    
            //序列化规则
            builder.serializers(
                new LocalDateTimeSerializer(dtf),
                new LocalDateSerializer(df),
                new LocalTimeSerializer(tf)
             );
            //反序列化规则 
            builder.deserializers(
                new LocalDateTimeDeserializer(dtf),
                new LocalDateDeserializer(df),
                new LocalTimeDeserializer(tf)
            );
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.timeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        };
    }

}