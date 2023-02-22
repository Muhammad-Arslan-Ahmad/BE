package com.elastic;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Asus
 */
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
 @Ignore
public class TestUtil {

     public static <T> T convert(String data,Class<T> cls) throws JsonProcessingException {
         return new ObjectMapper().readValue(data, cls);
     }
     public static String asJsonString(final Object obj) {
         try {
             return new ObjectMapper().writeValueAsString(obj);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
}
