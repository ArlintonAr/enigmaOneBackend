package com.enigmaOne.enigmaOne.persistence.audit;

import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;

public class AuditEmployeeListener {

    @PostPersist //aplica a un metodo y este  no debe retornar nada y debe ser publico
    @PostUpdate
    public void onPostPersist(Employee employee){
        //System.out.println(employee.toString());
    }

    @PreRemove
    public void onPreDelete(Employee employee){
       //System.out.println(employee.toString());
    }
}
