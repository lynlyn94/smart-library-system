package com.library.backend.entity;

/**
 * 管理员信息表
 * @TableName admin
 */
public class Admin {
    /**
     * 工号
     */
    private String adminId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 登录密码（加密存储）
     */
    private String password;

    /**
     * 职位
     */
    private String position;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 权限等级
     */
    private Integer privilegeLevel;

    /**
     * 工号
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * 工号
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    /**
     * 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 登录密码（加密存储）
     */
    public String getPassword() {
        return password;
    }

    /**
     * 登录密码（加密存储）
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 职位
     */
    public String getPosition() {
        return position;
    }

    /**
     * 职位
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 联系方式
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 联系方式
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 权限等级
     */
    public Integer getPrivilegeLevel() {
        return privilegeLevel;
    }

    /**
     * 权限等级
     */
    public void setPrivilegeLevel(Integer privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Admin other = (Admin) that;
        return (this.getAdminId() == null ? other.getAdminId() == null : this.getAdminId().equals(other.getAdminId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getPosition() == null ? other.getPosition() == null : this.getPosition().equals(other.getPosition()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getPrivilegeLevel() == null ? other.getPrivilegeLevel() == null : this.getPrivilegeLevel().equals(other.getPrivilegeLevel()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAdminId() == null) ? 0 : getAdminId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getPosition() == null) ? 0 : getPosition().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getPrivilegeLevel() == null) ? 0 : getPrivilegeLevel().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", adminId=").append(adminId);
        sb.append(", name=").append(name);
        sb.append(", password=").append(password);
        sb.append(", position=").append(position);
        sb.append(", phone=").append(phone);
        sb.append(", privilegeLevel=").append(privilegeLevel);
        sb.append("]");
        return sb.toString();
    }
}