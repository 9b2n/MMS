package com.company.Model;

import com.company.Controller.ProgramManager;

import java.sql.*;
import java.util.AbstractCollection;
import java.util.ArrayList;

public class AccountDAO {
    String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://mms.crgsa3qt3jqa.ap-northeast-2.rds.amazonaws.com/mms?user=jaewon&password=wlfkf132";

    private AccountDTO account;
    private String sql;

    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;
    ArrayList<AccountDTO> accountList;

    public AccountDAO(){
        accountList = getAll();
    }

    public void connectDB() { // DB에 접속
        try {
            Class.forName(jdbcDriver);

            conn = DriverManager.getConnection(jdbcUrl, "jaewon", "wlfkf132");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void closeDB() { // DB 접속 종료
        try {
            pstmt.close();
            if(rs != null) rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<AccountDTO> getAll(){ // DB에 등록되어있는 모든 계정 받아오기
        sql = "select * from Accounts";
        connectDB();
        ArrayList<AccountDTO> accountList = new ArrayList<>();
        try{
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){

                AccountDTO dto = new AccountDTO();
                dto.setId(rs.getString("id"));
                dto.setPassword(rs.getString("passwd"));
                dto.setIsSupperUser(rs.getBoolean("is_superuser"));
                dto.setIsStaff(rs.getBoolean("is_Staff"));
                dto.setUserName(rs.getString("user_name"));
                dto.setIsLogin(rs.getBoolean("is_login"));
                accountList.add(dto);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        closeDB();
        return accountList;
    }
    public boolean setLogin(String id, String PW){ // id와 pw에 일치하는 계정의 로그인 상태를 true로 만들기
        int check=0;
        sql = "update Accounts set is_login = ? where id = ? and passwd = ?";
        connectDB();
        try {

            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1,true);
            pstmt.setString(2,id);
            pstmt.setString(3,PW);
            check = pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        closeDB();
        if(check != 0) return true;
        return false;
    }
    public boolean setLogout(String id, String PW){ // id와 pw에 일치하는 계정의 로그인 상태를 false로 만들기
        int check=0;
        sql = "update Accounts set is_login = ? where id = ? and passwd = ?";
        connectDB();
        try {

            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1,false);
            pstmt.setString(2,id);
            pstmt.setString(3,PW);
            check = pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        closeDB();
        if(check != 0) return true;
        return false;
    }

}
