package fr.groupbees.domain_ptransform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        OBJECT_MAPPER = mapper;
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error Json deserialization");
        }
    }

    public static <T> List<T> deserializeFromResourcePath(String resourcePath, TypeReference<List<T>> reference) {
        final InputStream stream = JsonUtil.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        return deserializeToList(stream, reference);
    }

    public static <T, R> Map<T, R> deserializeMapFromResourcePath(String resourcePath, TypeReference<Map<T, R>> reference) {
        final InputStream stream = JsonUtil.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        return deserializeToMap(stream, reference);
    }

    public static <T> List<T> deserializeToList(String json, TypeReference<List<T>> reference) {
        try {
            return OBJECT_MAPPER.readValue(json, reference);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error Json deserialization");
        }
    }

    public static <T, R> Map<T, R> deserializeToMap(String json, TypeReference<Map<T, R>> reference) {
        try {
            return OBJECT_MAPPER.readValue(json, reference);
        } catch (IOException e) {
            throw new IllegalStateException("Error Json deserialization");
        }
    }

    public static <T, R> Map<T, R> deserializeToMap(InputStream stream, TypeReference<Map<T, R>> reference) {
        try {
            return OBJECT_MAPPER.readValue(stream, reference);
        } catch (IOException e) {
            throw new IllegalStateException("Error Json deserialization");
        }
    }

    public static <T> List<T> deserializeToList(InputStream stream, TypeReference<List<T>> reference) {
        try {
            return OBJECT_MAPPER.readValue(stream, reference);
        } catch (IOException e) {
            throw new IllegalStateException("Error Json deserialization");
        }
    }

    public static <T> String serialize(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error Json serialization");
        }
    }
}
