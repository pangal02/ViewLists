package gr.uop;

import java.util.LinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        TextField filterTextField = new TextField();
        filterTextField.setPromptText("Type to filter");
        
        ListView<String> leftList = new ListView<>();
        leftList.setMaxHeight(Double.MAX_VALUE);
        leftList.setPrefWidth(200);

        LinkedList<String> allItems = new LinkedList<>();
        for(int i = 1; i < 21; i++){
            allItems.add("Item " + i);
            leftList.getItems().add("Item " + i);
        }

        ImageView rightImageView = new ImageView(new Image(getClass().getResourceAsStream("images/right.png")));
        Button rightButton = new Button("", rightImageView);
        rightButton.setMaxWidth(Double.MAX_VALUE);

        ImageView leftImageView = new ImageView(new Image(getClass().getResourceAsStream("images/left.png")));
        Button leftButton = new Button("", leftImageView);
        rightButton.setMaxWidth(Double.MAX_VALUE);
        
        ListView<String> rightList = new ListView<>();
        rightList.setMaxHeight(Double.MAX_VALUE);
        rightList.setPrefWidth(leftList.getPrefWidth());

        ImageView upImageView = new ImageView(new Image(getClass().getResourceAsStream("images/up.png")));
        Button upButton = new Button("", upImageView);
        upButton.setMaxWidth(Double.MAX_VALUE);

        ImageView downImageView = new ImageView(new Image(getClass().getResourceAsStream("images/down.png")));
        Button downButton = new Button("", downImageView);
        downButton.setMaxWidth(Double.MAX_VALUE);

        //PANES
        VBox leftPane = new VBox(5);
        leftPane.getChildren().addAll(filterTextField, leftList);

        VBox leftRightButtonsPane = new VBox(5);
        leftRightButtonsPane.getChildren().addAll(rightButton, leftButton);
        leftRightButtonsPane.setAlignment(Pos.CENTER);

        VBox rightPane = new VBox(5);
        // TextField dummyTextField = new TextField();
        // dummyTextField.setVisible(false);
        rightPane.getChildren().addAll(/*dummyTextField,*/ rightList);

        VBox upDownPane = new VBox(5);
        upDownPane.getChildren().addAll(upButton, downButton);
        upDownPane.setAlignment(Pos.CENTER);

        HBox mainPane = new HBox(10);
        mainPane.setPadding(new Insets(10, 50, 50, 50));
        mainPane.getChildren().addAll(leftPane, leftRightButtonsPane, rightPane, upDownPane);
        mainPane.setAlignment(Pos.CENTER);

        VBox.setVgrow(leftList, Priority.ALWAYS);
        VBox.setVgrow(rightList, Priority.ALWAYS);

        leftList.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
                rightList.setMaxHeight(newValue.doubleValue());
            }
        });
        leftPane.setAlignment(Pos.BOTTOM_CENTER);
        rightPane.setAlignment(Pos.BOTTOM_CENTER);

        leftButton.setDisable(true);
        rightButton.setDisable(true);
        upButton.setDisable(true);
        downButton.setDisable(true);

        leftList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
                rightButton.setDisable(newValue.intValue() == -1);
            }
        });

        rightList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int selectedIndex = newValue.intValue();
                leftButton.setDisable(selectedIndex == -1);
                upButton.setDisable(selectedIndex == -1 || selectedIndex == 0);
                downButton.setDisable(selectedIndex == -1 || selectedIndex == rightList.getItems().size()-1);
            }
            
        });

        //Buttons Action
        rightButton.setOnAction(event -> {
            int leftSelIndex = leftList.getSelectionModel().getSelectedIndex();
            String leftSelItem = leftList.getSelectionModel().getSelectedItem();
            rightList.getItems().add(leftSelItem);
            leftList.getItems().remove(leftSelIndex);
            allItems.remove(leftSelItem);
            leftList.getSelectionModel().select(leftSelIndex < leftList.getItems().size() ? leftSelIndex : leftSelIndex - 1);

            int rightSelIndex = rightList.getSelectionModel().getSelectedIndex();
            if(rightSelIndex >= 0 && rightSelIndex < rightList.getItems().size()){
                downButton.setDisable(false);
            }
        });

        leftButton.setOnAction(event -> {
            int rightSelIndex = rightList.getSelectionModel().getSelectedIndex();
            String rightSelItem = rightList.getSelectionModel().getSelectedItem();
            leftList.getItems().add(rightSelItem);
            allItems.add(rightSelItem);
            rightList.getItems().remove(rightSelItem);
            rightList.getSelectionModel().select(rightSelIndex < rightList.getItems().size() ? rightSelIndex : rightSelIndex - 1);
            if(rightList.getItems().size() == 1){
                downButton.setDisable(true);
            }
        });

        upButton.setOnAction(event -> {
            int upSelIndex = rightList.getSelectionModel().getSelectedIndex();
            String upSelItem = rightList.getSelectionModel().getSelectedItem();
            rightList.getItems().remove(upSelItem);
            rightList.getItems().add(upSelIndex - 1, upSelItem);
            rightList.getSelectionModel().select(upSelIndex-1);
        });

        downButton.setOnAction(event -> {
            int downSelIndex = rightList.getSelectionModel().getSelectedIndex();
            String downSelItem = rightList.getSelectionModel().getSelectedItem();
            rightList.getItems().remove(downSelItem);
            rightList.getItems().add(downSelIndex + 1, downSelItem);
            rightList.getSelectionModel().select(downSelIndex + 1);
        });


        // Implement filter
        filterTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String str = newValue;
                String selectedItem = leftList.getSelectionModel().getSelectedItem();

                leftList.getItems().clear();
                for(String item : allItems){
                    if(item.contains(str)){
                        leftList.getItems().add(item);
                    }
                }
                leftList.getSelectionModel().select(selectedItem);
            }
        });

        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());
        });

        Scene scene = new Scene(mainPane, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Lists");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}