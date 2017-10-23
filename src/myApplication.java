
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class myApplication extends Application {

	private String filePath = "";
	private TextField inputField = new TextField();
	private TextArea textArea = new TextArea();
	private TextArea errorOutput = new TextArea();
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Bare Bones IDE");
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		
        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
		
		BorderPane borderPane = new BorderPane();
		Button buttonNew = new Button("New");
		Button buttonOpen = new Button("Open");
		Button buttonSave = new Button("Save");
		Button buttonRun = new Button("Run");
		inputField.setPrefWidth(400);
		ToolBar toolBar = new ToolBar( buttonNew, buttonOpen, buttonSave, new Separator(), buttonRun, new Separator(), inputField);
		Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
		
		buttonNew.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        //System.out.println("Button New clicked");
		    		filePath = inputField.getText();
		    		textArea.setText("");
		    }
		});
		
		buttonOpen.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        //System.out.println("Button Open clicked");
		    		filePath = inputField.getText();
		    		textArea.setText(loadFile());
		    }
		});
		
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        //System.out.println("Button Save clicked");
		        filePath = inputField.getText();
		    }
		});
		
		buttonRun.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        //System.out.println("Button Run clicked");
		        
		    }
		});
		
		errorOutput.setBorder(border);
		textArea.setBorder(border);
		
		errorOutput.setEditable(false);
		borderPane.setCenter(textArea);
		borderPane.setTop(toolBar);
		borderPane.setBottom(errorOutput);

		Scene scene = new Scene(borderPane, 0, 0);
        primaryStage.setScene(scene);
		
		primaryStage.show();
	}

	private void saveFile() {
		
	}
	
	private String loadFile() {
		String str = "";
		try {
			File f = new File(filePath);
			if(f.exists() && !f.isDirectory()) { 
				BufferedReader in = new BufferedReader(new FileReader(filePath));
				String curLine = "";
				while (curLine != null) {
					if (curLine != "")
					str += curLine + "\n";
					curLine = in.readLine();
				}
			
				in.close();
			} else { throw new IOException(); }

		} catch (IOException e) { errorOutput.appendText("Invalid path entered");}
		
		return str;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
