# 玉山銀行 線上圖書借閱系統

一個以三層式架構（Web Server + Application Server + 關聯式資料庫）實作的線上圖書借閱系統，提供使用者註冊、登入、瀏覽書籍、借書與還書等功能。

## 技術架構

| 層級 | 技術 |
|------|------|
| 前端（展示層） | Vue 3 + Vite + Vue Router + Pinia + axios |
| 後端（應用層） | Spring Boot 3.5 + Spring Security + Spring JDBC |
| 資料庫 | MySQL 8.0（透過 Stored Procedure 存取） |
| 建置工具 | Maven（後端）、npm（前端） |
| 身分驗證 | JWT（JSON Web Token） |

### 後端分層設計

- **展示層（controller）**：`AuthController`、`BookController`、`BorrowController`，提供 RESTful API。
- **業務層（service）**：`AuthService`、`BookService`、`BorrowService`，處理業務邏輯（如密碼加密）。
- **資料層（repository）**：`UserRepository`、`BookRepository`、`BorrowRepository`，透過 `SimpleJdbcCall` 呼叫 Stored Procedure。
- **共用層（common）**：DTO、設定（Security/CORS）、JWT 工具、安全過濾器。

## 專案結構

```
.
├── backend/          # Spring Boot 後端
│   └── src/main/java/com/esunbank/library/
│       ├── controller/     # 展示層
│       ├── service/        # 業務層
│       ├── repository/     # 資料層
│       └── common/         # 共用層（dto/config/security/util）
├── frontend/         # Vue 前端
│   └── src/
│       ├── views/          # 頁面（登入/註冊/書單/我的借閱）
│       ├── stores/         # Pinia 狀態管理
│       ├── router/         # 路由
│       └── api/            # axios 設定
└── DB/               # 資料庫腳本
    ├── DDL.sql             # 建立資料庫、資料表、Stored Procedure
    └── DML.sql             # 測試資料
```

## 環境需求

- JDK 17
- Node.js 22+
- MySQL 8.0
- Maven（或使用專案內附的 mvnw）

## 安裝與啟動步驟

### 1. 建立資料庫

啟動 MySQL 後，執行 `DB/` 資料夾內的腳本（可用 MySQL Workbench 或指令）：

```
mysql -u root -p < DB/DDL.sql   # 建立資料庫、資料表、Stored Procedure
mysql -u root -p < DB/DML.sql   # 匯入測試資料
```

### 2. 設定後端

複製設定檔範本並填入你的資料庫連線資訊：

```
cd backend/src/main/resources
copy application.properties.example application.properties   # Windows
# 或 cp application.properties.example application.properties  # macOS/Linux
```

編輯 `application.properties`，將 `spring.datasource.password` 改為你的 MySQL 密碼，並自行設定一組至少 32 字元的 `jwt.secret`。

### 3. 啟動後端

```
cd backend
./mvnw spring-boot:run    # macOS/Linux
mvnw.cmd spring-boot:run   # Windows
```

後端啟動於 `http://localhost:8080`。

### 4. 啟動前端

```
cd frontend
npm install
npm run dev
```

前端啟動於 `http://localhost:5173`（若埠被佔用會自動改用 5174）。開啟瀏覽器即可使用。

## 資料表設計

| 資料表 | 說明 |
|--------|------|
| `user_account` | 帳號憑證（手機、加密密碼） |
| `user_profile` | 使用者資料（與 user_account 為 1:1） |
| `books` | 書籍書目（紙本與電子書共用） |
| `ebook_detail` | 電子書專屬明細（與 books 為 1:1，僅電子書有） |
| `inventory` | 庫存（可借單位，紙本複本或電子書授權） |
| `borrowing_records` | 借閱紀錄 |

## Stored Procedures

| 名稱 | 功能 |
|------|------|
| `sp_register_user` | 註冊（以 Transaction 同時寫入 account 與 profile） |
| `sp_get_account_by_phone` | 以手機查詢帳號（供登入驗證） |
| `sp_update_last_login` | 更新最後登入時間 |
| `sp_list_books` | 查詢書籍清單（含可借數量） |
| `sp_borrow_book` | 借書（Transaction 改狀態 + 新增紀錄） |
| `sp_return_book` | 還書（Transaction 更新歸還時間 + 改狀態） |
| `sp_list_my_borrows` | 查詢個人未歸還借閱清單 |

## 安全性設計

- **密碼保護**：使用 BCrypt 進行加鹽（salt）雜湊後儲存，資料庫不存明碼。
- **身分驗證**：採用 JWT。借書、還書等受保護的 API 必須攜帶有效 token；瀏覽書目則開放公開存取。
- **防 SQL Injection**：所有資料庫存取皆透過 Stored Procedure 並以參數化方式傳入，避免字串拼接。
- **防 XSS**：前端輸入經格式驗證；Vue 樣板預設對輸出內容進行 HTML 逸出（escape）。
- **交易完整性**：借書、還書、註冊等需同時異動多張資料表的操作，皆於 Stored Procedure 內以 Transaction（START TRANSACTION / COMMIT / ROLLBACK）確保一致性。

## 時區說明

所有時間欄位一律以 UTC（UTC+0）儲存（`UTC_TIMESTAMP()`），前端顯示時再轉為使用者本地時間。
