package student;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

//&30
public class StudentVO {
    private String sno;
    private String snm;
    private String site_id;
    private String site_pw;
    private Date enter_date;
    private String site_join;

    public StudentVO() {}

    @Override
    public String toString() {
        return "학생정보 " +
                "[학번 = " + sno + '\'' +
                ", 학생이름 = " + snm + '\'' +
                ", 사이트아이디 =" + site_id + '\'' +
                ", 사이트비번 = " + site_pw + '\'' +
                ", 입학일자 = " + enter_date +
                ", 사이트 가입 =" + site_join + '\''+
                "]";
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getSnm() {
        return snm;
    }

    public void setSnm(String snm) {
        this.snm = snm;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getSite_pw() {
        return site_pw;
    }

    public void setSite_pw(String site_pw) {
        this.site_pw = site_pw;
    }

    public Date getEnter_date() {
        return enter_date;
    }

    public void setEnter_date(Date enter_date) {
        this.enter_date = enter_date;
    }

    public String getSite_join() {
        return site_join;
    }

    public void setSite_join(String site_join) {
        this.site_join = site_join;
    }

    public StudentVO(String sno, String snm, java.sql.Date enter_date) {
        this.sno = sno;
        this.snm = snm;
        this.enter_date = enter_date;
    }

    public StudentVO(String sno, String snm, String site_id,
                     String site_pw, Date enterDate, String site_join) {
        this.sno = sno;
        this.snm = snm;
        this.site_id = site_id;
        this.site_pw = site_pw;
        this.enter_date = enterDate;
        this.site_join = site_join;
    }

    public StudentVO enrollStudentVO(Scanner scan) {

        String sno;
        while (true) {
            System.out.print("ℹ\uFE0F 학번(숫자만) : ");
            sno = scan.next();

            if (sno.length() > 20) {
                System.out.println("⚠\uFE0F 20자 이내로 해주세요.");
                continue;
            }

            if (sno.matches("\\d+")) {
                break;
            }
            System.out.println("⚠\uFE0F 학번은 숫자만 입력하세요.");
        }


        String snm;

        while (true) {
            System.out.print("ℹ\uFE0F 이름(한글/영문, 20자 이내) : ");
            snm = scan.next();

            if (snm.length() > 20) {
                System.out.println("⚠\uFE0F 20자 이내로 해주세요.");
                continue;
            }

            if (!snm.matches("[a-zA-Z가-힣]+")) {
                System.out.println("⚠\uFE0F 이름은 한글 또는 영문만 입력하세요.");
                continue;
            }

            break;
        }


        Date enterDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);

        while (true) {
            System.out.print("ℹ\uFE0F 입학일자(yyyyMMdd 또는 yyyy-MM-dd) : ");
            String inputDate = scan.next();

            // 🔑 remove '-' if exists
            inputDate = inputDate.replace("-", "");

            try {
                enterDate = sdf.parse(inputDate);
                break; // valid
            } catch (ParseException e) {
                System.out.println("⚠\uFE0F 날짜 형식이 올바르지 않습니다.");
            }
        }

        return new StudentVO(
                sno,
                snm,
                new java.sql.Date(enterDate.getTime())
        );

    }


}
