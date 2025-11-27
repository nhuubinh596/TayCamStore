package com.example.asm_gd1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "DanhGia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Tên không được để trống")
    private String tenNguoiDanhGia;

    @NotNull(message = "Số sao bắt buộc")
    @Min(value = 1, message = "Sao tối thiểu 1")
    @Max(value = 5, message = "Sao tối đa 5")
    private Integer soSaoDanhGia;

    @Size(max = 255, message = "Nội dung tối đa 255 ký tự")
    private String noiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tayCamId")
    private TayCam tayCam;
}
