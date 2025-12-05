DROP DATABASE GamingController;

CREATE DATABASE GamingController;
GO

USE GamingController;
GO

CREATE TABLE TayCam (
    maTayCam INT IDENTITY(1,1) PRIMARY KEY,
    hinhAnh NVARCHAR(255) DEFAULT '/images/Vader4Pro.jpg',
    tenTayCam NVARCHAR(255),
    hangSanXuat NVARCHAR(100),
    gia DECIMAL(18, 2) NOT NULL, 
    soLuongTon INT DEFAULT 0,
    releaseDate DATETIME,
    preorder_discount FLOAT DEFAULT 0.0
);
GO

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(255) NOT NULL,
    password NVARCHAR(100) NOT NULL,
    role NVARCHAR(20) NOT NULL,
    hoTen NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    email NVARCHAR(150),
    diaChi NVARCHAR(255),
    CONSTRAINT UQ_users_username UNIQUE (username)
);
GO

CREATE TABLE DanhGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenNguoiDanhGia NVARCHAR(100),
    soSaoDanhGia INT,
    noiDung NVARCHAR(MAX),
    tayCamId INT NOT NULL,
    FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE CASCADE
);
GO

CREATE TABLE DatTruoc (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenKhachHang NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    email NVARCHAR(150),
    diaChi NVARCHAR(255),
    giaDatTruoc DECIMAL(18, 2),
    tayCamId INT, 
    ngayDat DATETIME2 DEFAULT GETDATE(), 
    trangThai NVARCHAR(50) DEFAULT N'Chờ xác nhận', 
    user_id INT,
    FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE NO ACTION, 
    CONSTRAINT FK_DT_User FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);
GO

CREATE TABLE DatTruocItem (
    id INT IDENTITY(1,1) PRIMARY KEY,
    orderId INT NOT NULL,
    tayCamId INT NOT NULL,
    tenSanPham NVARCHAR(255) NULL, 
    donGia DECIMAL(18,2) NOT NULL,
    soLuong INT NOT NULL,
    CONSTRAINT FK_DTI_Order FOREIGN KEY (orderId) REFERENCES DatTruoc(id),
    CONSTRAINT FK_DTI_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam)
);
GO

INSERT INTO users (username, password, role, hoTen, email, diaChi)
VALUES 
('admin', '{noop}123', 'ADMIN', N'Quản trị viên', 'admin@shop.com', N'Hà Nội'), 
('user_staff1', '{noop}123', 'USER', N'Nhân viên cũ 1', 'user_staff1@shop.com', N'Hà Nội'),
('user_staff2', '{noop}123', 'USER', N'Nhân viên cũ 2', 'user_staff2@shop.com', N'Đà Nẵng'),
('user1', '{noop}123', 'USER', N'Nguyễn Văn A', 'user1@mail.com', N'TP HCM'),
('user2', '{noop}123', 'USER', N'Lê Thị B', 'user2@mail.com', N'Hà Nội'),
('user3', '{noop}123', 'USER', N'Phạm Văn C', 'user3@mail.com', N'Cần Thơ'),
('user4', '{noop}123', 'USER', N'Trần Đình D', 'user4@mail.com', N'Hải Phòng'),
('user5', '{noop}123', 'USER', N'Hoàng Thị E', 'user5@mail.com', N'Nghệ An'),
('user6', '{noop}123', 'USER', N'Đỗ Minh F', 'user6@mail.com', N'Đà Nẵng'),
('user7', '{noop}123', 'USER', N'Vũ Kim G', 'user7@mail.com', N'Đồng Nai'),
('user8', '{noop}123', 'USER', N'Nguyễn Văn H', 'user8@mail.com', N'Hải Dương'),
('user9', '{noop}123', 'USER', N'Lê Văn K', 'user9@mail.com', N'Quảng Ninh'),
('user10', '{noop}123', 'USER', N'Trần Thị L', 'user10@mail.com', N'Bình Dương'),
('user11', '{noop}123', 'USER', N'Phan Đình M', 'user11@mail.com', N'Hà Tĩnh'),
('user12', '{noop}123', 'USER', N'Đỗ Thị N', 'user12@mail.com', N'Thái Bình'),
('user13', '{noop}123', 'USER', N'Hoàng Gia P', 'user13@mail.com', N'Vũng Tàu'),
('user14', '{noop}123', 'USER', N'Vũ Đình Q', 'user14@mail.com', N'Tây Ninh'),
('user15', '{noop}123', 'USER', N'Lý Quang R', 'user15@mail.com', N'Quảng Nam'),
('user16', '{noop}123', 'USER', N'Trương Thị S', 'user16@mail.com', N'Bình Thuận'),
('user17', '{noop}123', 'USER', N'Phạm Văn T', 'user17@mail.com', N'Bắc Ninh'); -- ID 20
GO

