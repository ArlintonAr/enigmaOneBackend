package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.persistence.repository.EmployeeRepository;
import com.enigmaOne.enigmaOne.service.dto.EmployeeCreateUpdateDTO;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import com.enigmaOne.enigmaOne.service.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService implements EmployeeServiceInterface {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; //encriptar contraseña

    @Autowired
    private CloudinaryService cloudinaryService;



    public List<EmployeeResponseDTO> getAllEmployees() {
       List<EmployeeResponseDTO> employees = this.employeeRepository.findAll()
               .stream()
               .map(this.employeeMapper::toEmployeeDTOResponse)
               .collect(Collectors.toList());
        return employees;
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
       return this.employeeRepository.findById(id)
               .map(this.employeeMapper::toEmployeeDTOResponse)
               .orElse(null);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeeByFirstName(String name) {
        List<EmployeeResponseDTO> employeesFind = this.employeeRepository.findEmployeeByFirstNameContainingIgnoreCase(name)
                .stream()
                .map(this.employeeMapper::toEmployeeDTOResponse)
                .collect(Collectors.toList());
        return  employeesFind;
    }

    @Override
    public List<EmployeeResponseDTO> findByAnyTerm(String term) {
        List<EmployeeResponseDTO> employeesFind = this.employeeRepository.findByAnyTerm(term)
                .stream()
                .map(this.employeeMapper::toEmployeeDTOResponse)
                .collect(Collectors.toList());
        return employeesFind;
    }


    @Transactional
    public boolean saveEmployee(EmployeeCreateUpdateDTO employee, MultipartFile photoFile) {
        try {
            System.out.println("ESTE ES EL EMPLEADO: "+ employee);
            //optener contraseña para encriptar
            Employee newEmployee = employeeMapper.DTOToEmployeeCreate(employee);

            String encondedPassword = employee.getPassword();

            //subir foto al repositorio cloudinary y obtener el link de la ruta para almacenar en base de datos
            if ( photoFile != null && !photoFile.isEmpty()){
               String urlPhoto = this.cloudinaryService.uploadPhotoToCloudinary(photoFile,"employees");
               System.out.println(urlPhoto);
               newEmployee.setPhoto(urlPhoto);

            }
            newEmployee.setPassword(this.passwordEncoder.encode(encondedPassword));
            this.employeeRepository.save(newEmployee);
            return  true;

        } catch (Exception e) {
             //TODO: Menejo de errores
            System.out.println(e);
             return false;
        }

    }

    @Transactional
    public boolean updateEmployee(EmployeeCreateUpdateDTO employeeDto,MultipartFile file,Long id) {
        try{
            Employee employee = this.employeeRepository.findById(id).orElse(null);

            if (employee!=null) {
                if (employeeDto.getPassword()!= null && !employeeDto.getPassword().isEmpty()){
                    employeeDto.setPassword(this.passwordEncoder.encode(employeeDto.getPassword()));
                }
                if (file != null && !file.isEmpty()){
                   //eliminar la foto que está en el repositorio de cloudinary
                    this.cloudinaryService.deletePhotoOnCloudinary(employee.getPhoto());
                    //subir la nueva foto a cloudinary
                    String updatedPhoto = this.cloudinaryService.uploadPhotoToCloudinary(file,"employees");
                    //finalmente asignar nueva ruta defoto al empleado
                    employeeDto.setPhoto(updatedPhoto);
                }
                this.employeeMapper.DTOtoEmployeeUpdate(employeeDto, employee);
                this.employeeRepository.save(employee);
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            //TODO: Manejar errores (pendiente)
            System.out.println(e);
            return false;
        }
    }

    @Transactional
    public boolean deleteEmployee(Long id) {
        try {
            Employee employee = this.employeeRepository.findById(id).orElse(null);
            if (employee != null  ){

                this.cloudinaryService.deletePhotoOnCloudinary(employee.getPhoto());
                this.employeeRepository.deleteById(id);
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            //TODO: Manejar errores (pendiente)
            System.out.println(e);
            return  false;
        }

    }


    //Existe por id
    public boolean existsEmployeeById(Long id) {
        return this.employeeRepository.existsById(id);
    }
    //Existe por dni
    public boolean existsEmployeeByEmail(String email) {
        return  this.employeeRepository.existsEmployeeByEmail(email);
    }
    //Existe por email
    public boolean existsEmployeeByDni(String dni) {
        return  this.employeeRepository.existsEmployeeByDni(dni);
    }




}
