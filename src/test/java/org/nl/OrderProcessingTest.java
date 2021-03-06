package org.nl;



import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nl.controllers.RegistrationController;
import org.nl.model.User;
import org.nl.services.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Date;
import java.util.Objects;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class OrderProcessingTest {

    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";
    public static final String[] ROLES = new String[]{"Client", "Worker", "Courier", "Manager"};

    private void onlyInit() throws Exception{
        FileSystemService.setAppFolder("database-test");
        FileSystemService.initDirectory();
        FileUtils.cleanDirectory(FileSystemService.getApplicationHomeFolder().toFile());
        Main.initDatabases();
    }

    @BeforeEach
    void setUp() throws Exception {
        onlyInit();
        UserService.addUser(USERNAME, PASSWORD, ROLES[1], "Timisoara");
        ProductService.addProduct(0, "Chair", (float) 24.99, "H: 50 cm", "description", 5, "");
        Date d1 = new Date();
        OrderService.addOrder(USERNAME, 0, d1, "Timisoara");
        ProductService.addProduct(1, "Sofa", (float) 37.99, "H: 100 cm", "description", 10, "");
        Date d2 = new Date();
        OrderService.addOrder(USERNAME, 1, d2, "Timisoara");

    }

    @AfterEach
    void tearDown(){
        Main.closeDatabases();
    }

    @Start
    void start(Stage primaryStage) throws Exception {
        onlyInit();
        RegistrationController.loggeduser = new User(USERNAME, UserService.encodePassword(USERNAME, PASSWORD), ROLES[1], "");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("worker/Worker.fxml")));
        primaryStage.setTitle("Nature Leaf Testing");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
        StageService.setMainStage(primaryStage);
        tearDown();
        //new Main().start(primaryStage);
    }

    @Test
    void testBackButton(FxRobot robot) {
        robot.clickOn("#searchField");
        robot.push(KeyCode.ENTER);
        robot.clickOn("Back to menu");
        robot.clickOn("Order Processing");
    }

    @Test
    void testSearch(FxRobot robot) {
        robot.clickOn("#searchField");
        robot.push(KeyCode.ENTER);
        robot.clickOn("#searchField");
        robot.write("Chair");
        robot.push(KeyCode.ENTER);
        Pane p = robot.lookup("#pane").query();
        Assertions.assertThat(p.getChildren().size()).isEqualTo(1);
    }

    @Test
    void testProcessOrder(FxRobot robot) {
        robot.clickOn("#searchField");
        robot.push(KeyCode.ENTER);
        robot.clickOn("Process order");
        assertThat(OrderService.getAllPlacedOrders().toList()).size().isEqualTo(2);
        robot.clickOn("No");
        assertThat(OrderService.getAllPlacedOrders().toList()).size().isEqualTo(2);
        robot.clickOn("Process order");
        assertThat(OrderService.getAllPlacedOrders().toList()).size().isEqualTo(2);
        robot.clickOn("Yes");
        assertThat(OrderService.getAllPlacedOrders().toList()).size().isEqualTo(1);
    }

}