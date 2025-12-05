package com.example.asm_gd1.repository;

import com.example.asm_gd1.model.DanhGia;
import com.example.asm_gd1.model.TayCam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {
    Page<DanhGia> findByTayCam_MaTayCam(Integer maTayCam, Pageable pageable);

    long countByTayCam_MaTayCam(Integer maTayCam);

    @Query("SELECT AVG(d.soSaoDanhGia) FROM DanhGia d WHERE d.tayCam.maTayCam = :id")
    Double findAverageRatingByTayCamId(@Param("id") Integer id);

    @Query("SELECT tc FROM TayCam tc WHERE " +
            "(SELECT COALESCE(AVG(d.soSaoDanhGia),0) FROM DanhGia d WHERE d.tayCam.maTayCam = tc.maTayCam) >= :minRating")
    Page<TayCam> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating, Pageable pageable);

    @Query("SELECT tc FROM TayCam tc WHERE " +
            "NOT EXISTS (SELECT d FROM DanhGia d WHERE d.tayCam.maTayCam = tc.maTayCam AND d.soSaoDanhGia < :minRating)")
    Page<TayCam> findByAllRatingsAbove(@Param("minRating") Double minRating, Pageable pageable);

}