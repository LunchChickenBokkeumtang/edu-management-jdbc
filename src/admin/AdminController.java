package admin;

import common.Session;
import student.StudentVO;

import java.sql.*;
import java.util.Scanner;

public class AdminController {

    private Session session;
    private Connection connect;

    public AdminController(Session session, Connection connect) {
        this.session = session;
        this.connect = connect;
    }

    // 관리자 메뉴 메소드
    public void start(Scanner scan) {
        while (session.getAdminId() == null) {
            System.out.println("\uD83D\uDD39 관리자 메뉴 시작");
            System.out.println("메뉴선택 : \uD83D\uDD39 1. 관리자 로그인 \t \uD83D\uDD39 2. 관리자 가입 \t \uD83D\uDD39 3. 홈으로 ");
            String input = scan.next();

            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }

            int adminMenu = Integer.parseInt(input);

            switch (adminMenu) {
                case 1 -> adminLogin(scan); //관리자 로그인
                case 2 -> adminJoin(scan); //관리자 가입
                case 3 -> {
                    return; // 홈으로
                }
                default -> System.out.println("⚠\uFE0F 해당 번호는 존재하지 않습니다.");
            }
        }
        adminMainMenu(scan);
    } // 관리자 메뉴 메소드 종료

    // 관리자 로그인 메소드
    private void adminLogin(Scanner scan) {
        System.out.println("\uD83D\uDD39 관리자 로그인 메뉴");

        String sql = "select aid, anm, acon from admin where aid = ? and apw = ?";

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
                System.out.print("ℹ\uFE0F 비번입력>> "); //20글자//null값 불가처리
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
                    if(rs.getString("acon").equals("ON")){
                        session.setAdminId(rs.getString("aid"));
                        System.out.println("\uD83C\uDF89 " + rs.getString("anm") + " 관리자님 환영합니다. \uD83C\uDF89");
                    } else{
                        System.out.println("\uD83D\uDD12 해당 계정은 정지되었습니다. ");
                    }
                } else {
                    System.out.println("⚠\uFE0F 정보가 일치하지 않습니다. 다시 입력해주세요.");
                }
            }

        } catch (Exception e) {
            System.out.println("⚠\uFE0F 관리자 로그인 오류");
        }
    } //관리자 로그인 메소드 종료

    //관리자 가입 메소드
    private void adminJoin(Scanner scan) {
        System.out.println("\uD83D\uDD39 관리자 가입 메뉴 시작");
        String sql = " INSERT INTO admin(aid, apw, anm, arole, acon) VALUES( ?, ?, ?,'SUB', 'OFF') ";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            String id;
            String pw;
            String name;

            while (true) {
                System.out.print("ℹ\uFE0F 아이디입력>> "); //20글자//null값 불가처리
                id = scan.next(); // next~ 콘솔 입력자리
                if (id.length() > 20) {
                    System.out.println("⚠\uFE0F 최대 20Byte 이하로 입력하셔야 합니다.");
                }
                if (!id.matches("^[a-zA-Z0-9]+$")) {
                    System.out.println("⚠\uFE0F 아이디는 영문자와 숫자만 입력 가능합니다.");
                    continue;
                }
                break;
            }

            while (true) {
                System.out.print("ℹ\uFE0F 비밀번호>> "); //20글자//null값 불가처리
                pw = scan.next(); // next~ 콘솔 입력자리
                if (pw.length() > 20) {
                    System.out.println("⚠\uFE0F 최대 20Byte 이하로 입력하셔야 합니다.");
                } else {
                    break;
                }
            }

            while (true) {
                System.out.print("ℹ\uFE0F 이름입력>> "); //20글자//null값 불가처리
                name = scan.next(); // next~ 콘솔 입력자리
                if (name.length() > 20) {
                    System.out.println("⚠\uFE0F 최대 20Byte 이하로 입력하셔야 합니다.");
                }
                if (!name.matches("^[가-힣]+$")) {
                    System.out.println("⚠\uFE0F 이름은 한글만 가능합니다.");
                    continue;
                }
                break;
            }

            ps.setString(1, id);
            ps.setString(2, pw);
            ps.setString(3, name);

            int count = ps.executeUpdate();

            if (count > 0) {
                System.out.println("관리자 가입 성공!");
            } else {
                System.out.println("관리자 가입 실패");
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {

                String msg = e.getMessage();
                String duplicatedValue = "";

                int start = msg.indexOf("'") + 1;
                int end = msg.indexOf("'", start);

                if (start > 0 && end > start) {
                    duplicatedValue = msg.substring(start, end);
                }
                System.out.println("⚠\uFE0F 이미 사용 중인 ID 입니다." + duplicatedValue);

            } else {
                System.out.println("⚠\uFE0F 관리자 가입 오류");
            }
        }
    } //관리자 가입 메소드 종료

    // 관리자 로그인 후 메뉴 메소드
    private void adminMainMenu(Scanner scan) {
        while (true) {
            System.out.println("=== 관리자 기능 메뉴 ===");
            System.out.print("\uD83D\uDD39 1. 학생 등록");
            System.out.print("\uD83D\uDD39 2. 학생 목록");
            System.out.print("\uD83D\uDD39 3. 관리자 목록"); // super만
            System.out.print("\uD83D\uDD39 4. 상태 변경"); // super만
            System.out.print("\uD83D\uDD39 5. 역할 변경"); // super만
            System.out.println("\uD83D\uDD39 6. 로그아웃");

            String input = scan.next();

            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }

            int menu = Integer.parseInt(input);

            switch (menu) {
                case 1 -> enrollStudent(scan); // 학생등록
                case 2 -> sList(); // 학생목록
                case 3 -> aList(); // 관리자목록
                case 4 -> changeAdminStatus(scan, session.getAdminId(), session); //상태변경
                case 5 -> aroleChange(scan, session.getAdminId(), session); // 역할변경
                case 6 -> { //로그아웃
                    session.setAdminId(null);
                    System.out.println("ℹ\uFE0F 로그아웃 되었습니다.");
                    return;
                }
                default -> System.out.println("⚠\uFE0F 잘못된 메뉴 입력입니다.");
            }
        }
    }// 관리자 로그인 후 메뉴 메소드 종료

    //학생등록 메소드
    public void enrollStudent(Scanner scan) {
        String sql = "INSERT INTO student (sno, snm, enter_date) VALUES (?, ?, ?)";

        while (true) {
            StudentVO vo = new StudentVO();
            StudentVO boardVO = vo.enrollStudentVO(scan);

            try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
                pstmt.setString(1, boardVO.getSno());
                pstmt.setString(2, boardVO.getSnm());
                pstmt.setDate(3,
                        new java.sql.Date(boardVO.getEnter_date().getTime()));
                pstmt.executeUpdate();
                System.out.println("✅ 학생 등록 성공");
                break;
            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("⚠\uFE0F 이미 등록된 학번입니다. 다시 입력하세요.\n");
            } catch (Exception e) {
                System.out.println("⚠\uFE0F 학생 등록 중 오류가 발생했습니다.\n");
            }
        }
    }//학생등록 메소드 종료

    //학생목록 메소드
    public void sList() {
        String sql = "SELECT sno, snm, site_id, site_pw, enter_date, site_join FROM student";

        try (PreparedStatement ps = connect.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("============== 학생 목록 ==============");

            // 🔹 헤더
            System.out.printf(
                    "%-15s | %-12s | %-20s | %-18s | %-12s | %-12s%n",
                    "학번", "이름", "아이디", "비밀번호", "입학일", "상태"
            );
            System.out.println("--------------------------------------------------------------------------------");

            // 🔹 데이터 출력
            while (rs.next()) {
                String sno = rs.getString("sno");
                String snm = rs.getString("snm");
                String siteId = rs.getString("site_id");
                String sitePw = rs.getString("site_pw");
                String enterDate = String.valueOf(rs.getDate("enter_date"));
                String siteJoin = rs.getString("site_join");

                // null 처리
                siteId = (siteId == null) ? "-" : siteId;
                sitePw = (sitePw == null) ? "-" : sitePw;
                siteJoin = (siteJoin == null) ? "-" : siteJoin;

                System.out.printf(
                        "%-15s | %-12s | %-20s | %-18s | %-12s | %-12s%n",
                        sno, snm, siteId, sitePw, enterDate, siteJoin
                );
            }
            System.out.println("======================================");
        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 학생 목록 조회 오류");
        }
    }//학생 목록 메소드 종료

    //관리자 목록 메소드
    public void aList() {
        if (!isSuperAdmin()) {
            System.out.println("\uD83D\uDD12 SUPER 관리자만 접근 가능합니다.");
            return;
        }

        String sql = "SELECT aid, apw, anm, arole, acon FROM admin";

        try (PreparedStatement ps = connect.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("============== 관리자 목록 ==============");

            // 🔹 헤더
            System.out.printf(
                    "%-15s | %-18s | %-12s | %-10s | %-10s%n",
                    "아이디", "비밀번호", "이름", "역할", "상태"
            );

            System.out.println("--------------------------------------------------------------------------");

            // 🔹 데이터
            while (rs.next()) {
                String aid   = rs.getString("aid");
                String apw   = rs.getString("apw");
                String anm   = rs.getString("anm");
                String arole = rs.getString("arole");
                String acon  = rs.getString("acon");

                // null 처리
                apw   = (apw == null) ? "-" : apw;
                anm   = (anm == null) ? "-" : anm;
                arole = (arole == null) ? "-" : arole;
                acon  = (acon == null) ? "-" : acon;

                System.out.printf(
                        "%-15s | %-18s | %-12s | %-10s | %-10s%n",
                        aid, apw, anm, arole, acon
                );
            }
            System.out.println("========================================");
        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 관리자 목록 조회 오류!");
        }
    }//관리자 목록 메소드 종료

    //상태 변경 메소드
    private void changeAdminStatus(Scanner scan, String loginAdminId, Session session) {
        // 로그인 관리자 권한 조회
        String arole = null;
        String roleSql = "SELECT arole FROM admin WHERE aid = ?";

        try (PreparedStatement ps = connect.prepareStatement(roleSql)) {
            ps.setString(1, loginAdminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arole = rs.getString("arole");
                }
            }
        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 관리자 권한 조회 SQL 오류");
            return;
        }

        if (arole == null || !arole.equalsIgnoreCase("super")) {
            System.out.println("\uD83D\uDD12 SUPER 관리자만 접근 가능합니다.");
            return;
        }

        /* ================= 관리자 상태 목록 (최초 1회 출력) ================= */
        System.out.println("\n====== 관리자 상태 목록 ======");

        String listSql = "SELECT aid, acon FROM admin ORDER BY acon DESC, aid";
        try (PreparedStatement ps = connect.prepareStatement(listSql);
             ResultSet rs = ps.executeQuery()) {

            System.out.printf("%-15s | %-10s%n", "아이디", "상태");
            System.out.println("--------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s | %-10s%n",
                        rs.getString("aid"),
                        rs.getString("acon"));
            }

            System.out.println("================================\n");

        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 관리자 목록 조회 오류");
            return;
        }

        /* ================= 상태 변경 반복 ================= */
        int continueChoice;

        do {
            // 변경할 관리자 ID 입력
            String targetId;
            while (true) {
                System.out.print("ℹ\uFE0F 변경할 아이디 입력 >> ");
                targetId = scan.next();

                if (targetId.equals("admin")) {
                    System.out.println("⚠\uFE0F\n admin 계정은 상태 변경이 불가능합니다.");
                    continue;
                }

                String checkSql = "SELECT aid FROM admin WHERE aid = ?";
                try (PreparedStatement ps = connect.prepareStatement(checkSql)) {
                    ps.setString(1, targetId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) break;
                    }
                } catch (SQLException e) {
                    System.out.println("⚠\uFE0F 관리자 존재 여부 확인 SQL 오류");
                    return;
                }

                System.out.println("⚠\uFE0F 해당 ID는 관리자 목록에 없습니다. 다시 입력하세요.");
            }

            // 상태 입력
            int changingStatus;
            while (true) {
                System.out.print("ℹ\uFE0F 변경할 상태 입력 (\uD83D\uDD39 0.사용불가  \uD83D\uDD39 1.사용가능) >> ");

                if (!scan.hasNextInt()) {
                    System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                    scan.next();
                    continue;
                }

                changingStatus = scan.nextInt();
                if (changingStatus == 0 || changingStatus == 1) break;

                System.out.println("⚠\uFE0F 0 또는 1만 입력 가능합니다.");
            }

            // 상태 업데이트
            String updateSql = "UPDATE admin SET acon = ? WHERE aid = ?";
            try (PreparedStatement ps = connect.prepareStatement(updateSql)) {
                ps.setString(1, changingStatus == 1 ? "ON" : "OFF");
                ps.setString(2, targetId);

                int rowCnt = ps.executeUpdate();
                System.out.println(rowCnt > 0 ? "상태가 변경되었습니다." : "변경 실패");

            } catch (SQLException e) {
                System.out.println("⚠\uFE0F 상태 변경 SQL 오류");
                return;
            }

            // 자기 자신 OFF → 즉시 로그아웃
            if (targetId.equals(loginAdminId) && changingStatus == 0) {
                System.out.println("ℹ\uFE0F 관리자님의 상태가 '사용불가'로 변경되어 자동 로그아웃됩니다.");
                session.logoutAdmin();
                start(scan);
                return;
            }

            // 이어서 여부
            while (true) {
                System.out.print("ℹ\uFE0F 이어서 하시겠습니까? (\uD83D\uDD39 0.No  \uD83D\uDD39 1.Yes) >> ");

                if (!scan.hasNextInt()) {
                    System.out.println("⚠\uFE0F 숫자만 입력하세요.");
                    scan.next();
                    continue;
                }

                continueChoice = scan.nextInt();
                if (continueChoice == 0 || continueChoice == 1) break;

                System.out.println("⚠\uFE0F 0 또는 1만 입력 가능합니다.");
            }
        } while (continueChoice == 1);
    }//상태 변경 메소드 종료

    //역할 변경 메소드
    public void aroleChange(Scanner scan, String loginAdminId, Session session) {
        String arole = null;
        String roleSql = "select arole from admin where aid = ?";

        try (PreparedStatement ps = connect.prepareStatement(roleSql)) {
            ps.setString(1, loginAdminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arole = rs.getString("arole");
                }
            }
        } catch (SQLException e) {
            System.out.println("관리자 권한 조회 SQL 오류");
            return;
        }

        // 권한 체크
        if (arole == null || arole.isEmpty()) {
            System.out.println("존재하지 않는 관리자입니다.");
            return;
        }

        if (!arole.equalsIgnoreCase("super")) {
            System.out.println("\uD83D\uDD12 SUPER 관리자만 접근 가능합니다.");
            return;
        }

        loginAdminId = session.getAdminId();
        String aid;
        int aroleNo;

        int continueChoice;

        /* ===== 관리자 목록 (최초 1회만 출력) ===== */
        System.out.println("\n============ 관리자 목록 ============");
        String sqlAdminList = "SELECT aid, arole FROM admin ORDER BY arole DESC, aid";

        try (PreparedStatement ps = connect.prepareStatement(sqlAdminList);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.printf("아이디: %-15s | 역할: %s%n",
                        rs.getString("aid"),
                        rs.getString("arole"));
            }

        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 관리자 목록 조회 오류!");
            return;
        }

        System.out.println("====================================\n");

        /* ===== 역할 변경 반복 ===== */
        do {

            /* === 변경할 아이디 입력 === */
            while (true) {
                System.out.print("ℹ\uFE0F 변경할 아이디 입력 >> ");
                aid = scan.next();

                String sqlCheck = "SELECT COUNT(*) FROM admin WHERE aid = ?";
                try (PreparedStatement ps = connect.prepareStatement(sqlCheck)) {
                    ps.setString(1, aid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            System.out.println("⚠\uFE0F 존재하지 않는 관리자입니다.");
                            continue;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("⚠\uFE0F 관리자 존재 여부 조회 오류!");
                    return;
                }
                break;
            }

            /* === 변경할 역할 입력 === */
            while (true) {
                System.out.print("ℹ\uFE0F 변경할 역할 입력(\uD83D\uDD39 1.SUPER  \uD83D\uDD39 2.SUB) >> ");
                String input = scan.next();

                try {
                    aroleNo = Integer.parseInt(input);
                    if (aroleNo != 1 && aroleNo != 2) {
                        System.out.println("⚠\uFE0F 1 또는 2만 입력하세요.");
                        continue;
                    }
                    arole = (aroleNo == 1) ? "SUPER" : "SUB";
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("⚠\uFE0F 숫자로 입력해주세요.");
                }
            }

            /* === 역할 변경 === */
            String sqlUpdate = """
                UPDATE admin
                SET arole = ?
                WHERE aid = ?
                AND EXISTS (
                    SELECT 1 FROM admin WHERE aid = ? AND arole = 'SUPER'
                )
                """;

            int count;
            try (PreparedStatement ps = connect.prepareStatement(sqlUpdate)) {
                ps.setString(1, arole);
                ps.setString(2, aid);
                ps.setString(3, loginAdminId);
                count = ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("⚠\uFE0F 역할 변경 오류!");
                return;
            }

            if (count > 0) {
                System.out.println("역할 변경 완료");
            } else {
                System.out.println("\uD83D\uDD12 SUPER 관리자만 변경할 수 있습니다.");
            }

            /* === 이어서 할지 여부 === */
            while (true) {
                System.out.print("ℹ\uFE0F 이어서 하시겠습니까? (\uD83D\uDD39 0.No  \uD83D\uDD39 1.Yes) >> ");

                if (!scan.hasNextInt()) {
                    System.out.println("⚠\uFE0F 숫자만 입력하세요.");
                    scan.next();
                    continue;
                }

                continueChoice = scan.nextInt();
                if (continueChoice == 0 || continueChoice == 1) break;

                System.out.println("⚠\uFE0F 0 또는 1만 입력 가능합니다.");
            }

        } while (continueChoice == 1);
    }//역할 변경 메소드 종료

    //슈퍼관리자 확인 메소드
    private boolean isSuperAdmin() {

        String sql = "SELECT arole FROM admin WHERE aid = ?";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setString(1, session.getAdminId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "SUPER".equalsIgnoreCase(rs.getString("arole"));
                }
            }

        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 슈퍼 계정 찾기 오류!");
        }

        return false;
    }//슈퍼관리자 확인 메소드 종료

}




