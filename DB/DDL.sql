-- =====================================================================
-- 玉山銀行 線上圖書借閱系統 - DDL (資料庫結構定義)
-- 內容：建立資料庫、七張資料表、八支 Stored Procedure
-- 字元集：utf8mb4 / utf8mb4_0900_ai_ci
-- 時區：所有時間欄位以 UTC (UTC+0) 儲存
-- =====================================================================

DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;
USE library_db;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 資料表
-- =====================================================================

-- books：書籍書目（紙本與電子書共用）
DROP TABLE IF EXISTS books;
CREATE TABLE books (
  isbn            VARCHAR(13)   NOT NULL COMMENT '國際標準書號',
  name            VARCHAR(255)  NOT NULL COMMENT '書名',
  author          VARCHAR(100)  DEFAULT NULL COMMENT '作者',
  translator      VARCHAR(100)  DEFAULT NULL COMMENT '譯者',
  original_author VARCHAR(100)  DEFAULT NULL COMMENT '原文作者',
  introduction    TEXT          DEFAULT NULL COMMENT '內容簡介',
  publisher       VARCHAR(100)  DEFAULT NULL COMMENT '出版社',
  published_date  DATE          DEFAULT NULL COMMENT '出版日期',
  published_place VARCHAR(50)   DEFAULT NULL COMMENT '出版地',
  category        VARCHAR(50)   DEFAULT NULL COMMENT '書籍分類',
  language        VARCHAR(20)   DEFAULT NULL COMMENT '語言',
  specification   VARCHAR(100)  DEFAULT NULL COMMENT '規格',
  suitable_age    VARCHAR(20)   DEFAULT NULL COMMENT '適讀年齡',
  purchase_price  DECIMAL(10,2) DEFAULT NULL COMMENT '購入價格',
  PRIMARY KEY (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ebook_detail：電子書專屬明細（與 books 為 1:1，僅電子書有此筆）
DROP TABLE IF EXISTS ebook_detail;
CREATE TABLE ebook_detail (
  isbn               VARCHAR(13)  NOT NULL COMMENT '對應書目 ISBN',
  file_format        VARCHAR(50)  DEFAULT NULL COMMENT '檔案格式',
  file_size          VARCHAR(20)  DEFAULT NULL COMMENT '檔案大小',
  recommended_device VARCHAR(100) DEFAULT NULL COMMENT '建議閱讀裝置',
  tts_support        VARCHAR(10)  DEFAULT NULL COMMENT 'TTS 語音朗讀 (有/無)',
  PRIMARY KEY (isbn),
  CONSTRAINT fk_ebook_books
    FOREIGN KEY (isbn) REFERENCES books (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- inventory：庫存（可借單位，紙本實體複本或電子書授權）
DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
  inventory_id BIGINT      NOT NULL AUTO_INCREMENT COMMENT '庫存唯一 ID',
  isbn         VARCHAR(13) NOT NULL COMMENT '對應書目 ISBN',
  barcode      VARCHAR(50) DEFAULT NULL COMMENT '實體條碼 (電子書可為空)',
  status       VARCHAR(20) NOT NULL COMMENT '在庫/出借中/整理中/遺失/損毀/廢棄',
  location     VARCHAR(50) DEFAULT NULL COMMENT '館藏位置',
  store_time   DATETIME    NOT NULL COMMENT '入庫時間 (UTC)',
  PRIMARY KEY (inventory_id),
  UNIQUE KEY uq_barcode (barcode),
  KEY fk_inventory_books (isbn),
  CONSTRAINT fk_inventory_books
    FOREIGN KEY (isbn) REFERENCES books (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- branch：分館（取書館）
DROP TABLE IF EXISTS branch;
CREATE TABLE branch (
  branch_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分館唯一 ID',
  branch_name VARCHAR(100) NOT NULL COMMENT '館名',
  city        VARCHAR(20)  DEFAULT NULL COMMENT '縣市',
  district    VARCHAR(20)  DEFAULT NULL COMMENT '區域',
  address     VARCHAR(255) DEFAULT NULL COMMENT '地址',
  phone       VARCHAR(50)  DEFAULT NULL COMMENT '電話',
  PRIMARY KEY (branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- user_account：帳號憑證（登入用）
DROP TABLE IF EXISTS user_account;
CREATE TABLE user_account (
  user_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '使用者唯一 ID',
  phone_number  VARCHAR(20)  NOT NULL COMMENT '手機、登入帳號、不可重複',
  password_hash VARCHAR(255) NOT NULL COMMENT '加鹽雜湊後的密碼',
  PRIMARY KEY (user_id),
  UNIQUE KEY uq_phone_number (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- user_profile：使用者資料（與 user_account 為 1:1）
DROP TABLE IF EXISTS user_profile;
CREATE TABLE user_profile (
  user_id           BIGINT       NOT NULL COMMENT '對應帳號 ID',
  user_name         VARCHAR(50)  NOT NULL COMMENT '使用者名稱',
  email             VARCHAR(100) DEFAULT NULL COMMENT '電子郵件',
  address           VARCHAR(255) DEFAULT NULL COMMENT '地址',
  birthday          DATE         DEFAULT NULL COMMENT '生日',
  default_branch    BIGINT       DEFAULT NULL COMMENT '預設取書館 (對應 branch.branch_id)',
  is_active         TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '帳號啟用狀態',
  registration_time DATETIME     NOT NULL COMMENT '註冊時間 (UTC)',
  last_login_time   DATETIME     DEFAULT NULL COMMENT '最後登入時間 (UTC)',
  PRIMARY KEY (user_id),
  CONSTRAINT fk_profile_account
    FOREIGN KEY (user_id) REFERENCES user_account (user_id),
  CONSTRAINT fk_profile_branch
    FOREIGN KEY (default_branch) REFERENCES branch (branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- borrowing_records：借閱紀錄
DROP TABLE IF EXISTS borrowing_records;
CREATE TABLE borrowing_records (
  record_id      BIGINT   NOT NULL AUTO_INCREMENT COMMENT '借閱紀錄唯一 ID',
  user_id        BIGINT   NOT NULL COMMENT '借閱人 (對應 user_account)',
  inventory_id   BIGINT   NOT NULL COMMENT '借閱的庫存單位',
  borrowing_time DATETIME NOT NULL COMMENT '借出時間 (UTC)',
  return_time    DATETIME DEFAULT NULL COMMENT '歸還時間 (UTC，NULL=未還)',
  PRIMARY KEY (record_id),
  KEY idx_user (user_id),
  KEY idx_inventory (inventory_id),
  CONSTRAINT fk_borrow_account
    FOREIGN KEY (user_id) REFERENCES user_account (user_id),
  CONSTRAINT fk_borrow_inventory
    FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- Stored Procedures
-- =====================================================================

-- sp_register_user：註冊（同時寫入 account 與 profile，以 Transaction 保證一致）
DROP PROCEDURE IF EXISTS sp_register_user;
DELIMITER $$
CREATE PROCEDURE sp_register_user(
  IN  p_phone_number   VARCHAR(20),
  IN  p_password_hash  VARCHAR(255),
  IN  p_user_name      VARCHAR(50),
  IN  p_email          VARCHAR(100),
  IN  p_address        VARCHAR(255),
  IN  p_birthday       DATE,
  IN  p_default_branch BIGINT,
  OUT p_user_id        BIGINT
)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN ROLLBACK; RESIGNAL; END;

  START TRANSACTION;

  INSERT INTO user_account (phone_number, password_hash)
  VALUES (p_phone_number, p_password_hash);

  SET p_user_id = LAST_INSERT_ID();

  INSERT INTO user_profile
    (user_id, user_name, email, address, birthday, default_branch, registration_time)
  VALUES
    (p_user_id, p_user_name, p_email, p_address, p_birthday, p_default_branch, UTC_TIMESTAMP());

  COMMIT;
END$$
DELIMITER ;

-- sp_get_account_by_phone：以手機號碼查詢帳號（供後端驗證登入）
DROP PROCEDURE IF EXISTS sp_get_account_by_phone;
DELIMITER $$
CREATE PROCEDURE sp_get_account_by_phone(
  IN p_phone_number VARCHAR(20)
)
BEGIN
  SELECT a.user_id, a.phone_number, a.password_hash, p.user_name
  FROM user_account a
  JOIN user_profile p ON a.user_id = p.user_id
  WHERE a.phone_number = p_phone_number COLLATE utf8mb4_0900_ai_ci;
END$$
DELIMITER ;

-- sp_update_last_login：更新最後登入時間
DROP PROCEDURE IF EXISTS sp_update_last_login;
DELIMITER $$
CREATE PROCEDURE sp_update_last_login(
  IN p_user_id BIGINT
)
BEGIN
  UPDATE user_profile
  SET last_login_time = UTC_TIMESTAMP()
  WHERE user_id = p_user_id;
END$$
DELIMITER ;

-- sp_list_books：查詢書籍清單（含可借數量與一個可借的 inventory_id）
DROP PROCEDURE IF EXISTS sp_list_books;
DELIMITER $$
CREATE PROCEDURE sp_list_books()
BEGIN
  SELECT
    b.isbn, b.name, b.author, b.translator, b.original_author,
    b.introduction, b.publisher, b.published_date, b.category, b.language,
    COUNT(i.inventory_id) AS total_count,
    SUM(CASE WHEN i.status = '在庫' THEN 1 ELSE 0 END) AS available_count,
    MIN(CASE WHEN i.status = '在庫' THEN i.inventory_id END) AS available_inventory_id
  FROM books b
  LEFT JOIN inventory i ON b.isbn = i.isbn
  GROUP BY b.isbn, b.name, b.author, b.translator, b.original_author,
           b.introduction, b.publisher, b.published_date, b.category, b.language
  ORDER BY b.isbn;
END$$
DELIMITER ;

-- sp_borrow_book：借書（改庫存狀態 + 新增借閱紀錄，以 Transaction 保證一致）
DROP PROCEDURE IF EXISTS sp_borrow_book;
DELIMITER $$
CREATE PROCEDURE sp_borrow_book(
  IN  p_user_id      BIGINT,
  IN  p_inventory_id BIGINT,
  OUT p_record_id    BIGINT
)
BEGIN
  DECLARE v_status VARCHAR(20);
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN ROLLBACK; RESIGNAL; END;

  START TRANSACTION;

  SELECT status INTO v_status
  FROM inventory
  WHERE inventory_id = p_inventory_id
  FOR UPDATE;

  IF v_status IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '庫存不存在';
  ELSEIF v_status <> '在庫' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '此書目前無法借閱';
  END IF;

  UPDATE inventory SET status = '出借中' WHERE inventory_id = p_inventory_id;

  INSERT INTO borrowing_records (user_id, inventory_id, borrowing_time)
  VALUES (p_user_id, p_inventory_id, UTC_TIMESTAMP());

  SET p_record_id = LAST_INSERT_ID();

  COMMIT;
END$$
DELIMITER ;

-- sp_return_book：還書（更新歸還時間 + 改庫存狀態，以 Transaction 保證一致）
DROP PROCEDURE IF EXISTS sp_return_book;
DELIMITER $$
CREATE PROCEDURE sp_return_book(
  IN p_inventory_id BIGINT
)
BEGIN
  DECLARE v_record_id BIGINT;
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN ROLLBACK; RESIGNAL; END;

  START TRANSACTION;

  SELECT record_id INTO v_record_id
  FROM borrowing_records
  WHERE inventory_id = p_inventory_id AND return_time IS NULL
  ORDER BY borrowing_time DESC
  LIMIT 1
  FOR UPDATE;

  IF v_record_id IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '查無未歸還的借閱紀錄';
  END IF;

  UPDATE borrowing_records SET return_time = UTC_TIMESTAMP() WHERE record_id = v_record_id;
  UPDATE inventory SET status = '在庫' WHERE inventory_id = p_inventory_id;

  COMMIT;
END$$
DELIMITER ;

-- sp_list_my_borrows：查詢某使用者目前未歸還的借閱清單（含書名）
DROP PROCEDURE IF EXISTS sp_list_my_borrows;
DELIMITER $$
CREATE PROCEDURE sp_list_my_borrows(
  IN p_user_id BIGINT
)
BEGIN
  SELECT
    br.record_id, br.inventory_id, br.borrowing_time,
    b.isbn, b.name AS book_name, b.author, inv.barcode
  FROM borrowing_records br
  JOIN inventory inv ON br.inventory_id = inv.inventory_id
  JOIN books b ON inv.isbn = b.isbn
  WHERE br.user_id = p_user_id AND br.return_time IS NULL
  ORDER BY br.borrowing_time DESC;
END$$
DELIMITER ;

-- sp_list_branches：查詢分館清單（供註冊頁下拉選單）
DROP PROCEDURE IF EXISTS sp_list_branches;
DELIMITER $$
CREATE PROCEDURE sp_list_branches()
BEGIN
  SELECT branch_id, branch_name, city, district
  FROM branch
  ORDER BY branch_id;
END$$
DELIMITER ;

-- =====================================================================
-- DDL 結束
-- =====================================================================