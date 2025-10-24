package com.enigmaOne.enigmaOne.persistence.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@MappedSuperclass //una super clase que puede ser heredada de otras clases
@Data
public class Auditable {


    //auditoria
    @CreatedDate
    private Date created_at;
    @LastModifiedDate
    private Date updated_at;


}
