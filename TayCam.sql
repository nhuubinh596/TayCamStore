CREATE DATABASE asm_taycam;
GO
USE asm_taycam;
GO

-- 1. Users
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    hoTen NVARCHAR(100),
    soDienThoai VARCHAR(20),
    email VARCHAR(150),
    diaChi NVARCHAR(255)
);

-- 2. Hãng SX (Mới)
CREATE TABLE HangSanXuat (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenHang NVARCHAR(100) NOT NULL,
    hinhAnh NVARCHAR(255)
);

-- 3. Thể Loại (Mới)
CREATE TABLE TheLoai (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenTheLoai NVARCHAR(100) NOT NULL
);

-- 4. Tay Cầm
CREATE TABLE TayCam (
    maTayCam INT IDENTITY(1,1) PRIMARY KEY,
    tenTayCam NVARCHAR(255) NOT NULL,
    hinhAnh NVARCHAR(255) DEFAULT '/images/Vader4Pro.jpg',
    gia FLOAT DEFAULT 0,
    soLuongTon INT DEFAULT 0,
    releaseDate DATETIME2,
    preorder_discount FLOAT DEFAULT 0,
    moTa NVARCHAR(MAX),
    hangSanXuatId INT,
    theLoaiId INT,
    CONSTRAINT FK_TayCam_Hang FOREIGN KEY (hangSanXuatId) REFERENCES HangSanXuat(id),
    CONSTRAINT FK_TayCam_TheLoai FOREIGN KEY (theLoaiId) REFERENCES TheLoai(id)
);

-- 5. Khuyến Mãi (Mới)
CREATE TABLE KhuyenMai (
    id INT IDENTITY(1,1) PRIMARY KEY,
    maCode VARCHAR(20) UNIQUE NOT NULL,
    phanTramGiam FLOAT NOT NULL,
    ngayHetHan DATETIME2
);

-- 6. PT Thanh Toán (Mới)
CREATE TABLE PhuongThucThanhToan (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenPhuongThuc NVARCHAR(100) NOT NULL
);

-- 7. Đặt Trước
CREATE TABLE DatTruoc (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenKhachHang NVARCHAR(100),
    soDienThoai VARCHAR(20),
    email VARCHAR(150),
    diaChi NVARCHAR(255),
    giaDatTruoc FLOAT,
    ngayDat DATETIME2 DEFAULT GETDATE(),
    trangThai NVARCHAR(50),
    user_id INT,
    pttt_id INT,
    khuyen_mai_id INT,
    CONSTRAINT FK_DatTruoc_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT FK_DatTruoc_PTTT FOREIGN KEY (pttt_id) REFERENCES PhuongThucThanhToan(id),
    CONSTRAINT FK_DatTruoc_KM FOREIGN KEY (khuyen_mai_id) REFERENCES KhuyenMai(id)
);

-- 8. Chi Tiết Đặt Trước
CREATE TABLE DatTruocItem (
    id INT IDENTITY(1,1) PRIMARY KEY,
    orderId INT NOT NULL,
    tayCamId INT,
    tenSanPham NVARCHAR(255),
    donGia FLOAT,
    soLuong INT,
    CONSTRAINT FK_Item_Order FOREIGN KEY (orderId) REFERENCES DatTruoc(id) ON DELETE CASCADE,
    CONSTRAINT FK_Item_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE SET NULL
);

-- 9. Đánh Giá
CREATE TABLE DanhGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenNguoiDanhGia NVARCHAR(100),
    soSaoDanhGia INT,
    noiDung NVARCHAR(MAX),
    ngayDanhGia DATETIME2 DEFAULT GETDATE(),
    tayCamId INT NOT NULL,
    CONSTRAINT FK_DanhGia_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE CASCADE
);

-- 10. Yêu Thích (Mới)
CREATE TABLE YeuThich (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    tay_cam_id INT NOT NULL,
    ngayThem DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_YeuThich_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT FK_YeuThich_TayCam FOREIGN KEY (tay_cam_id) REFERENCES TayCam(maTayCam) ON DELETE CASCADE
);
GO

