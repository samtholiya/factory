package com.samtholiya.factory.machine.service.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MachineRepository extends PagingAndSortingRepository<Machine, Integer> {

    Optional<Machine> findByMachineKey(String machineKey);

    Integer deleteByIdGreaterThan(Integer id);
}