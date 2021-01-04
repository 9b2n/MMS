package com.company.Controller;

import com.company.Model.*;
import com.company.View.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.company.View.*;
import com.google.gson.Gson;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class mmsListener {
    private Gson gson = new Gson();
    private Message msg;
    private Socket socket;
    private BufferedReader inMsg = null;
    private PrintWriter outMsg = null;
    static final int INSERT_ACCOUNT = 1, LOGIN = 2, LOGOUT = 3, CHAT_MESSAGE = 4;

    private static mmsListener s_Instance;
    public static mmsListener getInstance(){
        if (s_Instance == null) s_Instance = new mmsListener();
        return s_Instance;
    }

    public void loginPanelListener(LoginViewPanel panel){
        panel.loginButton.addActionListener(e -> {
            String id = panel.txtId.getText();
            String pw = panel.txtPw.getText();
//            AccountDAO dao = new AccountDAO();

            String msg = "select * from Accounts where id = "
                    + "'" + id + "'" + "and passwd = " + "'" + pw + "'";

            ProgramManager.getInstance().getMainController().msgSend(new Message(id, pw, msg, LOGIN));
        });
        panel.joinButton.addActionListener(e -> {
            ViewManager.getInstance().joinViewOpen();
            joinViewListener(ViewManager.getInstance().getJoinView());
        });
    }
    public void joinViewListener(JoinView frame){
        frame.joinButton.addActionListener(e -> {
            String id = frame.txtId.getText();
            String pw = frame.txtPw.getText();
            String name = frame.txtName.getText();
            String msg= "insert into Accounts(id, passwd, user_name, is_login) values('"
                    + id + "', '" + pw + "', '" + name + "', " + 0 + ")";
            if(id != null && pw != null && name != null) {
                ProgramManager.getInstance().getMainController().msgSend(new Message(id, pw, msg, INSERT_ACCOUNT));
                frame.dispose();

            }
            else {

                JOptionPane.showMessageDialog(frame, "잘못된 양식입니다.");

            }

        });

    }
    public void mainViewPanelListener(MainViewPanel panel){ // **********

        MainView mainView = ViewManager.getInstance().getMainView();
        panel.productButton.addActionListener(e -> {
            if(ProgramManager.getInstance().getState() instanceof OrderManageState){
                mainView.orderListViewPanel.setVisible(false);
                ProgramManager.getInstance().setMainState();
            }
            else if(ProgramManager.getInstance().getState() instanceof CustomerManageState){
                mainView.customerViewPanel.setVisible(false);
                ProgramManager.getInstance().setMainState();
            }
        });
        panel.orderListButton.addActionListener(e-> {
            if(ProgramManager.getInstance().getState() instanceof MainState) {
                mainView.productViewPanel.setVisible(false);
                ProgramManager.getInstance().setOrderManageState();
            }
            else if(ProgramManager.getInstance().getState() instanceof CustomerManageState){
                mainView.customerViewPanel.setVisible(false);
                ProgramManager.getInstance().setOrderManageState();
            }
        });
        panel.customerButton.addActionListener(e->{
            if(ProgramManager.getInstance().getState() instanceof MainState) {
                mainView.productViewPanel.setVisible(false);
                ProgramManager.getInstance().setCustomerManageState();
            }
            else if(ProgramManager.getInstance().getState() instanceof OrderManageState){
                mainView.orderListViewPanel.setVisible(false);
                ProgramManager.getInstance().setCustomerManageState();
            }
        });
        panel.shoppingButton.addActionListener(e -> {
            ShoppingView shoppingView = ViewManager.getInstance().getShoppingView();
            shoppingView.setVisible(true);
            if(!ViewManager.getInstance().isShoppingViewOpen())
                shoppingViewListener(shoppingView);
        });
        panel.logoutButton.addActionListener(e -> {
            msg = new Message(ProgramManager.getInstance().id, ProgramManager.getInstance().pw,  "로그아웃", LOGOUT);
            ProgramManager.getInstance().getMainController().msgSend(msg);
            if(ProgramManager.getInstance().getState() instanceof MainState) {
                mainView.productViewPanel.setVisible(false);
            } else if(ProgramManager.getInstance().getState() instanceof CustomerManageState) {
                mainView.customerViewPanel.setVisible(false);
            } else if(ProgramManager.getInstance().getState() instanceof OrderManageState){
                mainView.orderListViewPanel.setVisible(false);
            }
            panel.setVisible(false);
            ProgramManager.getInstance().setLoginState();
        });
        panel.chatButton.addActionListener(e -> {
            ViewManager.getInstance().getChattingView().setVisible(true);
        });
    }
    public void productViewPanelListener(ProductViewPanel panel){
        ProductDAO dao = new ProductDAO();
        ArrayList<ProductDTO> datas = new ArrayList<ProductDTO>();

        panel.addButton.addActionListener(e -> {
            addProduct();
        }); //상품등록 버튼 리스너
        panel.searchButton.addActionListener(e -> {
            searchProduct(dao, panel.editMode, panel);
            panel.editMode = true;
        }); //검색 버튼 리스너
        panel.deleteButton.addActionListener(e -> {
            deleteProduct(dao,panel.editMode,panel, datas);
        }); //삭제버튼 리스너
        panel.updateButton.addActionListener(e -> {
            try {
                updateProduct(datas, dao, ProgramManager.getInstance().getPC().bufferedString);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }); // 수정버튼 리스너

        panel.productTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ProgramManager.getInstance().getPC().isClick =true;
                    ProgramManager.getInstance().getPC().appMain();  //클릭한 상품의 Pr코드 넣기
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
        }); // 수정버튼에 사용할 마우스 리스너
    } //ProductViewPaneListener

    //ProductViewPanelListener Method //메소드/////////////////
    ///여기
    public void deleteProduct(ProductDAO dao, boolean editMode, ProductViewPanel panel, ArrayList<ProductDTO> datas){
        String add_msg="";
        int row = ViewManager.getInstance().getMainView().productViewPanel.productTable.getSelectedRow();
        if(row == -1 ){
            JOptionPane.showMessageDialog(ViewManager.getInstance().getMainView().productViewPanel, " 삭제할 정보를 조회 후 선택해 주세요.");

        }else {
            int prCode = Integer.parseInt(ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 0).toString()); //삭제하고 곳
            add_msg = add_msg + "delete from Product where pr_code = " + prCode; //삭제 쿼리문
        }

        ProgramManager.getInstance().getMainController().msgSend(new Message("", "", add_msg, 7)); //쿼리문 메세지 보내기기
        panel.SUDLab.setText("검색 정보 :");
    }//테이블 누르고 삭제
    //여기


    public void searchProduct(ProductDAO dao, boolean editMode,ProductViewPanel panel){
        try {

            ProductDTO p = dao.getProduct(Integer.parseInt(panel.txtSearch.getText()));
            if (p.getPrCode() != -1) {
                panel.SUDtxt.setText("");
                panel.SUDtxt.append("코드\t이름\t가격\t위치\t유통기한\t재고\t상태\n" +
                        Integer.toString(p.getPrCode()) + "\t" + p.getPrName() + "\t" + p.getPrice() + "\t" + p.getLocation() + "\t" + p.getExpDate() + "\t" + p.getAmount() + "\t"
                        + p.getState());

                editMode = true; //찾았으면 수정,삭제가능
            } else {

                panel.SUDtxt.setText("검색하는 코드에 대한 정보가 없음");
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        panel.SUDLab.setText("검색 정보 :                                                         EditMode : " + editMode);
    }//search한후 텍스트area에 정보띄우기

    public void addProduct(){
        ViewManager.getInstance().getProductCRUDView().drawView();
        ViewManager.getInstance().getProductCRUDView().chk = 1;
    } //CRUD창 추가후 버튼리스너 추가

    public void refreshData (ArrayList<ProductDTO> datas, ProductDAO dao, ProductViewPanel panel)throws SQLException, ClassNotFoundException {

        datas = dao.getAll();
        Object record[] = new Object[7];
        panel.tableModel.setNumRows(0); // 다시붙일때 테이블 로우 초기화

        System.out.println("여기냐?");
        if( datas != null){

            for(ProductDTO p : datas) {
                record[0] = p.getPrCode();
                record[1] = p.getPrName();
                record[2] = p.getPrice();
                record[3] = p.getLocation();
                record[4] = p.getExpDate();
                record[5] = p.getAmount();
                record[6] = p.getState();
                panel.tableModel.addRow(record);

            }

        } // 테이블 초기화후 상품 재등록하기
    }//refreshData
    public void updateProduct(ArrayList<ProductDTO> datas, ProductDAO dao, int bufferedString ) {

        int row = ViewManager.getInstance().getMainView().productViewPanel.productTable.getSelectedRow();
        if(row == -1 ) {
            JOptionPane.showMessageDialog(ViewManager.getInstance().getMainView().productViewPanel, "수정할 정보를 선택해 주세요.");
        } else {
            int prcode = Integer.parseInt((ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 0).toString()));
            String prName = (String)ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 1);
            int price = Integer.parseInt(ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 2).toString());
            String location = (String)ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 3);
            Date date = Date.valueOf(ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 4).toString());
            int amount = Integer.parseInt(ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 5).toString());
            String state = (String)ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 6);


            String prstate = (String)ViewManager.getInstance().getMainView().productViewPanel.tableModel.getValueAt(row, 6);;

            DefaultTableModel dt = ViewManager.getInstance().getMainView().productViewPanel.tableModel;

            String add_msg = "update Product set pr_name = " + "'" + prName + "'" +
                    ",  PRICE = "+ price + ", location = "+ "'" + location +"'" +
                    ", exp_date = "+ "'" + date + "'" + ", amount = "+ amount + ", state = "+ "'" + prstate + "'" + "where pr_code = " + prcode;
            //update하기 위한 쿼리문



            ProgramManager.getInstance().getMainController().msgSend(new Message("", "", add_msg, 6)); //메세지 보내기

            ViewManager.getInstance().getMainView().productViewPanel.SUDtxt.setText("수정이 완료되었습니다.");
        }
    }




    public void orderListViewPanelListener(OrderListViewPanel panel){

        panel.btnSerach.addActionListener(e -> {
            ProgramManager.getInstance().getOrderController().searchOrder(panel); // 주문 목록 조회
        });

    }
    public void customerViewPanelListener(CustomerViewPanel panel){

        panel.addButton.addActionListener(e -> {
            CustomerManageView cmv = ProgramManager.getInstance().getCC().makeCustomerManageView();
            customerManageViewListener(cmv);
        });
        panel.searchButton.addActionListener(e -> {
            ProgramManager.getInstance().getCC().search = true;
            ProgramManager.getInstance().getCC().appMain();
        });
        panel.updateButton.addActionListener(e -> {
            ProgramManager.getInstance().getCC().update = true;
            ProgramManager.getInstance().getCC().appMain();
        });
        panel.deleteButton.addActionListener(e -> {
            ProgramManager.getInstance().getCC().delete =true;
            ProgramManager.getInstance().getCC().appMain();
        });

        panel.tblCustomerList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ProgramManager.getInstance().getCC().isClick =true;
                ProgramManager.getInstance().getCC().appMain();
            }
            public void mousePressed(MouseEvent e) { }
            public void mouseReleased(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
        });
    }

    public void productCRUDViewListener(ProductCRUDView frame){
        ProductDAO dao=new ProductDAO();
        ArrayList<ProductDTO> datas = new ArrayList<ProductDTO>();

        frame.completeButton.addActionListener(e -> {
            if(frame.chk==1)
                try {
                    addProduct_inCRUD(frame, dao, ViewManager.getInstance().getMainView().productViewPanel.editMode, datas); //상품등록하기
                }catch (Exception e1){}
        });
    }//productCRUDViewListener

    //productCRUDViewListener Method CRUD패널 메소드 ////////////////////
    public void addProduct_inCRUD(ProductCRUDView CRUDv, ProductDAO dao, boolean editMode, ArrayList<ProductDTO> datas) throws SQLException, ClassNotFoundException {

        String add_msg="insert into Product(pr_code, pr_name, price, location, exp_date, amount, state) ";
        String prstate = "판매";


        add_msg += "values(" + Integer.parseInt(CRUDv.codeText.getText()) +
                ", " + "'"+ CRUDv.nameText.getText() +"'"+
                ", " + Integer.parseInt(CRUDv.priceText.getText()) +
                ", " + "'" + CRUDv.locationText.getText() +"'"+
                ", " + "'" + CRUDv.expDateText.getText() + "'" +
                ", " + Integer.parseInt(CRUDv.countText.getText()) +
                ", " + "'"+ prstate + "'"+")"; // 상품 등록을 위한 쿼리문


        System.out.println("상품등록 완료");
        CRUDv.codeText.setText("");
        CRUDv.nameText.setText("");
        CRUDv.priceText.setText("");
        CRUDv.locationText.setText("");
        CRUDv.expDateText.setText("");
        CRUDv.countText.setText(""); // 상품 등록후 공간 초기화 하기




        ProgramManager.getInstance().getMainController().msgSend(new Message("", "", add_msg, 5)); // 쿼리문 메세지 보내기


        ViewManager.getInstance().getMainView().productViewPanel.SUDLab.setText("검색 정보 :                                                      ");

    } //addProduct_inCRUD complete누르면 정보 추가
    //메소드///////////////////////////////



    public void shoppingViewListener(ShoppingView frame){

        /*
        for (ActionListener al : frame.btnEnter.getActionListeners()) {
            frame.btnEnter.removeActionListener(al);
        }
        for (ActionListener al : frame.btnEnroll.getActionListeners()) {
            frame.btnEnter.removeActionListener(al);
        }
        for (ActionListener al : frame.btnPay.getActionListeners()) {
            frame.btnEnter.removeActionListener(al);
        }
        for (ActionListener al : frame.btnDelete.getActionListeners()) {
            frame.btnEnter.removeActionListener(al);
        }*/

        frame.btnEnter.addActionListener(e -> {

            String name = frame.txtName.getText();
            String phone = frame.txtPhone.getText();

            if(name != null && phone != null) {
                try {
                    ProgramManager.getInstance().getShoppingController().refreshData(frame);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e2) {
                    e2.printStackTrace();
                }

                frame.lblCname.setText("고객이름 : " + name);
                frame.lblCphoneNum.setText("고객 번호 : " + phone);

                frame.pn1.setVisible(false);
                frame.pn2.setVisible(true);
            }
            else System.out.println("이름과 번호를 모두 입력하세요!");

        });

        frame.btnEnroll.addActionListener(e -> {
            try {
                ProgramManager.getInstance().getShoppingController().addMyList(frame);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }
        });

        frame.btnPay.addActionListener(e -> {

            try {
                ProgramManager.getInstance().getShoppingController().payment(frame); // 여기를 통과해야 되게끔..
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }
        });

        frame.btnDelete.addActionListener(e -> {
            try {
                ProgramManager.getInstance().getShoppingController().deleteMy(frame);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }

        });

    }

    public void customerManageViewListener(CustomerManageView frame){

        frame.btnRegister.addActionListener(e -> {
            ViewManager.getInstance().setCustomerManageView(frame);
            ProgramManager.getInstance().getCC().register =true;
            ProgramManager.getInstance().getCC().appMain();
        });
        frame.btnExit.addActionListener(e -> {
            frame.dispose();
        });
    }

    public void chattingViewListener(ChattingView frame) {
        frame.exitButton.addActionListener(e-> {
            ProgramManager.getInstance().getChattingController().exitChatting();
        });
        frame.msgInput.addActionListener((e -> {
            ProgramManager.getInstance().getChattingController().sendTextMessage(ProgramManager.getInstance().id + "/" + frame.msgInput.getText());
            ViewManager.getInstance().getChattingView().msgInput.setText("");
        }));
    }
}
