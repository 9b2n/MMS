package com.company.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class ProductDAO {
    String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    String jdbcUrl = "jdbc:mysql://mms.crgsa3qt3jqa.ap-northeast-2.rds.amazonaws.com/mms?user=jaewon&password=wlfkf132";
    public Connection conn;

    public PreparedStatement pstmt;
    public ResultSet rs;

    public ProductDTO product;
    public Vector<String> items = null;

    public String sql;

    //DB 연결 메서드
    public void  connectDB() throws ClassNotFoundException, SQLException {
        //1단계 : JDBC 드라이버 로드
        Class.forName(jdbcDriver);

        //2단계 : 데이터베이스 연결


        conn = DriverManager.getConnection(jdbcUrl, "jaewon", "wlfkf132");


    }



    public void registerUser(String uname, String email) throws SQLException {
        String sql = "Insert into event values(?, ?, ?, ?, ?, ?, ?)";

        //3단계 : Statement 생성
        pstmt = conn.prepareStatement(sql);

        //4단계 : SQL 문 전송
        pstmt.executeUpdate();
    }

    public void printList() throws SQLException {
        String sql = "select * from Product";

        pstmt = conn.prepareStatement(sql);
        //5단계 : 결과받기
        rs = pstmt.executeQuery();
    }

    public ArrayList<ProductDTO> getAll() throws SQLException, ClassNotFoundException {

        connectDB();
        sql= "select * from Product";

        //전체 검색 데이터를 전달하는 ArrayList
        ArrayList<ProductDTO> datas = new ArrayList<ProductDTO>();

        //관리번호 콤보박스 데이터를 위한 벡터 초기화
        items = new Vector<String>();
        items.add("전체");

        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while(rs.next()){
            ProductDTO p = new ProductDTO();
            p.setPrCode(rs.getInt("pr_code"));
            p.setPrName(rs.getString("pr_name"));
            p.setPrice(rs.getInt("price"));
            p.setLocation(rs.getString("location"));
            p.setExpDate(rs.getDate("exp_date"));
            p.setAmount(rs.getInt("amount"));
            p.setState(rs.getString("state"));

            datas.add(p);
            items.add(String.valueOf(rs.getInt("pr_code")));

        }
        closeDB();
        return datas;
    } // getAll 상품 전체 가져오기

    public ProductDTO getProduct(int prcode) throws SQLException, ClassNotFoundException {
        sql = "select * from Product where pr_code = ?";

        connectDB();

        ProductDTO p = null;
        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, prcode);
            rs=pstmt.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if(rs.next()) {
            p = new ProductDTO();
            p.setPrCode(rs.getInt("pr_code"));
            p.setPrName(rs.getString("pr_name"));
            p.setPrice(rs.getInt("price"));
            p.setLocation(rs.getString("location"));
            p.setExpDate(rs.getDate("exp_date"));
            p.setAmount(rs.getInt("amount"));
            p.setState(rs.getString("state"));
        }
        else{
            p.setPrCode(-1);
            p.setPrName(rs.getString("pr_name"));
            p.setPrice(rs.getInt("price"));
            p.setLocation(rs.getString("location"));
            p.setExpDate(rs.getDate("exp_date"));
            p.setAmount(rs.getInt("amount"));
            p.setState(rs.getString("state"));

        }
        closeDB();
        return p;
    } // getProduct 상품 하나 가져오기



    public void updateProduct(ProductDTO product) throws SQLException, ClassNotFoundException {
        ProductDTO p = product;
        String sql = "update Product set pr_name = ?, PRICE = ?, location = ?, exp_date = ?, amount = ?, state = ? where pr_code = ?";
        connectDB();

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, p.getPrName());
            pstmt.setInt(2, p.getPrice());
            pstmt.setString(3, p.getLocation());
            pstmt.setDate(4, (Date) p.getExpDate());
            pstmt.setInt(5, p.getAmount());
            pstmt.setString(6, p.getState());
            pstmt.setInt(7, p.getPrCode());


            if(pstmt.executeUpdate() == 0) {
                closeDB();
            } else {
                closeDB();
            }
        } catch (SQLException e) {
            closeDB();
            e.printStackTrace();
        }
    } //updateProduct 상품 업데이트하기


    public void closeDB() {
        try {
            pstmt.close();
            if(rs != null) rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
