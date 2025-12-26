package common;

public class Session {

    private String adminId;
    private String studentId;

    /* ================= 로그인 상태 체크 ================= */

    public boolean isAdminLogin() {
        return adminId != null;
    }

    public boolean isStudentLogin() {
        return studentId != null;
    }

    /* ================= getter / setter ================= */

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /* ================= 로그아웃 ================= */

    public void logoutAdmin() {
        this.adminId = null;
    }

    public void logoutStudent() {
        this.studentId = null;
    }

    public void logoutAll() {
        logoutAdmin();
        logoutStudent();
    }
}
