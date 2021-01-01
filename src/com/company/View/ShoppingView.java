package com.company.View;

import com.company.Controller.ShoppingController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ShoppingView extends JFrame {

    //private JPanel selectProductPanel, productListPanel, myShoppingPanel;
    public JLabel lblProduct, lblSearch, lblCount, lblItemList, lblMyList, lblMsg, lblCname, lblCphoneNum, lblstate;
    public JTextField jtfSearch, jtfCount;
    public JComboBox cb;
    public JTable tbItem, tbMyList;
    public JScrollPane jspItem, jspMyList;
    public JButton btnDelete, btnDelete2, btnPay, btnEnroll;
    public Font fnt, fntDelete;
    public DefaultTableModel modelItemList, modelMyList;

    public JTable productTable,productTable2;
    public DefaultTableModel tableModel,tableModel2;
    public String tableHeader[] = { "Code","Name","Price","Count","State"};
    public ShoppingView() {

        // 폰트 설정
        fnt = new Font("Dialog", Font.BOLD, 15);
        fntDelete = new Font("Dialog", Font.BOLD, 10);

        // -----------label, textfield-----------
        lblProduct = new JLabel("상품");
        lblSearch = new JLabel("코드");
        lblCount = new JLabel("수량");
        lblCname = new JLabel("고객이름 : 4조");
        lblCphoneNum = new JLabel("고객 번호 : 010-8890-2749");

        cb = new JComboBox(); // 상품을 콤보박스에서 조회할 수 있도록 생성
        jtfSearch = new JTextField();
        jtfCount = new JTextField();
        
        
        // -----------ItemList-----------
        lblItemList = new JLabel("구매 할 수 있는 물품 List");
        lblstate = new JLabel("상태 : ");

        //btn
        btnEnroll = new JButton("담기");


        // table 생성
        tableModel = new DefaultTableModel(tableHeader,0);
        productTable = new JTable(tableModel);

        jspItem = new JScrollPane(productTable);
        jspItem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jspItem.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        // -----------MyList-----------
        lblMyList = new JLabel("내가 담은 List(장바구니)");

        // table에 들어갈 rows, columns 정의
        String MyListcolumns[]={"Code", "Name", "Price", "Count", "State"};
        tableModel2 = new DefaultTableModel(MyListcolumns, 0);
        productTable2 = new JTable(tableModel2);


        jspMyList = new JScrollPane(productTable2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        btnDelete = new JButton("삭제");

        // -----------PayMsg-----------
        lblMsg = new JLabel("결제 금액 : 원");
        btnPay = new JButton("결제하기");

        drawView();
    }

    public void drawView() {

        setTitle("Shopping View");
        setSize(400, 550);
        setLayout(null);
        setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        // -----------label, textfield add-----------

        lblSearch.setFont(fnt);
        lblSearch.setBounds(10, 75, 60, 40);

        lblSearch.setVerticalTextPosition(SwingConstants.CENTER);
        add(lblSearch);

        lblCount.setFont(fnt);
        lblCount.setBounds(155, 75, 60, 40);
        add(lblCount);

        lblCname.setFont(fnt);
        lblCname.setBounds(20, 15, 120, 30);
        add(lblCname);

        lblCphoneNum.setFont(fnt);
        lblCphoneNum.setBounds(175, 15, 300, 30);
        add(lblCphoneNum);



        jtfSearch.setBounds(45, 75, 100, 40);
        add(jtfSearch);

        jtfCount.setBounds(190, 75, 100, 40);
        add(jtfCount);

        //ENROLL BTN
        btnEnroll.setBounds(300,75,60,40);
        btnEnroll.setFont(new Font("",Font.BOLD, 13));
        add(btnEnroll);

        lblItemList.setFont(fnt);
        lblItemList.setBounds(20, 145, 200, 30);
        add(lblItemList);

        lblstate.setFont(fnt);
        lblstate.setBounds(210,145,140,30);
        add(lblstate);

        // -----------ItemList add-----------
        jspItem.setBounds(10, 180, 360, 160);
        add(jspItem);

        // -----------MyList-----------
        lblMyList.setFont(fnt);
        lblMyList.setBounds(20, 345, 200, 30);
        add(lblMyList);

        jspMyList.setBounds(10, 380, 310, 70);
        add(jspMyList);

        btnDelete.setFont(fntDelete);
        btnDelete.setBounds(320, 390, 60, 40);
        add(btnDelete);


        // -----------PayMsg-----------
        lblMsg.setFont(fnt);
        lblMsg.setBounds(30, 450, 150, 60);
        add(lblMsg);

        btnPay.setBounds(285, 465, 90, 30);
        add(btnPay);

        //pack();
        setVisible(true);
    }

    public void addOrderActionListner(ActionListener listener) {
        btnPay.addActionListener(listener);
        // 다른 리스너들은 추가하기...
    } // addButtonActionListener()

    public void addEnrollButtonListener(ActionListener listener){
        btnEnroll.addActionListener(listener);
    }

    public void addPayMentButtonListneer(ActionListener listener){
        btnPay.addActionListener(listener);
    }

    public void addDeletButtonListener(ActionListener listener){
        btnDelete.addActionListener(listener);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ShoppingView shoppingView = new ShoppingView();
        shoppingView.drawView();
        new ShoppingController(shoppingView);
    }


}