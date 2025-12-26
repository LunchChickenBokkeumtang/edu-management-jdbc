package student;

import common.Session;
import student.exam.ExamController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class StudentController {
    private Session session;
    private Connection connect;
    private ExamController examController;

    public StudentController(Session session, Connection connect) {
        this.session = session;
        this.connect = connect;
        this.examController = new ExamController(connect);
    }

    //학생 메뉴 메소드
    public void start(Scanner scan) {
        while (session.getStudentId() == null) {
            System.out.println("⏺\uFE0F 학생 메뉴 시작");
            System.out.println("메뉴선택 : \uD83D\uDD39 1. 로그인 \t \uD83D\uDD39 2. 회원가입 \t \uD83D\uDD39 3. 홈으로 ");
            String input = scan.next();

            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }

            int studentMenu = Integer.parseInt(input);

            switch (studentMenu) {
                case 1 -> studentLogin(scan);
                case 2 -> studentJoin(scan);
                case 3 -> {
                    return; // 홈으로
                }
                default -> System.out.println("⚠\uFE0F 해당 번호는 존재하지 않습니다.");
            }
        }
        studentMainMenu(scan);
    }//학생 메뉴 메소드 종료

    //학생 로그인 메소드
    private void studentLogin(Scanner scan) {
        System.out.println("⏺\uFE0F 학생 로그인 메뉴");

        String sql = "select * from student where site_id = ? and site_pw = ?";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            String id;
            String pw;
            while (true) {
                System.out.print("ℹ\uFE0F 아이디입력>> "); //20글자//null값 불가처리
                id = scan.next(); // next~ 콘솔 입력자리
                if (id.length() > 20) {
                    System.out.println("⚠\uFE0F 최대 20Byte 이하로 입력하셔야 합니다.");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.print("ℹ\uFE0F 비밀번호입력>> "); //20글자//null값 불가처리
                pw = scan.next(); // next~ 콘솔 입력자리
                if (pw.length() > 20) {
                    System.out.println("⚠\uFE0F 최대 20Byte 이하로 입력하셔야 합니다.");
                } else {
                    break;
                }
            }

            ps.setString(1, id);
            ps.setString(2, pw);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    session.setStudentId(rs.getString("site_id"));
                    System.out.println("🎉" + rs.getString("snm") + "님 환영합니다. \uD83C\uDF89");
                } else {
                    System.out.println("⚠\uFE0F 등록된 정보가 없거나 일치하지 않습니다.");
                }
            }

        } catch (Exception e) {
            System.out.println("⚠\uFE0F 학생 로그인 오류");
        }
    } //학생 로그인 메소드 종료

    //학생 회원가입 메소드
    public void studentJoin(Scanner scan) {
        String sqlCheckSno =
                "SELECT sno FROM student WHERE sno = ?";
        String sqlCheckSnoSnm =
                "SELECT sno FROM student WHERE sno = ? AND snm = ?";
        String sqlCheckId =
                "SELECT site_id FROM student WHERE site_id = ?";
        String sqlUpdate =
                "UPDATE student " +
                        "SET site_id = ?, site_pw = ?, site_join = '사이트가입' " +
                        "WHERE sno = ?";
        String sno;
        String siteId;
        String sitePw;
        String snm;

        while (true) {
            System.out.print("ℹ\uFE0F 학번 입력 >> ");
            sno = scan.next();

            System.out.print("ℹ\uFE0F 아이디 입력 >> ");
            siteId = scan.next();

            System.out.print("ℹ\uFE0F 비밀번호 입력 >> ");
            sitePw = scan.next();

            System.out.print("ℹ\uFE0F 이름 입력 >> ");
            snm = scan.next();

            String errorMsg = validateStudentInput(sno, siteId, sitePw, snm);

            if (errorMsg != null) {
                System.out.println(errorMsg);
                System.out.println("⚠\uFE0F 다시 입력해주세요.\n");
                continue;
            }
            break;
        }

        try {
            connect.setAutoCommit(false);
            try (PreparedStatement psSno =
                         connect.prepareStatement(sqlCheckSno)) {

                psSno.setString(1, sno);

                try (ResultSet rs = psSno.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("⚠\uFE0F 학번이 존재하지 않습니다.");
                        connect.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psSnoSnm =
                         connect.prepareStatement(sqlCheckSnoSnm)) {

                psSnoSnm.setString(1, sno);
                psSnoSnm.setString(2, snm);

                try (ResultSet rs = psSnoSnm.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("⚠\uFE0F 이름이 일치하지 않습니다.");
                        connect.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psCheckId =
                         connect.prepareStatement(sqlCheckId)) {

                psCheckId.setString(1, siteId);

                try (ResultSet rs = psCheckId.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("⚠\uFE0F 이미 존재하는 아이디입니다.");
                        connect.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psUpdate =
                         connect.prepareStatement(sqlUpdate)) {

                psUpdate.setString(1, siteId);
                psUpdate.setString(2, sitePw);
                psUpdate.setString(3, sno);

                if (psUpdate.executeUpdate() == 0) {
                    System.out.println("회원가입 실패");
                    connect.rollback();
                    return;
                }
            }
            connect.commit();
            System.out.println("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            try {
                connect.rollback();
            } catch (Exception ignore) {
            }
            System.out.println("⚠\uFE0F 회원가입 처리 중 오류가 발생했습니다.");
        } finally {
            try {
                connect.setAutoCommit(true);
            } catch (Exception ignore) {
            }
        }
    }//학생 회원가입 메소드 종료

    //학생 로그인 후 메뉴 메소드
    private void studentMainMenu(Scanner scan) {
        while (true) {
            System.out.println("⏺\uFE0F 학생 메뉴 선택 : \uD83D\uDD39 1. 시험응시 \t \uD83D\uDD39 2. 점수확인 \t \uD83D\uDD39 3. 로그아웃");
            System.out.print("ℹ\uFE0F 선택 >> ");

            String input = scan.next();

            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }

            int menu = Integer.parseInt(input);

            switch (menu) {
                case 1 -> examController.takeExam(scan,session.getStudentId()); //시험응시
                case 2 -> examController.showScoreDetail(scan,session.getStudentId()); //점수확인
                case 3 -> { //로그아웃
                    session.setStudentId(null);
                    System.out.println("ℹ\uFE0F 로그아웃 되었습니다.");
                    return;
                }
                default -> System.out.println("⚠\uFE0F 잘못된 메뉴 입력입니다.");
            }
        }
    }//학생 로그인 후 메뉴 메소드 종료

    //입력값 검증 메소드
    private String validateStudentInput(String sno, String siteId, String sitePw, String snm) {
        if (!sno.matches("^\\d{1,30}$")) {
            return "⚠\uFE0F 학번은 숫자만 가능하며 1~30자리여야 합니다.";
        }

        if (!siteId.matches("^[a-zA-Z가-힣0-9]{1,20}$")) {
            return "⚠\uFE0F 아이디는 최대 20자까지 가능합니다.";
        }

        if (sitePw.length() > 20) {
            return "⚠\uFE0F 비밀번호는 최대 20자까지 가능합니다.";
        }

        if (!snm.matches("^[a-zA-Z가-힣]{1,20}$")) {
            return "⚠\uFE0F 이름은 영문 또는 한글만 가능하며 1~20자여야 합니다.";
        }

        return null;
    }//입력값 검증 메소드 종료
}


