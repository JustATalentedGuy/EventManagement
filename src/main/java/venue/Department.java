package venue;

import system.SystemManager;

public class Department {
    private int departmentID;
    private String name;

    public Department(int id, String name) {
        this.departmentID = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setName(String name) {
        SystemManager.updateDepartment(departmentID, name);
    }
}