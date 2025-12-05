CREATE DATABASE asm_taycam;
GO

USE asm_taycam;
GO

CREATE TABLE TayCam (
    maTayCam INT IDENTITY(1,1) PRIMARY KEY,
    hinhAnh NVARCHAR(255) DEFAULT '/images/Vader4Pro.jpg',
    tenTayCam NVARCHAR(255),
    hangSanXuat NVARCHAR(100),
    gia DECIMAL(18, 2), 
    soLuongTon INT,
    releaseDate DATETIME,
    preorder_discount FLOAT DEFAULT 0.0
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);
GO

CREATE TABLE DatTruocItem (
    id INT IDENTITY(1,1) PRIMARY KEY,
    orderId INT NOT NULL,
    tayCamId INT NOT NULL,
    donGia DECIMAL(18,2) NOT NULL,
    soLuong INT NOT NULL,
    CONSTRAINT FK_DTI_Order FOREIGN KEY (orderId) REFERENCES DatTruoc(id),
    CONSTRAINT FK_DTI_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam)
);
GO

CREATE TABLE YeuThich (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    tayCamId INT NOT NULL,
    thoiGianYeuThich DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_YT_User FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT FK_YT_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE CASCADE,
    CONSTRAINT UQ_YeuThich_User_TayCam UNIQUE (userId, tayCamId) 
);
GO

INSERT INTO TayCam (hinhAnh, tenTayCam, hangSanXuat, gia, soLuongTon, releaseDate, preorder_discount)
VALUES
('/images/Vader4Pro.jpg', N'Tay cầm 1 (Flydigi)', N'Flydigi', 1200000, 50, DATEADD(DAY, -5, GETDATE()), 0.0),
('/images/Vader4Pro.jpg', N'Tay cầm 2 (Sony)', N'Sony', 1500000, 40, DATEADD(DAY, 5, GETDATE()), 10.0), 
('/images/Vader4Pro.jpg', N'Tay cầm 3 (Microsoft)', N'Microsoft', 1300000, 60, DATEADD(DAY, 2, GETDATE()), 10.0);

INSERT INTO users (username, password, role, hoTen, email)
VALUES 
('admin', '{noop}123', 'ADMIN', N'Quản trị viên', 'admin@shop.com'),
('user', '{noop}123', 'USER', N'Người dùng thường', 'user@shop.com');

INSERT INTO DanhGia (tenNguoiDanhGia, soSaoDanhGia, noiDung, tayCamId)
VALUES
(N'Nguyễn Văn A', 5, N'Rất tốt, đáng tiền.', 1),
(N'Lê Thị B', 4, N'Ổn, nhưng hơi đắt.', 2),
(N'Phạm Văn C', 3, N'Tạm được, giao hàng nhanh.', 3);

INSERT INTO DatTruoc (tenKhachHang, soDienThoai, email, diaChi, giaDatTruoc, tayCamId, ngayDat, trangThai, user_id)
VALUES
(N'Trần Thị D', '0909000001', 'd@gmail.com', N'Hà Nội', 1100000, 1, GETDATE(), N'Đã giao', 2),
(N'Ngô Văn E', '0909000002', 'e@gmail.com', N'Đà Nẵng', 1450000, 2, DATEADD(DAY, -1, GETDATE()), N'Chờ giao', 2);

INSERT INTO DatTruocItem (orderId, tayCamId, donGia, soLuong)
VALUES
(1, 1, 1100000, 1),
(2, 2, 1450000, 1);

SELECT * FROM TayCam;
SELECT * FROM DanhGia;
SELECT * FROM DatTruoc;
SELECT * FROM users;
SELECT * FROM DatTruocItem;
