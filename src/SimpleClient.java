import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject; // you'll need to download and add json-simple-1.1.1.jar
                                   // https://code.google.com/archive/p/json-simple/downloads

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class SimpleClient {

    private static String name = "John Burris"; //Use string from XML
    private static String id = "014001";
    private static String dob = "1/1/1111";
    private static String provider = "Admiral Ackbar";

    private static int currentPatient = 0;

public static void main(String[] args) {
      
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;
      try 
      {
         builder = builderFactory.newDocumentBuilder();
      } catch (ParserConfigurationException e) 
      {
         e.printStackTrace();  
      }
      
      Document document = null;
      String xmlDocument = "http://www2.southeastern.edu/Academics/Faculty/jburris/emr.xml";
      try {
         document = builder.parse(new URL(xmlDocument).openStream());
      } catch (SAXException e) 
      {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      //Creates a list of nodes for the root element, emr
      NodeList emrRoot = document.getElementsByTagName("emr");
      //Gets the first node in the list... really just our root node
      Node emrNode = (Node) emrRoot.item(0);
      getPatientInfo(emrRoot);

   //Below is the Dialog box code that you need to alter significantly.
   int selection = 0;
   while( selection != 1 )
   {
      JTextField nameField = new JTextField(name);
      JTextField idField = new JTextField(id);
      JPanel mainPanel = new JPanel(new GridLayout(0, 1));
      mainPanel.add(new JLabel("Name: "));
      mainPanel.add(nameField);
      mainPanel.add(new JLabel("ID: "));
      mainPanel.add(idField);
      String[] buttons = new String[] {"View Patient", "Exit", "Next", "Previous"};
      selection = JOptionPane.showOptionDialog(mainPanel, mainPanel, "Patient Manager", 
      JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons , buttons [0]);
      
      if( selection == 0 ){
         //This code displays the name, ID, DOB and Provider information. You do not need
         //To alter between the two "*****" lines. Just make sure the Strings name, dob, provider
         // and id have the correct values.
         //****************************************** 
         
          String[] items = {"Update"};
          JComboBox combo = new JComboBox(items);
          JTextField field1 = new JTextField(name);
          JTextField field2 = new JTextField(id);
          JTextField field3 = new JTextField(dob);
          JTextField field4 = new JTextField(provider);
          JPanel panel = new JPanel(new GridLayout(0, 1));
          panel.add(combo);
          panel.add(new JLabel("Name: "));
          panel.add(field1);
          panel.add(new JLabel("ID: "));
          panel.add(field2);
          panel.add(new JLabel("DOB: "));
          panel.add(field3);
          panel.add(new JLabel("Provider :"));
          panel.add(field4);
          int result = JOptionPane.showConfirmDialog(null, panel, "Test",
              JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
          if (result == JOptionPane.OK_OPTION) {
              System.out.println(combo.getSelectedItem()
                  + " " + field1.getText()
                  + " " + field2.getText()
                  + " " + field3.getText()
                  + " " + field4.getText());
              
              //TODO: Create a JSON object containing all of the text fields
              //      and send to your server.
              
              JSONObject obj = new JSONObject();

              obj.put("name", "john");
              obj.put(name,field1);
              obj.put(id, field2);
              obj.put(dob, field3);
              obj.put(provider, field4);
              
              String output = obj.toString();
              System.out.println(output);
           
              System.out.println(obj);
              
              // Something like: "http://localhost:8080/update?data="+obj.toString()
              // HINT : http://localhost:8080/update?data={"name":"john"}
              // would look like http://localhost:8080/update?data=%7B%22name%22%3A%22john%22%7D
              
              
          } else {
              System.out.println("Cancelled");
          }
         //****************************************** 
      }
      else if( selection == 1 ){
         // Exit the application
         System.out.println("User hit exit.");
      }
      else if( selection == 2 ){
          try{
              ++currentPatient;
              getPatientInfo(emrRoot);
          }catch(Exception e){
              currentPatient--;
              JOptionPane.showMessageDialog(null, "Reached end of file.", "XML Error", JOptionPane.ERROR_MESSAGE);
          }
         // the next patient in the XML document (if there is a next patient).
         System.out.println("User went to next patient.");
      }
      else if( selection == 3 ){
          try{
              --currentPatient;
              getPatientInfo(emrRoot);
          }catch(Exception e){
              currentPatient++;
              JOptionPane.showMessageDialog(null, "There is no previous record", "Error", JOptionPane.ERROR_MESSAGE);
          }
         // the previous patient in the XML document (if there is a previous patient).
         System.out.println("User went to previous patient.");
      }
      
   }//while
     
}//main method

    private static void getPatientInfo(NodeList emrRoot){
        Node emrNode = emrRoot.item(0);
        if(emrNode instanceof Element){
            Element emrElement = (Element)emrNode;
            //Gets the list of all patient_info nodes
            NodeList patInfoList = emrElement.getElementsByTagName("patient_info");

            //Gets the first patient_info node... the first in the list of patients
            Node patInfoNode = (Node) patInfoList.item(currentPatient);
            //Fill in the values of our strings. The first is tricky because
            //it is an attribute and not the content of the element.
            id = patInfoNode.getAttributes().getNamedItem("id").getTextContent();
            if(patInfoNode instanceof Element) {
                //Get the element information for parient_info
                Element pat_info = (Element)patInfoNode;
                //Get the name...
                Node n = pat_info.getElementsByTagName("name").item(0);
                name = n.getTextContent();
                //Get the dob...
                n= pat_info.getElementsByTagName("dob").item(0);
                dob = n.getTextContent();
                //Get the provider name...
                n= pat_info.getElementsByTagName("provider").item(0);
                provider = n.getTextContent();
            }
        }
    }

}//class