package com.esunbank.library.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "手機號碼不可為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, message = "密碼至少 6 碼")
    private String password;

    @NotBlank(message = "使用者名稱不可為空")
    private String userName;

    // 以下為選填欄位
    private String email;
    private String address;
    private String birthday;  // 格式：yyyy-MM-dd

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    private Long defaultBranch;

    public Long getDefaultBranch() { return defaultBranch; }
    public void setDefaultBranch(Long defaultBranch) { this.defaultBranch = defaultBranch; }
}