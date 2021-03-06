package org.nl.controllers.manager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dizitart.no2.objects.Cursor;
import org.nl.Main;
import org.nl.model.Product;
import org.nl.services.ProductService;
import org.nl.services.StageService;

import java.io.IOException;
import java.net.URL;

import static org.nl.services.FileSystemService.imgFromPrd;

public class StoreCheckController {
    @FXML
    public ChoiceBox<String> sortBy;
    @FXML
    public CheckBox onlyStock;
    @FXML
    public TextField searchField;
    @FXML
    public AnchorPane parentPane;

    //private ArrayList<Pane> produseAfisate = new ArrayList<>();

    @FXML
    private AnchorPane pane;
    @FXML
    public void initialize() {
        //i.setImage(new Image("file:/E:/ceva.png"));
        //i.imageProperty().set(new Image("file:/E:/ceva.png"));
        loadAllProducts();
        sortBy.getItems().addAll("Sort by...", "Price ascending","Price descending");
        sortBy.setValue("Sort by...");
        sortBy.setOnAction(this::reloadProducts);
    }

    @FXML
    public void goBack(ActionEvent evt){
        StageService.loadPage(evt,"Menus/Manager.fxml");
    }

    private void loadAllProducts(){
        for(int chind=pane.getChildren().size();chind>0;chind--) {
            pane.getChildren().remove(0);
        }
        Cursor<Product> all = ProductService.getAllProducts();
        int i = 0;
        for(Product p : all){
            addProductOnScreen(p,i);
            i++;
        }
        pane.setPrefHeight(i*125);
    }

    private void addProductOnScreen(Product p, int i){
        try {
            URL toFxml = Main.class.getClassLoader().getResource("manager/itemStoreCheck.fxml");
            if(toFxml == null)
                throw new RuntimeException("Could not load fxml file itemStoreCheck.fxml");
            FXMLLoader newLoader = new FXMLLoader(toFxml);
            Pane newPane = newLoader.load();
            ((ItemStoreController)newLoader.getController()).setProductId(p.getIdProdct());
            ((ItemStoreController)newLoader.getController()).getWithdrawButton().setOnAction(
                    (evt)-> {
                        try {
                            StageService.createYesNoPopup(evt,"Delete product?","Are you sure you want to delete the product?",
                                    null,ProductService.class.getMethod("removeProduct", int.class, StoreCheckController.class),
                                    p.getIdProdct(),this);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
            );


            pane.getChildren().add(newPane);

            ((TextField)newPane.getChildren().get(5)).setText(p.getName());
            newPane.setLayoutY(i*125);
            //System.out.println(imgFromPrd(p));
            ((ImageView)newPane.getChildren().get(0)).setImage(new Image(imgFromPrd(p)));

            //((Text)newPane.getChildren().get(7)).setText(""+p.getIdProdct());


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void reloadProducts(ActionEvent actionEvent) {
        for(int chind=pane.getChildren().size();chind>0;chind--) {
            pane.getChildren().remove(0);
        }
        String sort = sortBy.getValue();

        Cursor<Product> all;
        if(sort.equals("Sort by..."))
            all = ProductService.getAllProducts();
        else
        if(sort.equals("Price ascending"))
            all = ProductService.getAllProducts(true);
        else
            all = ProductService.getAllProducts(false);


        int i = 0;

        for(Product p : all){
            if(ProductService.checkProductStockName(p,searchField,onlyStock.isSelected())) {
                    addProductOnScreen(p, i);
                    i++;
            }
        }
        pane.setPrefHeight(i*125);
    }

    @FXML
    public void openAddDialog(ActionEvent evt){
        try {
            URL toFxml = Main.class.getClassLoader().getResource("manager/addProduct.fxml");
            if (toFxml == null)
                throw new RuntimeException("Could not load addProduct.fxml");
            FXMLLoader newLoader = new FXMLLoader(toFxml);
            Pane root = newLoader.load();
            ((AddProductController)newLoader.getController()).setScc(this);

            final Stage infoPage = new Stage();
            infoPage.initModality(Modality.WINDOW_MODAL);
            infoPage.setResizable(false);
            infoPage.initOwner(((Node) evt.getSource()).getScene().getWindow());
            infoPage.getIcons().add(new Image("icon.png"));
            infoPage.setTitle("Add a new product");
            Scene scene = new Scene(root);
            infoPage.setScene(scene);
            //setProduct(root,infoPage);
            infoPage.show();
        }catch (IOException e){
            System.out.println("IO error");
        }
    }
}
