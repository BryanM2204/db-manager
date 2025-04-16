package model;

import java.sql.Date;

public class Employee {
    String Address;
    Date Bdate;
    int Dno;
    String Fname;
    String Lname;
    char Minit;
    float Salary;
    char Sex;
    String Ssn;
    String Super_ssn;

    public Employee() {}

    public Employee(String Address, Date Bdate, int Dno, String Fname, String Lname, char Minit, float Salary,
                    char Sex, String Ssn, String Super_ssn) {
        this.Address = Address;
        this.Bdate = Bdate;
        this.Dno = Dno;
        this.Fname = Fname;
        this.Lname = Lname;
        this.Minit = Minit;
        this.Salary = Salary;
        this.Sex = Sex;
        this.Ssn = Ssn;
        this.Super_ssn = Super_ssn;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public Date getBdate() {
        return Bdate;
    }

    public void setBdate(Date Bdate) {
        this.Bdate = Bdate;
    }

    public int getDno() {
        return Dno;
    }

    public void setDno(int Dno) {
        this.Dno = Dno;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String Fname) {
        this.Fname = Fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String Lname) {
        this.Lname = Lname;
    }

    public char getMinit() {
        return Minit;
    }

    public void setMinit(char Minit) {
        this.Minit = Minit;
    }

    public float getSalary() {
        return Salary;
    }

    public void setSalary(float Salary) {
        this.Salary = Salary;
    }

    public char getSex() {
        return Sex;
    }

    public void setSex(char Sex) {
        this.Sex = Sex;
    }

    public String getSsn() {
        return Ssn;
    }

    public void setSsn(String Ssn) {
        this.Ssn = Ssn;
    }

    public String getSuper_ssn() {
        return Super_ssn;
    }

    public void setSuper_ssn(String Super_ssn) {
        this.Super_ssn = Super_ssn;
    }


}