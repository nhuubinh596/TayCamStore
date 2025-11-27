CREATE DATABASE asm_taycam;
GO

USE asm_taycam;
GO

CREATE TABLE TayCam (
    maTayCam INT IDENTITY(1,1) PRIMARY KEY,
    hinhAnh NVARCHAR(255) DEFAULT '/images/Vader4Pro.jpg',
    tenTayCam NVARCHAR(255),
    hangSanXuat NVARCHAR(100),
    gia FLOAT,
    soLuongTon INT,
    releaseDate DATETIME
);

CREATE TABLE DanhGia (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenNguoiDanhGia NVARCHAR(100),
    soSaoDanhGia INT,
    noiDung NVARCHAR(MAX),
    tayCamId INT,
    FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE CASCADE
);

CREATE TABLE DatTruoc (
    id INT IDENTITY(1,1) PRIMARY KEY,
    tenKhachHang NVARCHAR(100),
    soDienThoai NVARCHAR(20),
    email NVARCHAR(150),
    diaChi NVARCHAR(255),
    giaDatTruoc FLOAT,
    tayCamId INT,
    FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam) ON DELETE CASCADE
);

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(100) NOT NULL,
    role NVARCHAR(20) NOT NULL
);

CREATE TABLE DatTruocItem (
  id INT IDENTITY(1,1) PRIMARY KEY,          
  orderId INT NOT NULL,
  tayCamId INT NOT NULL,
  donGia DECIMAL(18,2) NOT NULL,
  soLuong INT NOT NULL,
  CONSTRAINT FK_DTI_Order FOREIGN KEY (orderId) REFERENCES DatTruoc(id),
  CONSTRAINT FK_DTI_TayCam FOREIGN KEY (tayCamId) REFERENCES TayCam(maTayCam)
);

INSERT INTO TayCam (hinhAnh, tenTayCam, hangSanXuat, gia, soLuongTon, releaseDate)
VALUES
('/images/Vader4Pro.jpg', N'Tay cầm 1', N'Flydigi', 1200000, 50, GETDATE()),
('/images/Vader4Pro.jpg', N'Tay cầm 2', N'Sony', 1500000, 40, DATEADD(DAY, 1, GETDATE())),
('/images/Vader4Pro.jpg', N'Tay cầm 3', N'Microsoft', 1300000, 60, DATEADD(DAY, 2, GETDATE()));

INSERT INTO DanhGia (tenNguoiDanhGia, soSaoDanhGia, noiDung, tayCamId)
VALUES
(N'Nguyễn Văn A', 5, N'Rất tốt', 1),
(N'Lê Thị B', 4, N'Ổn, nhưng hơi đắt', 2),
(N'Phạm Văn C', 3, N'Tạm được', 3);

INSERT INTO DatTruoc (tenKhachHang, soDienThoai, email, diaChi, giaDatTruoc, tayCamId)
VALUES
(N'Trần Thị D', '0909000001', 'd@gmail.com', N'Hà Nội', 1100000, 1),
(N'Ngô Văn E', '0909000002', 'e@gmail.com', N'Đà Nẵng', 1450000, 2),
(N'Đinh Văn F', '0909000003', 'f@gmail.com', N'HCM', 1300000, 3);

INSERT INTO users (username, password, role)
VALUES 
('admin', '123', 'ADMIN'),
('user', '123', 'USER');



SELECT * FROM TayCam
SELECT * FROM DanhGia
SELECT * FROM DatTruoc
SELECT * FROM users

ALTER TABLE TayCam ADD preorder_discount FLOAT DEFAULT 0;

UPDATE TayCam
SET preorder_discount = 10
WHERE releaseDate > GETDATE();


ALTER TABLE users ADD hoTen NVARCHAR(100);
ALTER TABLE users ADD soDienThoai NVARCHAR(20);
ALTER TABLE users ADD email NVARCHAR(150);
ALTER TABLE users ADD diaChi NVARCHAR(255);

SELECT TOP 20 * FROM DatTruocItem ORDER BY id DESC;   -- SQL Server

CREATE UNIQUE INDEX UX_users_username ON users(username);

-- đổi tên bảng/cột cho khớp dự án của bạn
UPDATE users
SET password = CONCAT('{noop}', password)
WHERE password NOT LIKE '{%';

UPDATE users SET password = '{noop}123' WHERE username='user';

UPDATE [users]
SET password = '{noop}' + password
WHERE password NOT LIKE '{%}';

update users
set password = '{bcrypt}$2a$10$C6B5x89...'
where username = 'poly'

-- 1) Bỏ trùng '{noop}{noop}' -> '{noop}'
UPDATE [users]
SET password = REPLACE(password, '{noop}{noop}', '{noop}')
WHERE password LIKE '%{noop}{noop}%';

-- 2) (tuỳ chọn) Tìm mọi mật khẩu không có prefix để xử lý tiếp
SELECT id, username, password
FROM [User]
WHERE password NOT LIKE '{%}%';

UPDATE TayCam
SET hinhAnh = '/images/Vader4Pro.jpg'
WHERE maTayCam BETWEEN 1 AND 106;

SELECT * FROM DatTruoc ORDER BY id DESC;

ALTER TABLE DatTruoc ADD ngayDat DATETIME2 NULL;
ALTER TABLE DatTruoc ADD trangThai NVARCHAR(50) NULL;
ALTER TABLE DatTruoc ADD user_id INT NULL;

SELECT tc.CONSTRAINT_NAME, tc.CONSTRAINT_TYPE, ccu.COLUMN_NAME
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu
  ON tc.CONSTRAINT_NAME = ccu.CONSTRAINT_NAME
WHERE ccu.TABLE_NAME = 'users' AND ccu.COLUMN_NAME = 'username';

ALTER TABLE users DROP CONSTRAINT [UQ__users__F3DBC572CAC0FEFB];

ALTER TABLE users ALTER COLUMN username VARCHAR(255) NOT NULL;

ALTER TABLE users
ADD CONSTRAINT UQ_users_username UNIQUE (username);

SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users';
