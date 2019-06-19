package com.softserve.ita.model;


import com.softserve.ita.anotations.Column;
import com.softserve.ita.anotations.Table;

@Table(name ="employees")
public class Employee {

    @Column(name ="id", isId = true)
    private int id;

    @Column(name ="name")
    private String name;

    @Column(name ="department")
    private String department;

    @Column(name ="salary")
    private int salary;

    @Column(name = "is_working")
    private boolean isWorking;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", isWorking=" + isWorking +
                "";
    }
}
