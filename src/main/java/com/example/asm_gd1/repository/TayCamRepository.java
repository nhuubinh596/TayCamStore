package com.example.asm_gd1.repository;

import com.example.asm_gd1.model.TayCam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TayCamRepository extends JpaRepository<TayCam, Integer> {

    Page<TayCam> findByTenTayCamContainingIgnoreCase(String name, Pageable pageable);

    // paging version — đảm bảo controller có thể gọi findByGiaBetween(min,max,pageable)
    Page<TayCam> findByGiaBetween(Double minGia, Double maxGia, Pageable pageable);

    // nếu bạn vẫn muốn giữ versi trả List (option)
    List<TayCam> findByGiaBetween(Double minGia, Double maxGia);

    @Query("""
           SELECT tc FROM TayCam tc
           WHERE
             (:name IS NULL OR :name = '' OR
              LOWER(tc.tenTayCam) LIKE LOWER(CONCAT('%', :name, '%')))
           AND
             (:brand IS NULL OR :brand = '' OR
              LOWER(tc.hangSanXuat) LIKE LOWER(CONCAT('%', :brand, '%')))
           AND
             (:minPrice IS NULL OR tc.gia >= :minPrice)
           AND
             (:maxPrice IS NULL OR tc.gia <= :maxPrice)
           """)
    Page<TayCam> searchAdvanced(
            @Param("name") String name,
            @Param("brand") String brand,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    @Query("""
           SELECT tc FROM TayCam tc
           WHERE (SELECT COALESCE(AVG(d.soSaoDanhGia),0)
                  FROM DanhGia d WHERE d.tayCam.maTayCam = tc.maTayCam) >= :minRating
           """)
    Page<TayCam> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating, Pageable pageable);

    @Query("""
           SELECT tc FROM TayCam tc
           WHERE NOT EXISTS (
               SELECT d FROM DanhGia d
               WHERE d.tayCam.maTayCam = tc.maTayCam AND d.soSaoDanhGia < :minRating
           )
           """)
    Page<TayCam> findByAllRatingsAbove(@Param("minRating") Double minRating, Pageable pageable);
}