INSERT INTO TayCam (hinhAnh, tenTayCam, hangSanXuat, gia, soLuongTon, releaseDate, preorder_discount)
VALUES
('/images/Vader4Pro.jpg', N'Tay cầm DualSense Edge', N'Sony', 4500000, 40, DATEADD(DAY, 5, GETDATE()), 10.0), 
('/images/Vader4Pro.jpg', N'Xbox Elite Series 2 Core', N'Microsoft', 4000000, 60, DATEADD(DAY, -10, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'8BitDo Ultimate C', N'8BitDo', 800000, 75, DATEADD(DAY, 2, GETDATE()), 5.0), 
('/images/Vader4Pro.jpg', N'Razer Wolverine V2 Pro', N'Razer', 5200000, 30, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'Flydigi Vader 3 Pro', N'Flydigi', 1490000, 90, DATEADD(DAY, -20, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'PS5 DualSense Cosmic Red', N'Sony', 1800000, 55, DATEADD(DAY, 10, GETDATE()), 8.0), 
('/images/Vader4Pro.jpg', N'Xbox Series X Controller', N'Microsoft', 1590000, 120, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'8BitDo SN30 Pro', N'8BitDo', 950000, 80, DATEADD(DAY, -1, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'Logitech G F710', N'Logitech', 650000, 150, DATEADD(DAY, 30, GETDATE()), 12.0), 
('/images/Vader4Pro.jpg', N'Asus ROG Raikiri Pro', N'Asus', 3900000, 25, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'Nintendo Switch Pro Controller', N'Nintendo', 1850000, 70, DATEADD(DAY, -50, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'GuliKit KingKong 2 Pro', N'GuliKit', 1350000, 110, DATEADD(DAY, 15, GETDATE()), 10.0), 
('/images/Vader4Pro.jpg', N'PowerA Enhanced Wired', N'PowerA', 700000, 180, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'SteelSeries Stratus+', N'SteelSeries', 1100000, 45, DATEADD(DAY, 7, GETDATE()), 7.0), 
('/images/Vader4Pro.jpg', N'Hori Horipad Mini', N'Hori', 550000, 200, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'Scuf Reflex FPS', N'Scuf', 6800000, 15, DATEADD(DAY, 4, GETDATE()), 15.0), 
('/images/Vader4Pro.jpg', N'EasySMX X10', N'EasySMX', 750000, 100, GETDATE(), 0.0),
('/images/Vader4Pro.jpg', N'GameSir T4 Pro', N'GameSir', 590000, 130, DATEADD(DAY, -3, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'Astro C40 TR', N'Astro', 4800000, 20, DATEADD(DAY, 6, GETDATE()), 10.0), 
('/images/Vader4Pro.jpg', N'Thrustmaster eSwap X Pro', N'Thrustmaster', 3200000, 50, GETDATE(), 0.0);
GO

INSERT INTO DanhGia (tenNguoiDanhGia, soSaoDanhGia, noiDung, tayCamId)
VALUES
(N'Khách A', 5, N'Sản phẩm tuyệt vời.', 1), (N'Khách B', 4, N'Pin hơi yếu.', 2),
(N'Khách C', 3, N'Giao hàng nhanh.', 3), (N'Khách D', 5, N'Hoàn hảo cho PC.', 4),
(N'Khách E', 4, N'Đã sử dụng rất bền.', 5), (N'Khách F', 5, N'Màu sắc đẹp, nút bấm nhạy.', 6),
(N'Khách G', 4, N'Cần phải làm quen với bố cục nút.', 7), (N'Khách H', 5, N'Giá hợp lý, chất lượng tốt.', 8),
(N'Khách I', 3, N'Thiết kế hơi cũ.', 9), (N'Khách J', 5, N'Rất thích các tính năng rung.', 10),
(N'Khách K', 4, N'Sử dụng tốt cho game hành động.', 11), (N'Khách L', 5, N'Giao diện thân thiện.', 12),
(N'Khách M', 4, N'Phù hợp với tay nhỏ.', 13), (N'Khách N', 5, N'Chơi mượt, không có độ trễ.', 14),
(N'Khách O', 3, N'Cần cải thiện chất liệu.', 15), (N'Khách P', 5, N'Hài lòng với các nút macro.', 16),
(N'Khách Q', 4, N'Kết nối nhanh chóng.', 17), (N'Khách R', 5, N'Đáng mua, pin trâu.', 18),
(N'Khách S', 4, N'Thiết kế đẹp, cầm chắc tay.', 19), (N'Khách T', 5, N'Chuyên nghiệp, nhiều tuỳ chỉnh.', 20);
GO

INSERT INTO DatTruoc (tenKhachHang, soDienThoai, email, diaChi, giaDatTruoc, tayCamId, ngayDat, trangThai, user_id)
VALUES
(N'Admin Order 1', '0901xxxx01', 'admin@shop.com', N'Hà Nội', 4050000, 1, DATEADD(DAY, -15, GETDATE()), N'Đã giao', 1), 
(N'User Staff 1', '0901xxxx02', 'user_staff1@shop.com', N'Đà Nẵng', 4000000, 2, DATEADD(DAY, -10, GETDATE()), N'Đã giao', 2),
(N'User Staff 2', '0901xxxx03', 'user_staff2@shop.com', N'TP HCM', 760000, 3, DATEADD(DAY, -5, GETDATE()), N'Đang xử lý', 3),
(N'User 1', '0901xxxx04', 'user1@mail.com', N'Hà Nội', 5200000, 4, GETDATE(), N'Chờ xác nhận', 4),
(N'User 2', '0901xxxx05', 'user2@mail.com', N'Cần Thơ', 1490000, 5, GETDATE(), N'Đang xử lý', 5),
(N'User 3', '0901xxxx06', 'user3@mail.com', N'Hải Phòng', 1656000, 6, GETDATE(), N'Chờ xác nhận', 6),
(N'User 4', '0901xxxx07', 'user4@mail.com', N'Nghệ An', 1590000, 7, GETDATE(), N'Đang xử lý', 7),
(N'User 5', '0901xxxx08', 'user5@mail.com', N'Đà Nẵng', 950000, 8, GETDATE(), N'Chờ xác nhận', 8),
(N'User 6', '0901xxxx09', 'user6@mail.com', N'Đồng Nai', 572000, 9, GETDATE(), N'Đã hủy', 9),
(N'User 7', '0901xxxx10', 'user7@mail.com', N'Hải Dương', 3900000, 10, GETDATE(), N'Chờ xác nhận', 10),
(N'User 8', '0901xxxx11', 'user8@mail.com', N'Quảng Ninh', 1850000, 11, GETDATE(), N'Đang xử lý', 11),
(N'User 9', '0901xxxx12', 'user9@mail.com', N'Bình Dương', 1350000, 12, GETDATE(), N'Chờ xác nhận', 12),
(N'User 10', '0901xxxx13', 'user10@mail.com', N'Hà Tĩnh', 700000, 13, GETDATE(), N'Đang xử lý', 13),
(N'User 11', '0901xxxx14', 'user11@mail.com', N'Thái Bình', 1023000, 14, GETDATE(), N'Chờ xác nhận', 14),
(N'User 12', '0901xxxx15', 'user12@mail.com', N'Vũng Tàu', 550000, 15, GETDATE(), N'Đã giao', 15),
(N'User 13', '0901xxxx16', 'user13@mail.com', N'Tây Ninh', 5780000, 16, GETDATE(), N'Đang xử lý', 16),
(N'User 14', '0901xxxx17', 'user14@mail.com', N'Quảng Nam', 750000, 17, GETDATE(), N'Chờ xác nhận', 17),
(N'User 15', '0901xxxx18', 'user15@mail.com', N'Bình Thuận', 590000, 18, GETDATE(), N'Đang xử lý', 18),
(N'User 16', '0901xxxx19', 'user16@mail.com', N'Bắc Ninh', 4320000, 19, GETDATE(), N'Chờ xác nhận', 19),
(N'User 17', '0901xxxx20', 'user17@mail.com', N'Cần Thơ', 3200000, 20, GETDATE(), N'Đã giao', 20);
GO

INSERT INTO DatTruocItem (orderId, tayCamId, tenSanPham, donGia, soLuong)
VALUES
(1, 1, N'Tay cầm DualSense Edge', 4050000, 1), (2, 2, N'Xbox Elite Series 2 Core', 4000000, 1),
(3, 3, N'8BitDo Ultimate C', 800000, 1), (4, 4, N'Razer Wolverine V2 Pro', 5200000, 1),
(5, 5, N'Flydigi Vader 3 Pro', 1490000, 1), (6, 6, N'PS5 DualSense Cosmic Red', 1656000, 1),
(7, 7, N'Xbox Series X Controller', 1590000, 1), (8, 8, N'8BitDo SN30 Pro', 950000, 1),
(9, 9, N'Logitech G F710', 572000, 1), (10, 10, N'Asus ROG Raikiri Pro', 3900000, 1),
(11, 11, N'Nintendo Switch Pro Controller', 1850000, 1), (12, 12, N'GuliKit KingKong 2 Pro', 1350000, 1),
(13, 13, N'PowerA Enhanced Wired', 700000, 1), (14, 14, N'SteelSeries Stratus+', 1023000, 1),
(15, 15, N'Hori Horipad Mini', 550000, 1), (16, 16, N'Scuf Reflex FPS', 5780000, 1),
(17, 17, N'EasySMX X10', 750000, 1), (18, 18, N'GameSir T4 Pro', 590000, 1),
(19, 19, N'Astro C40 TR', 4320000, 1), (20, 20, N'Thrustmaster eSwap X Pro', 3200000, 1);
GO

SELECT * FROM TayCam;
SELECT * FROM DanhGia;
SELECT * FROM DatTruoc;
SELECT * FROM users;
SELECT * FROM DatTruocItem;