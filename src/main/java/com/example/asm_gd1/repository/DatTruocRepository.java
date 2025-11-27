package com.example.asm_gd1.repository;

import com.example.asm_gd1.model.DatTruoc;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;


@Repository
public interface DatTruocRepository extends JpaRepository<DatTruoc, Integer>, PagingAndSortingRepository<DatTruoc, Integer> {
    Page<DatTruoc> findAll(Pageable pageable);
    Page<DatTruoc> findByEmailOrderByIdDesc(String email, Pageable pageable);
    Page<DatTruoc> findAllByOrderByIdDesc(org.springframework.data.domain.Pageable pageable);

    Object findByEmailOrderByIdDesc(String email);
}