-- 1. Insert Users (22 users)
INSERT INTO users (username, password, role, hoTen, email, soDienThoai, diaChi) VALUES 
('admin', '{noop}123', 'ADMIN', N'Quản Trị Viên', 'admin@store.com', '0909000000', N'Hà Nội'),
('user', '{noop}123', 'USER', N'Người Dùng Mẫu', 'user@store.com', '0909111111', N'TP HCM'),
('nguyenvana', '{noop}123', 'USER', N'Nguyễn Văn A', 'a@gmail.com', '0912345678', N'Đà Nẵng'),
('tranthib', '{noop}123', 'USER', N'Trần Thị B', 'b@gmail.com', '0987654321', N'Hải Phòng'),
('phamvanc', '{noop}123', 'USER', N'Phạm Văn C', 'c@gmail.com', '0901234567', N'Cần Thơ'),
('lethid', '{noop}123', 'USER', N'Lê Thị D', 'd@gmail.com', '0933333333', N'Hà Nội'),
('hoangvane', '{noop}123', 'USER', N'Hoàng Văn E', 'e@gmail.com', '0944444444', N'Nghệ An'),
('dovanj', '{noop}123', 'USER', N'Đỗ Văn J', 'j@gmail.com', '0955555555', N'Thanh Hóa'),
('buithik', '{noop}123', 'USER', N'Bùi Thị K', 'k@gmail.com', '0966666666', N'Bắc Ninh'),
('dangvanl', '{noop}123', 'USER', N'Đặng Văn L', 'l@gmail.com', '0977777777', N'Hưng Yên'),
('gamer01', '{noop}123', 'USER', N'Gamer Pro 1', 'g1@gmail.com', '0988888801', N'Hà Nội'),
('gamer02', '{noop}123', 'USER', N'Gamer Pro 2', 'g2@gmail.com', '0988888802', N'TP HCM'),
('gamer03', '{noop}123', 'USER', N'Gamer Pro 3', 'g3@gmail.com', '0988888803', N'Đà Lạt'),
('gamer04', '{noop}123', 'USER', N'Gamer Pro 4', 'g4@gmail.com', '0988888804', N'Nha Trang'),
('gamer05', '{noop}123', 'USER', N'Gamer Pro 5', 'g5@gmail.com', '0988888805', N'Vũng Tàu'),
('streamer_x', '{noop}123', 'USER', N'Streamer X', 'sx@gmail.com', '0999999999', N'Hà Nội'),
('mod_game', '{noop}123', 'ADMIN', N'Moderator', 'mod@store.com', '0911112222', N'TP HCM'),
('test_acc1', '{noop}123', 'USER', N'Test Account 1', 't1@gmail.com', '0922223333', N'Huế'),
('test_acc2', '{noop}123', 'USER', N'Test Account 2', 't2@gmail.com', '0933334444', N'Vinh'),
('test_acc3', '{noop}123', 'USER', N'Test Account 3', 't3@gmail.com', '0944445555', N'Nam Định'),
('vip_member', '{noop}123', 'USER', N'Vip Member', 'vip@gmail.com', '0955556666', N'Hà Nội'),
('guest_pro', '{noop}123', 'USER', N'Guest Pro', 'guest@gmail.com', '0966667777', N'TP HCM');

-- 2. Insert Hãng Sản Xuất (10 Hãng)
INSERT INTO HangSanXuat (tenHang, hinhAnh) VALUES 
('Flydigi', '/images/Vader4Pro.jpg'),
('Sony PlayStation', '/images/Vader4Pro.jpg'),
('Microsoft Xbox', '/images/Vader4Pro.jpg'),
('Nintendo', '/images/Vader4Pro.jpg'),
('Razer', '/images/Vader4Pro.jpg'),
('Logitech', '/images/Vader4Pro.jpg'),
('8BitDo', '/images/Vader4Pro.jpg'),
('GameSir', '/images/Vader4Pro.jpg'),
('SteelSeries', '/images/Vader4Pro.jpg'),
('Gulikit', '/images/Vader4Pro.jpg');

-- 3. Insert Thể Loại (10 Thể loại)
INSERT INTO TheLoai (tenTheLoai) VALUES 
(N'Tay cầm PC'), (N'Tay cầm PS5'), (N'Tay cầm PS4'), 
(N'Tay cầm Xbox'), (N'Tay cầm Switch'), (N'Tay cầm Mobile'), 
(N'Tay cầm Racing'), (N'Tay cầm Fighting'), (N'Phụ kiện tay cầm'), (N'Combo Gaming');

