package com.softserve.ita.parser;

import com.softserve.ita.anotations.Column;
import com.softserve.ita.anotations.Table;
import com.softserve.ita.exceptions.ParseException;
import com.softserve.ita.util.DBUtil;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DbMapper<T> {

    private List<T> RsToObjects(Class entity, ResultSet rs) throws ParseException {
        List<T> entityList = new ArrayList<>();

        if (!entity.isAnnotationPresent(Table.class)) {
            throw new ParseException("Class missing Table annotation: " + entity.getSimpleName());
        }
        if (rs == null) {
            return entityList;
        }

        try {

            Class.forName(entity.getName());

            ResultSetMetaData rsMetaData = rs.getMetaData();
            Field[] fields = entity.getDeclaredFields();

            while (rs.next()) {
                @SuppressWarnings("unchecked")
                T pojo = (T) entity.newInstance();

                for (int iter = 1; iter <= rsMetaData.getColumnCount(); iter++) {
                    String columnName = rsMetaData.getColumnName(iter);
                    Object columnValue = rs.getObject(iter);

                    for (Field field : fields) {
                        field.setAccessible(true);

                        if (field.isAnnotationPresent(Column.class)) {

                            Column column = field.getAnnotation(Column.class);

                            if (column.name().equals(columnName)) {
                                field.set(pojo, columnValue);
                                break;
                            }
                        }

                        field.setAccessible(false);
                    }
                }

                entityList.add(pojo);
            }

            return entityList;

        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | SQLException e) {
            throw new ParseException(e.getMessage(), e);
        }

    }

    public T RsToObject(Class entity, ResultSet rs) throws ParseException {
        List<T> objects = RsToObjects(entity, rs);

        if (objects.size() > 1) {
            throw new ParseException("Expected one row but got " + objects.size());
        }

        if (objects.size() == 0) {
            return null;
        }

        return objects.get(0);

    }


    public List<T> selectAll(Class entity, Connection connection) throws ParseException {

        if (connection == null) {
            throw new ParseException("Connection cannot be null");
        }

        String tableName = checkTableAnnotationAndGetName(entity);

        Field[] fields = entity.getDeclaredFields();
        StringBuilder queryColumn = new StringBuilder();

        for (Field field : fields) {

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                queryColumn.append(column.name()).append(" ");
            }
        }

        String query = "select " + queryColumn.toString()
                .trim()
                .replaceAll(" ", ",") + " from " + tableName;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()
        ) {
            return RsToObjects(entity, rs);

        } catch (SQLException e) {
            throw new ParseException("Error during statement execution", e);
        }
    }


    public T selectById(Class entity, Connection connection, Number id) throws ParseException {

        if (connection == null) {
            throw new ParseException("Connection cannot be null");
        }

        String tableName = checkTableAnnotationAndGetName(entity);
        String idColumn = null;
        boolean isIdColumnInitialized = false;

        Field[] fields = entity.getDeclaredFields();
        StringBuilder queryColumn = new StringBuilder();

        for (Field field : fields) {

            if (field.isAnnotationPresent(Column.class)) {

                Column column = field.getAnnotation(Column.class);
                queryColumn.append(column.name()).append(" ");

                if (column.isId()) {
                    if (!isIdColumnInitialized) {
                        idColumn = column.name();
                        isIdColumnInitialized = true;
                    } else throw new ParseException("Cannot have more than one isId = true annotation");
                }
            }
        }

        String query = "select " + queryColumn.toString()
                .trim()
                .replaceAll(" ", ",") + " from "
                + tableName + " where " + idColumn + " = ?";


        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(query);
            stmt.setObject(1, id);
            rs = stmt.executeQuery();

            return RsToObject(entity, rs);

        } catch (SQLException e) {
            throw new ParseException("Error during statement execution", e);
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(stmt);
        }

    }


    private static String checkTableAnnotationAndGetName(Class clazz) throws ParseException {

        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new ParseException("Class missing Table annotation: " + clazz.getSimpleName());
        }

        Table table = (Table) clazz.getAnnotation(Table.class);

        return table.name();

    }
}
