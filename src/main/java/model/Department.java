package model;

import java.sql.Date;

public class Department {
    String Dname;
    int Dnumber;
    String Mgr_ssn;
    Date Mgr_start_date;

    public Department(String Dname, int Dnumber, String Mgr_ssn, Date Mgr_start_date) {
        this.Dname = Dname;
        this.Dnumber = Dnumber;
        this.Mgr_ssn = Mgr_ssn;
        this.Mgr_start_date = Mgr_start_date;
    }

    public Department() {
        
    }

    public String getDname() {
        return Dname;
    }

    public void setDname(String Dname) {
        this.Dname = Dname;
    }

    public int getDnumber() {
        return Dnumber;
    }

    public void setDnumber(int Dnumber) {
        this.Dnumber = Dnumber;
    }

    public String getMgr_ssn() {
        return Mgr_ssn;
    }

    public void setMgr_ssn(String Mgr_ssn) {
        this.Mgr_ssn = Mgr_ssn;
    }

    public Date getMgr_start_date() {
        return Mgr_start_date;
    }

    public void setMgr_start_date(Date Mgr_start_date) {
        this.Mgr_start_date = Mgr_start_date;
    }
}