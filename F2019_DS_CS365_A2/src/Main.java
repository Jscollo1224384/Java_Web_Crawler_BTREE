/**
 * Author@ Joseph M. Scollo
 * Fall 2019 CSC 365 w/ Doug Lea
 * Assignment 2
 *
 * This is driver program of assignment 2. It interacts with a controller class that executes the loader and application programs.
 * The loader program compares all web sites and calculates their similarities use a weighted cosine vector class. A JavaML library is used to
 * calculate and store clusters of similarity results via K-medoids clustering algorithm. The loader also keeps a cache of website content by tracking
 * "last-modified" header tags up dating the content every time the site is updated. The program takes each words hashcode(hashcode formed by the xoring of three different
 * hashcodes) as an element and puts it into a persistent B-Tree.
 * The B-Tree was coded using the pseudo code from clrs book edition 3. The tree was modified to detect collisions, the tree stores elements in the form of a double 2D array.
 * slot x = the hashcode and slot y = frequency. Every time a duplicate of a hashcode is put into the tree slot y is updated. ex. double [][] keys. keys[0][0] = a key keys[0][1] = a value.
 * The application program takes a user selected website and compares it every website hardcoded into the program.
 * It returns the most similar website and cluster of sites to the user selected site.
 *
 */

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main extends Application {
    private DateTimeFormatter dtf;
    private LocalDateTime now;
    private Button button;
    private Button button2;
    private TextField field;
    private Text fr;
    private TextFlow textFlow,r1, r2;
    private Text[] t;
    private String userEntry;
    private String score;
    private Controller c;
    ScrollPane scrollPane;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        scrollPane = new ScrollPane();
        String name = "webpages.txt";
        String currentWebPage = "";
        userEntry = "";
        BufferedReader buf = new BufferedReader(new FileReader(name));
        String[] url = new String[20];
        int i = 0;
        t = new Text [2];
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        now = LocalDateTime.now();
        while((currentWebPage = buf.readLine()) != null){
           url[i] = currentWebPage;
           i++;
        }
        buf.close();
        r1 = new TextFlow();
        r2 = new TextFlow();
        textFlow = new TextFlow();
        Text tSim = new Text("Your selected web page is most similar to:");
        primaryStage.setTitle("Web Compare");
        button = new Button("Compare");
        button2 = new Button("Clear");
        Button button3 = new Button("Quit");
        Button button4 = new Button("Reduced Cluster Results");
        field = new TextField("http://www.wikipedia.org/wiki/");
        GridPane layout = new GridPane();
        scrollPane.setContent(layout);
        layout.setPadding(new Insets(20,20,20,20));
        layout.setVgap(5);
        layout.setHgap(5);

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e)
            {
                button.setDisable(true);
                button4.setDisable(false);
                try {

                    c = new Controller();
                    userEntry = field.getText();
                    c.run(userEntry);
                    score = c.mostSimilar();
                    String []clust;
                    clust = c.runClust();
                    String cst = "";
                    for (int j = 0; j < clust.length; j++) {
                        cst = cst + clust[j] + "\n";
                    }
                    System.out.println(cst);
                    Text temp = new Text("Cluster Match: ");
                    Text temp2 = new Text(cst);
                    t[0] = temp;
                    t[1] = temp2;
                    r1.getChildren().add(t[0]);
                    r2.getChildren().add(t[1]);
                    fr = new Text(score);
                    textFlow.getChildren().add(fr);
                    button2.setDisable(false);
                    System.out.println(dtf.format(now));

                } catch (Exception ex ) {
                   ex.printStackTrace();
                }
            }

        };
        layout.add(r1, 0, 0);
        layout.add(r2, 0, 1);

        layout.add(textFlow,0, 31);

        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                button.setDisable(false);
                button2.setDisable(true);
                field.clear();
                textFlow.getChildren().clear();
                r1.getChildren().clear();
                r2.getChildren().clear();
                field = new TextField("http://www.wikipedia.org/wiki/");
            }
        };
        EventHandler<ActionEvent> event4 = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                button4.setDisable(true);
                r2.getChildren().clear();
                String[] clust = c.getSiteCluster();
                String cst = "";
                for (int j = 0; j < clust.length; j++) {
                    cst = cst + clust[j] + "\n";
                }
                Text temp2 = new Text(cst);
                t[1] = temp2;
                r2.getChildren().add(t[1]);
            }
        };
        EventHandler<ActionEvent> event3 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                Platform.exit();
            }
        };
        button.setOnAction(event);
        button2.setOnAction(event2);
        button2.setDisable(true);
        button4.setDisable(true);
        button3.setOnAction(event3);
        button4.setOnAction(event4);
        layout.add(field, 0, 25);
        layout.add(button, 0, 26);
        layout.add(button2, 0, 27);
        layout.add(button4, 0, 28);
        layout.add(button3, 0, 29);
        layout.add(tSim,0, 30);
        Scene scene = new Scene(scrollPane, 750, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

