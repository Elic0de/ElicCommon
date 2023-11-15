package com.github.elic0de.eliccommon.database;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DatabaseMap<T> extends LinkedHashMap<String, Object> {

    private final Class<T> objectClass;

    private final String tableName;

    private final LinkedHashMap<String, List<String>> attributes = new LinkedHashMap<>();


    protected DatabaseMap(@NotNull T object) throws IllegalArgumentException {
        super();

        // Validate that the @AnnoEntity annotation is present
        if (!object.getClass().isAnnotationPresent(AnnoEntity.class)) {
            throw new IllegalArgumentException("Object type must be annotated with @AnnoEntity");
        }

        if (object.getClass().getAnnotation(AnnoEntity.class).table() == null) {
            throw new IllegalArgumentException("tableName must be defined");
        }

        this.objectClass = (Class<T>) object.getClass();
        this.tableName = object.getClass().getAnnotation(AnnoEntity.class).table();

        readFields(object);
    }

    private void readFields(T object) {
        if (!object.getClass().isAnnotationPresent(AnnoEntity.class)) {
            throw new IllegalArgumentException("Object type must be annotated with @AnnoEntity");
        }

        final Field[] fields = object.getClass().getDeclaredFields();
        for (final Field field : fields) {
            final List<String> list = new ArrayList<>();

            // Ensure the field is accessible
            field.setAccessible(true);

            // Ignore fields that are annotated with @AnnoColumIgnored
            if (field.isAnnotationPresent(AnnoColumIgnored.class)) {
                continue;
            }

            final String key = field.getName().toLowerCase(Locale.ROOT);
            if (field.isAnnotationPresent(AnnoPrimaryKey.class)) {
                list.add("PRIMARY KEY");
            }

            if (field.isAnnotationPresent(AnnoNotNull.class)) {
                list.add("NOT NULL");
            }
            attributes.put(key, list);

            try {
                final Optional<Object> value = readFieldValue(field, object);
                this.put(key, value.orElse(null));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to read field " + field.getName() + " from object " +
                        object.getClass().getName() + " to map at " + field.getName(), e);
            }
        }
    }

    private <T> Optional<Object> readFieldValue(@NotNull Field field, @NotNull T object) throws IllegalAccessException {
        // Ensure the field is accessible
        field.setAccessible(true);

        // If the object is an enum, return the name of the enum
        if (field.getType().isEnum()) {
            return Optional.ofNullable(field.get(object)).map(Object::toString);
        }

        // Otherwise, return the value of the field
        return Optional.ofNullable(field.get(object));
    }

    public T applyObject(Map<String, Object> objectMap) {
        try {
            final T objectT = Database.getDefaults(objectClass);
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                final String key = entry.getKey();
                final Object object = entry.getValue();
                final Field field = objectT.getClass().getDeclaredField(key);

                field.setAccessible(true);
                field.set(objectT, object);
            }

            return objectT;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public String getAttribute(String key) {
        return String.join(" ", attributes.getOrDefault(key, List.of()));
    }
}
