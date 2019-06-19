package com.softserve.ita.dao;

import com.softserve.ita.exceptions.DAOException;
import com.softserve.ita.exceptions.ParseException;
import com.softserve.ita.model.Employee;
import com.softserve.ita.parser.DbMapper;

import java.sql.*;
import java.util.List;

public class EmployeeDAO {
    private static final String dbUrl = "jdbc:mysql://localhost:3306/employees_db?UseSSL = false?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static final String user = "root";
    private static final String password = "256415sl";

    public static List<Employee> getAllEmployee() throws DAOException{
        DbMapper<Employee> parser = new DbMapper<>();

        try(Connection conn = DriverManager.getConnection(dbUrl,user,password)
        ){

          return parser.selectAll(Employee.class, conn);

        }catch (SQLException | ParseException e) {
            // logger
            throw new DAOException(e.getMessage(), e);
        }

    }

    public static Employee getEmployeeById(int id) throws DAOException{
        DbMapper<Employee> parser = new DbMapper<>();

        try(Connection conn = DriverManager.getConnection(dbUrl,user,password)
        ){

            return parser.selectById(Employee.class, conn, id);

        }catch (SQLException | ParseException e) {
            // logger
            throw new DAOException(e.getMessage(), e);
        }
    }
}
