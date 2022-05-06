package com.joao.sampleproject.core;

import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

public class DynamicDto extends LinkedHashMap<String, Object> {

    private static final String DESCRIPTION_FIELD = "descricao";

    private DynamicDto() {
    }

    public Object get(Object... keys) {
        return Arrays.stream(keys).map(this::get).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static DynamicDto of() {
        return new DynamicDto();
    }

    public static DynamicDto of(DatabaseEntity<?> entity) {
        return of(entity, (StringPath) null);
    }

    public static DynamicDto of(DatabaseEntity<?> entity, StringPath descriptionPath) {
        if (entity == null) {
            return null;
        }

        return new DynamicDto()
                .with("id", entity.getId())
                .populateField(entity, descriptionPath);
    }

    public static DynamicDto of(DatabaseEntity<?> entity, StringPath... descriptionPathArray) {
        if (entity == null) {
            return null;
        }
        final DynamicDto dto = new DynamicDto().with("id", entity.getId());
        Arrays.stream(descriptionPathArray).forEach(path -> dto.populateField(entity, path));
        return dto;
    }

    public DynamicDto with(String key, Object value) {
        put(key, value);
        return this;
    }

    private DynamicDto populateField(DatabaseEntity<?> entity, StringPath descriptionPath) {
        try {
            String fieldName = descriptionPath != null ? descriptionPath.getMetadata().getName() : DESCRIPTION_FIELD;
            String value = BeanUtils.getProperty(entity, fieldName);
            if (value != null) {
                put(fieldName, value);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) {
            //
        }

        return this;
    }
}
