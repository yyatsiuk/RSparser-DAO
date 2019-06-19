package com.softserve.ita.Main;



import com.softserve.ita.dao.EmployeeDAO;
import com.softserve.ita.exceptions.DAOException;


public class JdbcMain {

    public static void main(String[] args) {

        try {
            System.out.println(EmployeeDAO.getEmployeeById(2));
        } catch (DAOException e1) {
            e1.printStackTrace();
        }
    }
}
