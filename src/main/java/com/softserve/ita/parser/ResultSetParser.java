package com.softserve.ita.parser;

import com.softserve.ita.anotations.Column;
import com.softserve.ita.anotations.Table;
import com.softserve.ita.exceptions.ParseException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultSetParser<T> {
    public List<T> toObjects(ResultSet rs, Class entity) throws ParseException {
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

                            if (column.name().equals(columnName)){
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

    public T toObject(ResultSet rs, Class entity) throws ParseException{
        List<T> objects = toObjects(rs, entity);

        if(objects.size() > 1){
            throw new ParseException("Expected one row but got " + objects.size());
        }

        if(objects.size() == 0){
            return null;
        }

        return objects.get(0);

    }
}
