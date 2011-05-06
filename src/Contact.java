import java.awt.*;

import java.applet.*;

import java.util.StringTokenizer;

import java.net.URL;



public class Contact extends Applet {

    static int num_recs=50;

    String email_address[] = new String[50];

    List itemList = new List(5,false);



    public void init(){

        System.out.println("Contact - init");

        designScreen();

        readParam();

        return;}



    public void start(){

        System.out.println("Contact - start");

        return;}



    public void stop(){

        System.out.println("Contact - stop");

        return;}



    public void destroy(){

        System.out.println("Contact - destroy");

        return;}



    final void designScreen(){

        System.out.println("Contact - designScreen");

        Button sendButton = new Button("Push This Button");

        Label listLabel   = new Label("Welcome to Harriet's First Applet");

        Panel listPanel   = new Panel();

        listPanel.setLayout(new BorderLayout());



        listLabel.setAlignment(Label.CENTER);

        listPanel.setBackground(Color.green);

        itemList.setBackground(Color.blue);

        setBackground(Color.white);



        listPanel.add("North",listLabel);

        listPanel.add("Center",itemList);

        listPanel.add("South",sendButton);

        add(listPanel);

        return;}



    final void readParam() { 

        System.out.println("Contact - readParam");

        String param_data=null; 

        String email_name=null;

        StringTokenizer st;

        int good_recs=-1;



        for (int j=0;j<num_recs;j++) { 

            param_data = getParameter("a" + j); 

            if (param_data != null){

                st = new StringTokenizer(param_data, "|"); 

                if (st.hasMoreTokens()) { 

                    email_name = st.nextToken();

                    if (st.hasMoreTokens()) {

                        good_recs = ++good_recs;

                        itemList.addItem(email_name);

                        email_address[good_recs] = st.nextToken();

                    }

                }

            }

        }

        return;}



    public boolean action(Event evt, Object obj){ 

        System.out.println("Contact - action");



        if (evt.target instanceof Button){

            int selected_index = itemList.getSelectedIndex(); 



            if (selected_index > -1)

                invokeTransfer(selected_index);

            else

                showStatus("Please select a name first, then press the Send Mail button.");

        }



        return true;}





    final void invokeTransfer(int selected_item){

        System.out.println("Contact - invokeTransfer");

        try {

            getAppletContext().showDocument(new URL("mailto:" + email_address[selected_item]));

            showStatus("Email successful for: " + email_address[selected_item]);

        }

        catch (Exception e){

            showStatus("Failed to connect to Email program for: " + email_address[selected_item]);

        }

        return;}

}

