package com.wmsi.sgx.service.sandp.capiq.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DateTimeDeserializer {

    protected String lorem;
    protected String ipsum;
    protected int integer;


    public DateTimeDeserializer(Map<String, Object> valueMap){
        for (String key : valueMap.keySet()){
            setField(key, valueMap.get(key));
        }
    }

    private void setField(String fieldName, Object value) {
        Field field;
        try {
            field = getClass().getDeclaredField(fieldName);
            field.set(this, value);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("lorem", "lorem Value");
        valueMap.put("ipsum", "ipsum Value");
        valueMap.put("integer", 100);
        valueMap.put("notThere", "Nope");

        DateTimeDeserializer f = new DateTimeDeserializer(valueMap);
        System.out.println("lorem => '"+f.lorem+"'");
        System.out.println("ipsum => '"+f.ipsum+"'");
        System.out.println("integer => '"+f.integer+"'");
    }
}