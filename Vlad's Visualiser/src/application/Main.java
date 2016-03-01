package application;

import java.util.Arrays;
import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.shape.*;
//import java.awt.MouseInfo;
//import java.awt.Point;

public class Main extends Application {
	private double[] coordinates = new double[1000];
	private double[] points = new double[1000];
    private double xMin,xMax,yMin,yMax;
	private static final double Width = 9;
	private static final double Height = 8;
	private static final double Size = 100;
	private double scalingFactor, borderSizeX, borderSizeY;
	private double dX,dY;
	private BorderPane root;
	
	private Parent createContent(){
		root = new BorderPane();
		root.setPrefSize(Width * Size, Height * Size);
		//create the h Box at the bottom
		HBox boxa = addHBox("Visualise");
		boxa.setAlignment(Pos.TOP_CENTER);
		root.setBottom(boxa);
		root.setOnMouseMoved(e -> mouseMove());
		return root;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene1 = new Scene(createContent());
		primaryStage.setScene(scene1);
		primaryStage.setTitle("Scenario Week 4 - Visualiser");
		primaryStage.show();
	}
	
	private void buttonVisualise(String text){
		int i,linesLen,pointsLen;
		boolean ok = false;
		linesLen = 0; pointsLen = 0;
		String subsir;
		for(i=0;i<text.length();i++){
			if(text.charAt(i) == '('){
				for(int k = i+1; k< text.length();k++){
					if(text.charAt(k) == ','){
						subsir = text.substring(i+1, k);
						if(ok == false)
							coordinates[linesLen++] = Double.parseDouble(subsir);
						else 
							points[pointsLen++] = Double.parseDouble(subsir);
						break;
					}
				}
			}
			else if(text.charAt(i) == ')'){
				for(int k = i-1; k > 0; k--){
					if(text.charAt(k) == ','){
						subsir = text.substring(k+2, i);
						if(ok == false)
							coordinates[linesLen++] = Double.parseDouble(subsir);
						else 
							points[pointsLen++] = Double.parseDouble(subsir);
						break;
					}
				}
			}
			else if(text.charAt(i) == ';'){
				ok = true;
			}
		}
		System.out.println(Arrays.toString(coordinates));
		System.out.println(Arrays.toString(points));
	    drawLines(linesLen); 
	    drawPoints(pointsLen);
	}
	
	private void getBoundaries(int len){
		xMin = coordinates[0]; xMax = coordinates[0];
		yMin = coordinates[0]; yMax = coordinates[0];
		
		for(int counter=1;counter<len;counter++){
			//x is on even positions
			if(counter%2==0){
				if(coordinates[counter] < xMin)
					xMin = coordinates[counter];
				else if(coordinates[counter] > xMax)
					xMax = coordinates[counter];
			}
			//y is on odd positions
			else{
				if(coordinates[counter] < yMin)
					yMin = coordinates[counter];
				else if(coordinates[counter] > yMax)
					yMax = coordinates[counter];
			}
		}
		//checking
		System.out.println(xMin + " , " + xMax);
		System.out.println(yMin + " , " + yMax);
	}
	
	private void drawLines(int len){
        Line linie;
        getBoundaries(len);
        setSign(xMin, yMin);
        scalingFactor = 4.0*Size/maximum(maximum(xMax,yMax),maximum(dX*xMin,dY*yMin));
        borderSizeX = Size/scalingFactor;
        borderSizeY = Size/scalingFactor;
        
        System.out.println(scalingFactor + ", " + borderSizeX + ", " + borderSizeY);
		
        for(int index = 0 ;index < len-3; index+=2){
            linie = new Line((coordinates[index]+(dX)*xMin+borderSizeX)*scalingFactor, (coordinates[index+1]+(dY)*yMin+borderSizeY)*scalingFactor, (coordinates[index+2]+(dX)*xMin+borderSizeX)*scalingFactor, (coordinates[index+3]+(dY)*yMin+borderSizeY)*scalingFactor);         
        	root.getChildren().add(linie);
        }
        linie = new Line((coordinates[0]+(dX)*xMin+borderSizeX)*scalingFactor,(coordinates[1]+(dY)*yMin+borderSizeY)*scalingFactor,(coordinates[len-2]+(dX)*xMin+borderSizeX)*scalingFactor,(coordinates[len-1]+(dY)*yMin+borderSizeY)*scalingFactor); 
        root.getChildren().add(linie);
	}
	
	 private void drawPoints(int len){
		 Circle circle;
		 for(int index = 0 ;index < len-1; index+=2){
			 circle = new Circle();
			 circle.setCenterX((points[index] + (dX) * xMin+borderSizeX)*scalingFactor);
			 circle.setCenterY((points[index+1] + (dY) * yMin+borderSizeY)*scalingFactor);
			 circle.setRadius(2.5);
			 circle.setFill(Color.RED);
			 root.getChildren().add(circle);
		 }
	 }

	public HBox addHBox(String name) {
	    HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699");

	    Button button = new Button(name);
	    button.setPrefSize(100, 20);
		Label label = new Label("Insert the coordinates here:  ");
		TextField textField = new TextField();
		textField.setPrefSize(5*Size, 30);
		hbox.getChildren().addAll(label, textField);
	    button.setOnAction(e -> buttonVisualise(textField.getText()));
	    hbox.getChildren().add(button);
	    return hbox;
	}
	
	private void setSign(double x, double y){
		if(x>=0 && y>=0){ 
			dX = 1;  dY = 1; 
		}
		else if(x<=0 && y>=0){
			dX=-1; dY=1;
		}
		else if(x<=0 && y<=0){
			dX=-1; dY=-1;
		}
		else if(x>=0 && y<=0){
			dX=1; dY=-1;
		}	
	}

	private double maximum(double a, double b){
		if(a > b)
			return a;
		return b;
	}
	
	private void mouseMove(){
		//Point p = MouseInfo.getPointerInfo().getLocation();
		//String text = p.toString();
		//System.out.println(text);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
