package com.example.demo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Service {

    private static final String EMPL_ID = "EmpID";
    private static final String PROJECT_ID = "ProjectID";
    private static final String START_DATE = "DateFrom";
    private static final String END_DATE = "DateTo";

    public List<Employee> getListOfEmployees() throws IOException {
        List<Employee> employeeList = new ArrayList<>();

        try {
            InputStream csvDocumentInputStream = this.getClass().getClassLoader().getResourceAsStream("data.csv");
            Reader reader = new BufferedReader(new InputStreamReader(csvDocumentInputStream));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            );

            for (CSVRecord csvRecord : csvParser) {
                Employee employeeDto = new Employee();
                employeeDto.setEmployeeId(parseInt(csvRecord.get(EMPL_ID)));
                employeeDto.setProjectId(parseInt(csvRecord.get(PROJECT_ID)));
                employeeDto.setStartDate((csvRecord.get(START_DATE).equals("NULL")) ? LocalDate.now() : LocalDate.parse(csvRecord.get(START_DATE)));
                employeeDto.setEndDate((csvRecord.get(END_DATE).equals("NULL")) ? LocalDate.now() : LocalDate.parse(csvRecord.get(END_DATE)));

                employeeList.add(employeeDto);
            }
        } catch (Exception e) {
            throw new IOException("Reading of csv {} is invalid!", e);
        }

        return employeeList;
    }

    public void findCommonEmployees() throws IOException {
        List<Employee> employees = getListOfEmployees();

        //group employees by project ID
        //then apply filter by projects that have more than 1 employee
        Map<Integer, List<Employee>> listOfEmployeesByProjectID =  employees
                .stream()
                .collect(Collectors.groupingBy(Employee::getProjectId))
                .entrySet()
                .stream().filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Integer employeeByProjectId : listOfEmployeesByProjectID.keySet()) {

            for(int i = 0; i < listOfEmployeesByProjectID.get(employeeByProjectId).size() - 1; i++) {

                for(int j = 1; j < listOfEmployeesByProjectID.get(employeeByProjectId).size(); j++) {

                    Employee employee = listOfEmployeesByProjectID.get(employeeByProjectId).get(i);
                    Employee employee1 = listOfEmployeesByProjectID.get(employeeByProjectId).get(j);

                    long overlappingDays = overlappingDays(employee.getStartDate(), employee.getEndDate(), employee1.getStartDate(), employee1.getEndDate());

                    if(!Objects.equals(employee.getEmployeeId(), employee1.getEmployeeId()) && overlappingDays > 1) {
                        System.out.println(employee.getEmployeeId() + " " + employee1.getEmployeeId() + " " + overlappingDays);
                    }
                }
            }
        }
    }

    private long overlappingDays(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
            long numberOfOverlappingDates;
            if (end1.isBefore(start2) || end2.isBefore(start1)) {
                numberOfOverlappingDates = 0;
            } else {
                LocalDate laterStart = Collections.max(Arrays.asList(start1, start2));
                LocalDate earlierEnd = Collections.min(Arrays.asList(end1, end2));
                numberOfOverlappingDates = ChronoUnit.DAYS.between(laterStart, earlierEnd);
            }
        return numberOfOverlappingDates;
    }
}
