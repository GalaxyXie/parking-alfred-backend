package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.entity.Employee;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.EmployeeNotExistedException;
import com.alfred.parkingalfred.repository.EmployeeRepository;
import com.alfred.parkingalfred.service.EmployeeService;
import com.alfred.parkingalfred.utils.EncodingUtil;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Employee getEmployeeByNameAndPassword(String name, String password) {
    String encodedPassword = EncodingUtil.encodingByMd5(password);
    Employee employee = employeeRepository.findByNameAndPassword(name, encodedPassword);
    if (employee == null) {
      throw new EmployeeNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    return employee;
  }
}
