package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.service.dto.EmployeeCreateUpdateDTO;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeServiceInterface {

     List<EmployeeResponseDTO> getAllEmployees();
     EmployeeResponseDTO getEmployeeById(Long id);
     List <EmployeeResponseDTO> getEmployeeByFirstName(String name);

     boolean saveEmployee(EmployeeCreateUpdateDTO employee, MultipartFile file);

     boolean updateEmployee(EmployeeCreateUpdateDTO employee,MultipartFile file,Long id);

     boolean deleteEmployee(Long id);

     boolean existsEmployeeById(Long id);
     boolean existsEmployeeByEmail(String email);
     boolean existsEmployeeByDni(String dni);


     List<EmployeeResponseDTO> findByAnyTerm(String term);
}
