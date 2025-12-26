package student.exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExamController {

    private Connection connect;

    public ExamController(Connection connect) {
        this.connect = connect;
    }

    //시험응시 메소드
    public void takeExam(Scanner scan, String siteId) {
        int thisYear = LocalDate.now().getYear();
        int thisMonth = LocalDate.now().getMonthValue();
        int semester=1;
        int termNo;

        if (thisMonth >= 3 && thisMonth <= 6) termNo = 1;
        else if (thisMonth >= 9 && thisMonth <= 12) termNo = 2;
        else {
            System.out.println("현재는 시험 기간이 아닙니다.");
            return;
        }

        //  학기시험번호 출력
        String listSql = "SELECT semester, exam_name FROM exam_info WHERE exam_year = ? AND termNo = ?";
        try (PreparedStatement ps = connect.prepareStatement(listSql)) {
            ps.setInt(1, thisYear);
            ps.setInt(2, termNo);
            /* 시험 과목 선택
            현재 바인드변수에 scan입력받지않고 고정값 설정해두어 자동으로 2025년 2학기 자바 초급 응시 선택됨

            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            System.out.println("시험 목록");
            while (rs.next()) {
                hasData = true;
                System.out.print( rs.getInt("semester") + ". " + rs.getString("exam_name")+"\t");
            }

            if (!hasData) {
                System.out.println("해당 학기의 시험이 없습니다.");
                return;
            }

            //  사용자 입력
            int semester;
            while (true) {
                System.out.print("\n응시할 시험 선택 >> ");
                String input = scan.next();
                if (!input.matches("\\d+")) {
                    System.out.println("숫자만 입력하세요.");
                    continue;
                }
                semester = Integer.parseInt(input);
                break;
            }*/

            String findSql = "SELECT exam_id, exam_name, exam_term FROM exam_info WHERE exam_year = ? AND termNo = ? AND semester = 1";
            int examId;
            String examName;
            String examTerm;

            try (PreparedStatement ps2 = connect.prepareStatement(findSql)) {
                ps2.setInt(1, thisYear);
                ps2.setInt(2, termNo);
                ps2.setInt(3, semester);
                ResultSet rs2 = ps2.executeQuery();

                if (!rs2.next()) {
                    System.out.println("존재하지 않는 학기시험번호입니다.");
                    return;
                }

                examId = rs2.getInt("exam_id");
                examName = rs2.getString("exam_name");
                examTerm = rs2.getString("exam_term");
            }

            // 응시 중복 여부 체크
            String checkSql = "SELECT COUNT(*) FROM exam_answer WHERE exam_id = ? AND site_id = ?";
            try (PreparedStatement ps3 = connect.prepareStatement(checkSql)) {
                ps3.setInt(1, examId);
                ps3.setString(2, siteId);
                ResultSet rs3 = ps3.executeQuery();
                rs3.next();
                System.out.println(thisYear + "년 " + examTerm + " " + examName + " 시험 시작!");
                if (rs3.getInt(1) > 0) {
                    System.out.println("이미 응시한 시험입니다.");
                    return;
                }
                startExamItems(scan, siteId, examId,semester);
            }

        } catch (SQLException e) {
            System.out.println("시험시작 오류!");
        }
    }//시험응시 메소드 종료

    //시험 시작 메소드
    private void startExamItems(Scanner scan, String siteId, int examId, int semester) {
        String sql =
                "SELECT item_no, item_q1, item_q2, item_q3, correct_no " +
                        "FROM exam_item " +
                        "WHERE exam_id = ? and semester = ? " +
                        "ORDER BY item_no";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            semester = 1;
            ps.setInt(1, examId);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int itemNo = rs.getInt("item_no");
                String q1 = rs.getString("item_q1");
                String q2 = rs.getString("item_q2");
                String q3 = rs.getString("item_q3");
                int correctNo = rs.getInt("correct_no");

                System.out.println();
                System.out.println(itemNo + ". " + q1);
                System.out.println("==============================");
                if (q2 != null && !q2.isBlank()) {
                    System.out.println(q2);
                }
                System.out.println("==============================");

                System.out.println(q3);
                System.out.print("ℹ\uFE0F 답안 입력(번호로 입력해주세요)>> ");
                String input = scan.next();

                if (!input.matches("\\d+")) {
                    System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                    continue;
                }

                int userAnswer = Integer.parseInt(input);

                boolean isCorrect = (userAnswer == correctNo);

                saveAnswer(siteId, examId, semester, itemNo, userAnswer, isCorrect);
            }

            System.out.println();

            while (true) {
                System.out.println();
                System.out.println("ℹ\uFE0F 선택 : 1.시험종료  2.답안수정");
                String choice = scan.next();

                if (!choice.matches("\\d+")) {
                    System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                    continue;
                }

                int menu = Integer.parseInt(choice);

                if (menu == 1) {
                    System.out.println("시험 응시가 완료되었습니다.");
                    return;
                } else if (menu == 2) {
                    modifyAnswer(scan, siteId, examId);
                } else {
                    System.out.println("⚠\uFE0F 잘못된 선택입니다.");
                }
            }
        } catch (SQLException e) {
            System.out.println("시험응시 오류!");
        }
    }

    //답안 저장 메소드
    private void saveAnswer( String siteId, int examId, int semester, int itemNo, int selectedAnswer, boolean isCorrect ) {
        String sql =
                "INSERT INTO exam_answer " +
                        "(site_id, exam_id, semester, item_no, selected_answer, is_correct) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, siteId);
            ps.setInt(2, examId);
            ps.setInt(3, semester);
            ps.setInt(4, itemNo);
            ps.setInt(5, selectedAnswer);
            ps.setBoolean(6, isCorrect);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("답안 저장 오류!");
        }
    }//답안 저장 메소드 종료

    //답안수정 메뉴 메소드
    private void modifyAnswer(Scanner scan, String siteId, int examId) {
        int semester=1;
        System.out.print("ℹ\uFE0F 수정할 문제 번호 입력 >> ");
        String input = scan.next();

        if (!input.matches("\\d+")) {
            System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
            return;
        }

        int itemNo = Integer.parseInt(input);

        String sql =
                "SELECT item_no, item_q1, item_q2, item_q3 " +
                        "FROM exam_item " +
                        "WHERE exam_id = ? AND item_no = ?";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ps.setInt(2, itemNo);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("⚠\uFE0F 해당 문제 번호가 존재하지 않습니다.");
                return;
            }

            System.out.println();
            System.out.println(itemNo + "번. " + rs.getString("item_q1"));
            System.out.println("==============================");
            if (rs.getString("item_q2") != null)
                System.out.println(rs.getString("item_q2"));
            System.out.println("==============================");
            System.out.println(rs.getString("item_q3"));
            System.out.println();

            System.out.print("ℹ\uFE0F 답안 입력(번호로 입력해주세요)>> ");
            int newAnswer = scan.nextInt();


            updateAnswer(siteId, examId,semester, itemNo, newAnswer);

            System.out.println("답안이 수정되었습니다.");

        } catch (SQLException e) {
            System.out.println("답안 수정 오류!");
        }
    }//답안수정 메뉴 메소드

    //답안수정 실행 메소드
    private void updateAnswer( String siteId, int examId, int semester, int itemNo, int newAnswer ) {
        //수정한 답안의 정답여부 확인
        String sqlItem = "SELECT correct_no FROM exam_item WHERE exam_id = ? AND item_no = ? and semester=?";
        int correctNo = 0;
        try (PreparedStatement psItem = connect.prepareStatement(sqlItem)) {
            psItem.setInt(1, examId);
            psItem.setInt(2, itemNo);
            psItem.setInt(3, semester);
            ResultSet rs = psItem.executeQuery();
            if (rs.next()) {
                correctNo = rs.getInt("correct_no");
            }
        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 답안수정 오류");
        }

        // is_correct 계산 (0 또는 1)
        int isCorrect = (newAnswer == correctNo) ? 1 : 0;
        String sql = "UPDATE exam_answer SET selected_answer = ?, is_correct = ? " +
                "WHERE site_id = ? AND exam_id = ? AND item_no = ?";

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setInt(1, newAnswer);
            ps.setInt(2, isCorrect);
            ps.setString(3, siteId);
            ps.setInt(4, examId);
            ps.setInt(5, itemNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("⚠\uFE0F 답안수정SQL오류");
        }
    }//답안수정 실행 메소드


    // 점수확인 메소드
    public void showScoreDetail(Scanner scan, String siteId) {
        System.out.println("ℹ\uFE0F 확인하고 싶은 년도와 학기를 입력하세요.");
        int year;
        int term;

        while (true) {
            System.out.print("ℹ\uFE0F 년도입력(예: 2025) >> ");
            String input = scan.next();
            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }
            year = Integer.parseInt(input);

            String sqlCheckYear =
                    "SELECT 1 FROM exam_info WHERE exam_year = ? LIMIT 1";
            try (PreparedStatement ps = connect.prepareStatement(sqlCheckYear)) {
                ps.setInt(1, year);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("⚠\uFE0F 해당 년도의 시험 기록이 없습니다.");
                        continue;
                    }
                }
            } catch (SQLException e) {
                System.out.println("⚠\uFE0F 년도 확인 중 오류 발생!");
                continue;
            }
            break;
        }

        while (true) {
            System.out.print("ℹ\uFE0F 학기 선택(숫자로 입력): \uD83D\uDD39 1. 1학기 \t\uD83D\uDD39 2. 2학기 >> ");
            String input = scan.next();

            if (!input.matches("\\d+")) {
                System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                continue;
            }
            term = Integer.parseInt(input);

            if (term != 1 && term != 2) {
                System.out.println("⚠️" + term + "학기는 존재하지 않습니다. 다시 입력해주세요.");
                continue;
            }
            break;
        }

        String sql =
                "SELECT ea.item_no, ea.is_correct " +
                        "FROM exam_answer ea " +
                        "JOIN exam_info ei ON ea.exam_id = ei.exam_id " +
                        "WHERE ea.site_id = ? " +
                        "AND ei.exam_year = ? " +
                        "AND ei.termNo = ? " +
                        "AND ei.semester = ? " +
                        "ORDER BY ea.item_no";

        int totalScore = 0;

        System.out.println();
        System.out.println("ℹ\uFE0F" + year + "년 " + term + "학기 성적확인");
        System.out.println("---------------------------------");

        List<Integer> itemNoList = new ArrayList<>();
        List<Integer> correctList = new ArrayList<>();

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, siteId);
            ps.setInt(2, year);
            ps.setInt(3, term);
            ps.setInt(4, 1);

            try (ResultSet rs = ps.executeQuery()) {

                boolean hasData = false;

                while (rs.next()) {
                    hasData = true;

                    int itemNo = rs.getInt("item_no");
                    int isCorrect = rs.getInt("is_correct");

                    itemNoList.add(itemNo);
                    correctList.add(isCorrect);

                    if (isCorrect == 1) {
                        totalScore += 20;
                    }
                }

                if (!hasData) {
                    System.out.println("⚠\uFE0F 응시 기록이 없습니다!\n\n");
                    return;
                }

                System.out.println("ℹ\uFE0F 총점 : " + totalScore + "점");

                for (int i = 0; i < itemNoList.size(); i++) {
                    System.out.println(
                            itemNoList.get(i) + "번. " +
                                    (correctList.get(i) == 1 ? "O" : "X")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("⚠\uFE0F 성적조회 오류 발생!");
        }
    }// 점수확인 메소드 종료
}