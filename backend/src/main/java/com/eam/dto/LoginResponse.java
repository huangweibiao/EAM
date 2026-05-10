package com.eam.dto;

/**
 * Login Response DTO
 */
public class LoginResponse {

    /**
     * Access token
     */
    private String accessToken;


    /**
     * Token type (typically "Bearer")
     */
    private String tokenType = "Bearer";

    /**
     * User information
     */
    private UserInfo userInfo;

    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String avatar;
        private Long deptId;
        private Integer status;

        public UserInfo() {
        }

        public UserInfo(Long id, String username, String nickname, String email, String avatar, Long deptId, Integer status) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.email = email;
            this.avatar = avatar;
            this.deptId = deptId;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
