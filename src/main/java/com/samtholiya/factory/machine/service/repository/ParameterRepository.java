package com.samtholiya.factory.machine.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ParameterRepository extends CrudRepository<Parameter, Integer> {

    @Query("select min(p.value) as minimum, max(p.value) as maximum, avg(p.value) as average, p.name as name from Parameter p where p.machine = ?1 and createdDate >= ?2 group by p.name")
    List<Map<String, Object>> findParameterSummaryForMachine(Machine machine, LocalDateTime time);

    @Query("select p from Parameter p where p.machine = ?1 and p.name = ?2 and p.createdDate >= ?3 order by p.value asc")
    List<Parameter> findParameterAfterDateTime(Machine machine, String name, LocalDateTime date);

    Integer deleteByIdGreaterThan(Integer id);
}
