package org.nl.controllers.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.dizitart.no2.objects.Cursor;
import org.nl.Main;
import org.nl.model.Order;
import org.nl.model.Product;
import org.nl.services.*;

import java.net.URL;
import java.text.SimpleDateFormat;

import static org.nl.controllers.RegistrationController.loggeduser;
import static org.nl.services.FileSystemService.imgFromPrd;


public class OrderHistoryController {
    @FXML
    private AnchorPane pane;
    @FXML
    public TextField searchField;
    @FXML
    public void initialize() {
        reloadOrders();
    }

    @FXML
    public void reloadOrders() {
        for(int chind=pane.getChildren().size();chind>0;chind--) {
            pane.getChildren().remove(0);
        }

        Cursor<Order> all = OrderService.getAllOrders();

        int i = 0;
        for(Order o : all){
            Product p = ProductService.getProduct(o.getIdProduct());
            if(OrderService.checkOrderName(p.getName(),searchField.getText()) && o.getUsername().equals(loggeduser.getUsername())) {
                addOrderOnScreen(o, i);
                i++;
            }
        }
        pane.setPrefHeight(i*125);
    }


    private void addOrderOnScreen(Order o, int i){
        try {
            URL toFxml = Main.class.getClassLoader().getResource("client/orderList.fxml");
            if(toFxml == null)
                throw new RuntimeException("Could not load fxml file item.fxml");
            FXMLLoader loader = new FXMLLoader(toFxml);
            Pane newPane = loader.load();
            ((ClientOrderItem)loader.getController()).setOrderDate(o.getDate());

            pane.getChildren().add(newPane);
            Product p = ProductService.getProduct(o.getIdProduct());

            ((Text)newPane.getChildren().get(1)).setText(p.getName());
            ((Text)newPane.getChildren().get(2)).setText("Status: " + o.getStatus());
            ((Text)newPane.getChildren().get(3)).setText(String.format("Price: $%.2f",p.getPrice()));
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            ((Text)newPane.getChildren().get(5)).setText("Order date: " + formatter.format(o.getDate()));

            newPane.setLayoutY(i*125);

            ((ImageView)newPane.getChildren().get(0)).setImage(new Image(imgFromPrd(p)));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    @FXML
    public void goBack(ActionEvent evt){
        StageService.loadPage(evt,"Menus/"+loggeduser.getRole()+".fxml");

    }

    public void removeOrphaned(ActionEvent evt) {
        OrderService.removeOrphanedForUser(loggeduser,this);
    }
}


