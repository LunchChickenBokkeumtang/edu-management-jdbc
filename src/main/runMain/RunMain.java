package main.runMain;

import admin.AdminController;
import student.StudentController;
import java.sql.Connection;
import java.util.Scanner;

import common.Session;

import static common.DBUtil.*;

public class RunMain {
    private Session session;
    private Connection connect;
    private AdminController adminController;
    private StudentController studentController;

    public void run() {
        connect = getConnection();
        session = new Session();
        Scanner scan = new Scanner(System.in);
        adminController = new AdminController(session, connect);
        studentController = new StudentController(session, connect);
        boolean isFirst = true;

        try {
            while (true) {
                if (isFirst) {
                    System.out.println("🎈Edu_management_team2에 오신걸 환영합니다🎈");
                    isFirst = false;
                }

                System.out.println("\uD83D\uDD39 메뉴선택 : \uD83D\uDD39 1. 관리자\t\uD83D\uDD39 2. 학생\t\uD83D\uDD39 3. 시스템종료");
                String input = scan.next();

                if (!input.matches("\\d+")) {
                    System.out.println("⚠\uFE0F 숫자만 입력 가능합니다.");
                    continue;
                }

                int mainMenu = Integer.parseInt(input);

                switch (mainMenu) {
                    case 1 -> adminController.start(scan);
                    case 2 -> studentController.start(scan);
                    case 3 -> {
                        System.out.println("ℹ\uFE0F 시스템을 종료합니다.");
                        return;
                    }
                    default -> System.out.println("⚠\uFE0F 해당 번호는 존재하지 않습니다.");
                }
            }

        } catch (Exception e) {
            System.out.println("⚠\uFE0F 런메인오류!");
        } finally {
            closeConnect(connect);
            System.out.println("ℹ\uFE0F 연결종료");
        }
    }
}




