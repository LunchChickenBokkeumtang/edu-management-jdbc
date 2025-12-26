package admin;

public class AdminVO {
    private String aid;
    private String apw;
    private String anm;
    private String arole;
    private String acon;

    public AdminVO(String aid, String apw, String anm, String arole, String acon) {

        this.aid = aid;
        this.apw = apw;
        this.anm = anm;
        this.arole = arole;
        this.acon = acon;

    }


    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getApw() {
        return apw;
    }

    public void setApw(String apw) {
        this.apw = apw;
    }

    public String getAnm() {
        return anm;
    }

    public void setAnm(String anm) {
        this.anm = anm;
    }

    public String getArole() {
        return arole;
    }

    public void setArole(String arloe) {
        this.arole = arole;
    }

    public String getAcon() {
        return acon;
    }

    public void setAcon(String acon) {
        this.acon = acon;
    }

    private String getCondition() {
        if ("ON".equalsIgnoreCase(acon)) return "사용가능";
        if ("OFF".equalsIgnoreCase(acon)) return "사용불가 또는 대기";
        return "입력값을 알 수 없음.";
    }

    @Override
    public String toString() {
        return "관리자목록 [" +
                "아이디='" + aid + '\'' +
                ", 비밀번호='" + apw + '\'' +
                ", 이름='" + anm + '\'' +
                ", 역할='" + arole + '\'' +
                ", 상태='" + getCondition() + '\'' +
                ']';
    }
}

