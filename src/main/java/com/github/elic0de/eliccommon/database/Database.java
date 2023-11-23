package com.github.elic0de.eliccommon.database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class Database {

    private final File databaseFile;

    private Connection connection;

    private void setConnection() {
        try {
            // Ensure that the database file exists
            if (databaseFile.createNewFile()) {
                Bukkit.getLogger().log(Level.INFO, "Created the SQLite database file");
            }

            // Specify use of the JDBC SQLite driver
            Class.forName("org.sqlite.JDBC");

            // Set SQLite database properties
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.setSynchronous(SQLiteConfig.SynchronousMode.FULL);

            // Establish the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath(), config.toProperties());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An exception occurred creating the database file", e);
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "An SQL exception occurred initializing the SQLite database", e);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to load the necessary SQLite driver", e);
        }
    }

    public TypeAdapter<String> STRING = new TypeAdapter<String>() {

        @Override
        public String read(ResultSet resultSet, String columnLabel) throws SQLException {
            return resultSet.getString(columnLabel);
        }

        @Override
        public String type() {
            return "char(36)";
        }
    };

    public TypeAdapter<Integer> INT = new TypeAdapter<Integer>() {

        @Override
        public Integer read(ResultSet resultSet, String columnLabel) throws SQLException {
            return resultSet.getInt(columnLabel);
        }

        @Override
        public String type() {
            return "INTEGER";
        }
    };

    public TypeAdapter<Long> LONG = new TypeAdapter<Long>() {

        @Override
        public Long read(ResultSet resultSet, String columnLabel) throws SQLException {
            return resultSet.getLong(columnLabel);
        }

        @Override
        public String type() {
            return "bigInt";
        }
    };

    public TypeAdapter<java.util.UUID> UUID = new TypeAdapter<UUID>() {

        @Override
        public UUID read(ResultSet resultSet, String columnLabel) throws SQLException {
            return java.util.UUID.fromString(resultSet.getString(columnLabel));
        }

        @Override
        public String type() {
            return "varchar";
        }
    };

    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new HashMap<>();

    public Database(JavaPlugin plugin, String fileName) {
        this.databaseFile = new File(plugin.getDataFolder(), fileName);

        typeAdapters.put(String.class, STRING);
        typeAdapters.put(UUID.class, UUID);
        typeAdapters.put(Integer.class, INT);
        typeAdapters.put(Long.class, LONG);
    }

    public <T> boolean isCreated(T object) {
        if (!object.getClass().isAnnotationPresent(AnnoEntity.class)) {
            throw new IllegalArgumentException("Object type must be annotated with @AnnoEntity");
        }
        if (!databaseFile.exists()) {
            return false;
        }
        final String tableName = object.getClass().getAnnotation(AnnoEntity.class).table();

        try (PreparedStatement statement = getConnection().prepareStatement(String.format("SELECT * FROM %s LIMIT 1;", tableName))) {
            statement.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public <T> void createTable(T objectClass) {
        final DatabaseMap<?> databaseMap = new DatabaseMap<>(objectClass);
        try (Statement statement = getConnection().createStatement()) {
            statement.execute(getScheme(CommandType.CREATE, databaseMap));
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create table", e);
        }
    }

    public <T> void insert(T objectClass) {
        final DatabaseMap<?> databaseMap = new DatabaseMap<>(objectClass);
        try (PreparedStatement statement = getConnection().prepareStatement(getScheme(CommandType.INSERT, databaseMap))) {
            int parameterIndex = 1;
            for (final Object object : databaseMap.values()) {
                final TypeAdapter<?> typeAdapter = typeAdapters.get(object.getClass());
                if (typeAdapter == null) throw new IllegalArgumentException();
                statement.setObject(parameterIndex, object);
                parameterIndex++;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to insert", e);
        }
    }

    public <T> T select(String columnLabel, String value, Class<T> objectClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final DatabaseMap<?> databaseMap = new DatabaseMap<>(getDefaults(objectClass));
        final Set<Map.Entry<String, Object>> objectMap = databaseMap.entrySet();
        final LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();

        try (PreparedStatement statement = getConnection().prepareStatement(getScheme(CommandType.SELECT, databaseMap).replaceAll("#where", columnLabel).replaceAll("#value", value))) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                for (final Map.Entry<String, Object> entry : objectMap) {
                    final String key = entry.getKey();
                    final Object object = entry.getValue();
                    final TypeAdapter<?> typeAdapter = typeAdapters.get(object.getClass());
                    if (typeAdapter == null) throw new IllegalArgumentException();
                    linkedHashMap.put(key,  typeAdapter.read(resultSet, key));
                }
             }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to select", e);
        }
        return (T) databaseMap.applyObject(linkedHashMap);
    }

    public enum CommandType {
        INSERT,
        SELECT,
        DELETE,
        UPDATE,
        CREATE
    }

    public String getScheme(CommandType type, DatabaseMap<?> databaseMap) {
        final String tableName = databaseMap.getTableName();
        final List<String> scheme = new ArrayList<>();
        for (Map.Entry<String, Object> entry : databaseMap.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            final TypeAdapter<?> adapter = typeAdapters.get(value.getClass());

            if (adapter == null) {
                throw new IllegalArgumentException("Unsupported types cannot be converted");
            }

            switch (type) {
                case CREATE: scheme.add(String.format("`%s` %s NOT NULL", key, adapter.type()));
                    break;
                case UPDATE: scheme.add(String.format("`%s` = ?", key));
                    break;
                case SELECT:
                case INSERT:
                    scheme.add(key);
                    break;
                case DELETE: {}
                default: throw new IllegalArgumentException("No such type of command type");
            }

        }
        final String column = String.join(", ", scheme);
        switch (type) {
            case CREATE: {
                return String.format("CREATE TABLE IF NOT EXISTS `%s` (%s);", tableName, column);
            }
            case SELECT: {
                return String.format("SELECT %s FROM `%s` WHERE #where = #value;", column, tableName);
            }
            case INSERT: {
                return String.format("INSERT INTO `%s` (%s) VALUES (%s)", tableName, column, generate(scheme.size()));
            }
            case UPDATE: {
                return String.format("UPDATE `%s` SET %s WHERE #where = #value;", tableName, column);
            }
            case DELETE: {
                return String.format("DELETE FROM `%s`", tableName);
            }
            default: throw new IllegalArgumentException("No such type of CommandType");
        }
    }

    public String generate(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                sb.append("?");
            } else {
                sb.append(", ").append("?");
            }
        }
        return sb.toString();
    }

    public abstract static class TypeAdapter<T> {

        public TypeAdapter() {}

        public abstract T read(ResultSet resultSet, String columnLabel) throws SQLException;

        public abstract String type();

    }

    protected static <T> T getDefaults(@NotNull Class<T> objectClass) throws InvocationTargetException,
            InstantiationException, IllegalAccessException, IllegalArgumentException {
        // Validate that the object type constructor with zero arguments
        final Optional<Constructor<?>> constructors = Arrays.stream(objectClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0).findFirst();
        if (!constructors.isPresent()) {
            throw new IllegalArgumentException("Class type must have a zero-argument constructor: " + objectClass.getName());
        }

        // Get the constructor
        final Constructor<?> constructor = constructors.get();
        constructor.setAccessible(true);

        // Instantiate an object of the class type to act as the base
        @SuppressWarnings("unchecked") final T defaults = (T) constructor.newInstance();
        return defaults;
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            setConnection();
        } else if (connection.isClosed()) {
            setConnection();
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to close connection", e);
        }
    }
}