-- 4. Insert Phương Thức Thanh Toán (5 PT)
INSERT INTO PhuongThucThanhToan (tenPhuongThuc) VALUES 
(N'Thanh toán khi nhận hàng (COD)'),
(N'Chuyển khoản ngân hàng (QR Code)'),
(N'Ví điện tử Momo'),
(N'Ví điện tử ZaloPay'),
(N'Thẻ tín dụng Visa/Mastercard');

-- 5. Insert Khuyến Mãi (10 Mã)
INSERT INTO KhuyenMai (maCode, phanTramGiam, ngayHetHan) VALUES 
('WELCOME', 10, '2025-12-31'), ('TET2025', 20, '2025-02-15'), ('SUMMER', 15, '2025-06-30'),
('BLACKFRIDAY', 50, '2025-11-28'), ('VIPMEMBER', 5, '2030-12-31'), ('GAMING', 8, '2025-10-10'),
('FLYDIGI10', 10, '2025-12-31'), ('SONYFAN', 12, '2025-12-31'), ('XBOXON', 12, '2025-12-31'),
('NINTENDO', 10, '2025-12-31');

-- 6. Insert Tay Cầm (25 Sản phẩm) - LƯU Ý: hinhAnh đều là Vader4Pro.jpg
INSERT INTO TayCam (tenTayCam, hangSanXuatId, theLoaiId, gia, soLuongTon, releaseDate, preorder_discount, hinhAnh, moTa) VALUES
(N'Flydigi Vader 4 Pro', 1, 1, 1200000, 50, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Tay cầm đỉnh cao cho PC'),
(N'Sony DualSense Edge', 2, 2, 4500000, 10, DATEADD(DAY, 10, GETDATE()), 10, '/images/Vader4Pro.jpg', N'Tay cầm Pro cho PS5'),
(N'Xbox Elite Series 2', 3, 4, 3500000, 25, DATEADD(DAY, -5, GETDATE()), 0, '/images/Vader4Pro.jpg', N'Huyền thoại tay cầm Xbox'),
(N'Nintendo Switch Pro', 4, 5, 1600000, 100, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Tay cầm chính hãng Nintendo'),
(N'Razer Wolverine V2', 5, 1, 2500000, 15, GETDATE(), 5, '/images/Vader4Pro.jpg', N'Led RGB Chroma cực đẹp'),
(N'Logitech F710', 6, 1, 800000, 200, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Bền bỉ, giá rẻ, không dây'),
(N'8BitDo Ultimate Bluetooth', 7, 5, 1100000, 60, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Kèm dock sạc tiện lợi'),
(N'GameSir T4 Kaleid', 8, 1, 950000, 40, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Vỏ trong suốt led RGB'),
(N'SteelSeries Stratus+', 9, 6, 1500000, 30, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Chuyên dụng cho Mobile/Android'),
(N'Gulikit KingKong 2 Pro', 10, 5, 1050000, 55, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Analog từ tính không bị trôi'),
(N'Flydigi Apex 4', 1, 1, 2100000, 5, DATEADD(DAY, 20, GETDATE()), 15, '/images/Vader4Pro.jpg', N'Màn hình LCD tích hợp'),
(N'DualSense Standard White', 2, 2, 1700000, 150, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Tay cầm PS5 bản tiêu chuẩn'),
(N'DualSense Cosmic Red', 2, 2, 1800000, 100, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Màu đỏ cá tính'),
(N'Xbox Series X Controller Black', 3, 4, 1400000, 120, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Tay cầm Xbox Series mới nhất'),
(N'Xbox Series X Controller Robot White', 3, 4, 1400000, 120, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Màu trắng tinh khôi'),
(N'Joy-Con Neon Red/Blue', 4, 5, 1800000, 80, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Bộ đôi tay cầm cho Switch'),
(N'Razer Kishi V2', 5, 6, 2200000, 40, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Biến điện thoại thành máy game'),
(N'Logitech F310', 6, 1, 450000, 300, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Huyền thoại giá rẻ có dây'),
(N'8BitDo Pro 2', 7, 5, 990000, 70, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Thiết kế Retro cổ điển'),
(N'GameSir G7 SE', 8, 4, 1150000, 50, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Có Hall Effect chống trôi'),
(N'Flydigi Direwolf 2', 1, 1, 850000, 90, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Ngon bổ rẻ cho sinh viên'),
(N'Sony DualShock 4', 2, 3, 1200000, 50, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Tay cầm PS4 vẫn còn hot'),
(N'Xbox Elite Series 2 Core', 3, 4, 2800000, 30, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Bản rút gọn của Elite 2'),
(N'8BitDo Arcade Stick', 7, 8, 2500000, 15, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Bàn game thùng đối kháng'),
(N'Hori Racing Wheel Apex', 2, 7, 3200000, 10, GETDATE(), 0, '/images/Vader4Pro.jpg', N'Vô lăng đua xe giá rẻ');

-- 7. Insert Đơn Hàng (25 Đơn)
INSERT INTO DatTruoc (tenKhachHang, soDienThoai, email, diaChi, giaDatTruoc, trangThai, user_id, pttt_id, ngayDat) VALUES
(N'Nguyễn Văn A', '0912345678', 'a@gmail.com', N'Đà Nẵng', 1200000, 'COMPLETED', 3, 1, DATEADD(DAY, -10, GETDATE())),
(N'Trần Thị B', '0987654321', 'b@gmail.com', N'Hải Phòng', 4500000, 'PREORDER', 4, 2, DATEADD(DAY, -9, GETDATE())),
(N'Phạm Văn C', '0901234567', 'c@gmail.com', N'Cần Thơ', 3500000, 'CANCELLED', 5, 3, DATEADD(DAY, -8, GETDATE())),
(N'Lê Thị D', '0933333333', 'd@gmail.com', N'Hà Nội', 1600000, 'COMPLETED', 6, 1, DATEADD(DAY, -7, GETDATE())),
(N'Hoàng Văn E', '0944444444', 'e@gmail.com', N'Nghệ An', 2500000, 'PREORDER', 7, 4, DATEADD(DAY, -6, GETDATE())),
(N'Đỗ Văn J', '0955555555', 'j@gmail.com', N'Thanh Hóa', 800000, 'COMPLETED', 8, 5, DATEADD(DAY, -5, GETDATE())),
(N'Bùi Thị K', '0966666666', 'k@gmail.com', N'Bắc Ninh', 1100000, 'COMPLETED', 9, 1, DATEADD(DAY, -4, GETDATE())),
(N'Đặng Văn L', '0977777777', 'l@gmail.com', N'Hưng Yên', 950000, 'PREORDER', 10, 2, DATEADD(DAY, -3, GETDATE())),
(N'Gamer Pro 1', '0988888801', 'g1@gmail.com', N'Hà Nội', 3000000, 'COMPLETED', 11, 3, DATEADD(DAY, -2, GETDATE())),
(N'Gamer Pro 2', '0988888802', 'g2@gmail.com', N'TP HCM', 4200000, 'COMPLETED', 12, 1, DATEADD(DAY, -1, GETDATE())),
(N'Gamer Pro 3', '0988888803', 'g3@gmail.com', N'Đà Lạt', 1700000, 'COMPLETED', 13, 2, GETDATE()),
(N'Gamer Pro 4', '0988888804', 'g4@gmail.com', N'Nha Trang', 1800000, 'PREORDER', 14, 1, GETDATE()),
(N'Gamer Pro 5', '0988888805', 'g5@gmail.com', N'Vũng Tàu', 1400000, 'CANCELLED', 15, 4, GETDATE()),
(N'Khách Vãng Lai 1', '0900000001', 'kvl1@gmail.com', N'Hà Nội', 1200000, 'COMPLETED', NULL, 1, DATEADD(DAY, -15, GETDATE())),
(N'Khách Vãng Lai 2', '0900000002', 'kvl2@gmail.com', N'HCM', 1700000, 'PREORDER', NULL, 2, DATEADD(DAY, -14, GETDATE())),
(N'Nguyễn Văn A', '0912345678', 'a@gmail.com', N'Đà Nẵng', 850000, 'COMPLETED', 3, 1, DATEADD(DAY, -1, GETDATE())),
(N'Trần Thị B', '0987654321', 'b@gmail.com', N'Hải Phòng', 1200000, 'COMPLETED', 4, 3, DATEADD(DAY, -20, GETDATE())),
(N'Streamer X', '0999999999', 'sx@gmail.com', N'Hà Nội', 10000000, 'COMPLETED', 16, 5, DATEADD(DAY, -2, GETDATE())),
(N'Test Account 1', '0922223333', 't1@gmail.com', N'Huế', 2100000, 'PREORDER', 18, 2, GETDATE()),
(N'Test Account 2', '0933334444', 't2@gmail.com', N'Vinh', 450000, 'COMPLETED', 19, 1, DATEADD(DAY, -5, GETDATE())),
(N'Vip Member', '0955556666', 'vip@gmail.com', N'Hà Nội', 5000000, 'COMPLETED', 21, 5, DATEADD(DAY, -12, GETDATE())),
(N'Guest Pro', '0966667777', 'guest@gmail.com', N'TP HCM', 990000, 'COMPLETED', 22, 1, DATEADD(DAY, -8, GETDATE())),
(N'Phạm Văn C', '0901234567', 'c@gmail.com', N'Cần Thơ', 2500000, 'PREORDER', 5, 2, GETDATE()),
(N'Lê Thị D', '0933333333', 'd@gmail.com', N'Hà Nội', 1150000, 'COMPLETED', 6, 1, DATEADD(DAY, -3, GETDATE())),
(N'Hoàng Văn E', '0944444444', 'e@gmail.com', N'Nghệ An', 2800000, 'CANCELLED', 7, 3, DATEADD(DAY, -1, GETDATE()));

-- 8. Insert DatTruocItem (25 Items)
INSERT INTO DatTruocItem (orderId, tayCamId, tenSanPham, donGia, soLuong) VALUES
(1, 1, N'Flydigi Vader 4 Pro', 1200000, 1),
(2, 2, N'Sony DualSense Edge', 4500000, 1),
(3, 3, N'Xbox Elite Series 2', 3500000, 1),
(4, 4, N'Nintendo Switch Pro', 1600000, 1),
(5, 5, N'Razer Wolverine V2', 2500000, 1),
(6, 6, N'Logitech F710', 800000, 1),
(7, 7, N'8BitDo Ultimate Bluetooth', 1100000, 1),
(8, 8, N'GameSir T4 Kaleid', 950000, 1),
(9, 9, N'SteelSeries Stratus+', 1500000, 2),
(10, 11, N'Flydigi Apex 4', 2100000, 2),
(11, 12, N'DualSense Standard White', 1700000, 1),
(12, 16, N'Joy-Con Neon Red/Blue', 1800000, 1),
(13, 14, N'Xbox Series X Controller Black', 1400000, 1),
(14, 1, N'Flydigi Vader 4 Pro', 1200000, 1),
(15, 12, N'DualSense Standard White', 1700000, 1),
(16, 21, N'Flydigi Direwolf 2', 850000, 1),
(17, 1, N'Flydigi Vader 4 Pro', 1200000, 1),
(18, 2, N'Sony DualSense Edge', 4500000, 2),
(18, 1, N'Flydigi Vader 4 Pro', 1200000, 1),
(19, 11, N'Flydigi Apex 4', 2100000, 1),
(20, 18, N'Logitech F310', 450000, 1),
(21, 2, N'Sony DualSense Edge', 4500000, 1),
(22, 19, N'8BitDo Pro 2', 990000, 1),
(23, 24, N'8BitDo Arcade Stick', 2500000, 1),
(24, 20, N'GameSir G7 SE', 1150000, 1),
(25, 23, N'Xbox Elite Series 2 Core', 2800000, 1);

-- 9. Insert Đánh Giá (25 Review)
INSERT INTO DanhGia (tenNguoiDanhGia, soSaoDanhGia, noiDung, tayCamId, ngayDanhGia) VALUES
(N'Nguyễn Văn A', 5, N'Hàng ngon, ship nhanh', 1, GETDATE()),
(N'Lê Thị B', 4, N'Hơi đắt nhưng xắt ra miếng', 2, DATEADD(DAY, -1, GETDATE())),
(N'Phạm Văn C', 3, N'Tạm ổn, hộp hơi móp', 3, DATEADD(DAY, -2, GETDATE())),
(N'Trần D', 5, N'Quá tuyệt vời, Flydigi mãi đỉnh', 1, DATEADD(DAY, -3, GETDATE())),
(N'User E', 5, N'DualSense rung phê thật', 12, DATEADD(DAY, -4, GETDATE())),
(N'Gamer 1', 4, N'Xbox cầm đầm tay', 14, DATEADD(DAY, -5, GETDATE())),
(N'Gamer 2', 5, N'Switch Pro pin trâu vãi', 4, DATEADD(DAY, -6, GETDATE())),
(N'Gamer 3', 2, N'Logitech F310 nút hơi cứng', 18, DATEADD(DAY, -7, GETDATE())),
(N'Gamer 4', 5, N'Led RGB đẹp nhức nách', 8, DATEADD(DAY, -8, GETDATE())),
(N'Gamer 5', 4, N'Giá hợp lý cho sinh viên', 21, DATEADD(DAY, -9, GETDATE())),
(N'Khách vãng lai', 5, N'Shop tư vấn nhiệt tình', 1, DATEADD(DAY, -10, GETDATE())),
(N'Test User', 3, N'Giao hàng hơi chậm', 6, DATEADD(DAY, -11, GETDATE())),
(N'Fan Sony', 5, N'Sony vô đối', 2, DATEADD(DAY, -12, GETDATE())),
(N'Fan Xbox', 5, N'Xbox tay cầm ergonomic nhất', 3, DATEADD(DAY, -13, GETDATE())),
(N'Fan Nintendo', 4, N'Drift analog nhẹ', 16, DATEADD(DAY, -14, GETDATE())),
(N'Reviewer', 5, N'Đã test, deadzone thấp', 11, DATEADD(DAY, -15, GETDATE())),
(N'Người mua', 4, N'Đóng gói kỹ', 7, DATEADD(DAY, -16, GETDATE())),
(N'Ẩn danh', 1, N'Hàng lỗi, đã đổi trả', 6, DATEADD(DAY, -17, GETDATE())),
(N'Khách quen', 5, N'Mua cái thứ 3 rồi', 1, DATEADD(DAY, -18, GETDATE())),
(N'Streamer', 5, N'Livestream bao mượt', 2, DATEADD(DAY, -19, GETDATE())),
(N'Pro Player', 5, N'Phản hồi nhanh', 11, DATEADD(DAY, -20, GETDATE())),
(N'Casual Gamer', 4, N'Dễ kết nối', 10, DATEADD(DAY, -21, GETDATE())),
(N'Mobile Gamer', 5, N'Kẹp điện thoại chắc chắn', 17, DATEADD(DAY, -22, GETDATE())),
(N'Retro Fan', 5, N'Nhìn hoài cổ đẹp', 19, DATEADD(DAY, -23, GETDATE())),
(N'Racing Boy', 5, N'Vô lăng quay mượt', 25, DATEADD(DAY, -24, GETDATE()));

-- 10. Insert Yêu Thích (25 items)
INSERT INTO YeuThich (user_id, tay_cam_id, ngayThem) VALUES
(3, 1, GETDATE()), (3, 2, GETDATE()),
(4, 3, GETDATE()), (4, 4, GETDATE()),
(5, 5, GETDATE()), (5, 1, GETDATE()),
(6, 11, GETDATE()), (6, 12, GETDATE()),
(7, 2, GETDATE()), (7, 14, GETDATE()),
(8, 1, GETDATE()), (8, 21, GETDATE()),
(9, 25, GETDATE()), (9, 24, GETDATE()),
(10, 8, GETDATE()), (10, 9, GETDATE()),
(11, 1, GETDATE()), (11, 11, GETDATE()),
(12, 2, GETDATE()), (12, 22, GETDATE()),
(13, 16, GETDATE()), (13, 17, GETDATE()),
(14, 18, GETDATE()), (14, 19, GETDATE()),
(15, 20, GETDATE());
GO

DROP DATABASE asm_taycam