package com.softserve.ita.dao;

import com.softserve.ita.exceptions.DAOException;
import com.softserve.ita.exceptions.ParseException;
import com.softserve.ita.model.Employee;
import com.softserve.ita.parser.ResultSetParser;
import com.softserve.ita.util.DBUtil;

import java.sql.*;
import java.util.List;

public class EmployeeDAO {
    private static final String dbUrl = "jdbc:mysql://localhost:3306/employees_db?UseSSL = false?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static final String user = "root";
    private static final String password = "256415sl";

    public static Employee selectAll() throws DAOException{
        ResultSetParser<Employee> parser = new ResultSetParser<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query ="select id, name, salary, is_working from employees where id = ?";
        try(Connection conn = DriverManager.getConnection(dbUrl,user,password)
        ){
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, 1);
            rs = stmt.executeQuery();
            return parser.toObject(rs,Employee.class);

        }catch (SQLException | ParseException e) {
            // logger
            throw new DAOException(e.getMessage(), e);
        }
         finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(stmt);
        }
    }
}
