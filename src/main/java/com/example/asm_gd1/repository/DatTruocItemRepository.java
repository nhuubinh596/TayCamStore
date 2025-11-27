// DatTruocItemRepository.java
package com.example.asm_gd1.repository;

import com.example.asm_gd1.model.DatTruocItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatTruocItemRepository extends JpaRepository<DatTruocItem, Integer> {
    List<DatTruocItem> findByDatTruoc_Id(Integer Id);
}
