package stagePack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable {
	@FXML
	private Button chooseButton;
	@FXML
	private Button startButton;
	@FXML
	private Button saveButton;
	@FXML
	private TextArea text;
	@FXML
	private TextArea text1;
	@FXML
	private Pane mediaPane;
	@FXML
	private ImageView imageView;
	@FXML
	private ImageView imageView1;
	@FXML
	private ProgressBar progress1;
	@FXML
	private ProgressBar progress2;
	@FXML
	private Label infoLabel;
	
	@FXML
	private Label label1;
	
	@FXML
	private Label label2;
	
	BufferedImage img = null;
	BufferedImage img1 = null;
	public static List<Integer> list;
	public static List<Character> splitList;
	
	//****************************
	
	public static List<Character> allChars;
	public static List<String> asciiNumbers;
	
	
	public static int counter = 0;
	File outputFile = null;
	File inputFile = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
	
	public void saveFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save Image");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image png(*.png)", "*.png"));
		File destination = fc.showSaveDialog(null);
		if (destination != null) {
		    try {
		        Files.copy(outputFile.toPath(), destination.toPath());
		    } catch (IOException ex) {
		        // handle exception...
		    }
		}
	}
	
	
	
	
	public void openFile(ActionEvent event) throws IOException {
		//inputFile.createNewFile();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open an image...");
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Image Files", "*.png"));
		inputFile = fileChooser.showOpenDialog(null);
		img = ImageIO.read(inputFile);
		Image image = new Image(inputFile.toURI().toString());
		imageView.setImage(image);
	}
	
	public void encrypt(ActionEvent event) {
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		String message = text.getText();
		convertToAscii(message);
		charList(list);

		int[][] pixels = new int[height][width];
		progress1.setVisible(true);
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[row].length; col++) {
				if (counter < splitList.size() - 1) {
					int pixel = img.getRGB(col, row);
					int a = (pixel >> 24) & 0xff;
					//a = changeBin(a);
					int r = (pixel >> 16) & 0xff;
					r = changeBin(r);
					int g = (pixel >> 8) & 0xff;
					g = changeBin(g);
					int b = pixel & 0xff;
					b = changeBin(b);
					pixel = (a << 24) | (r << 16) | (g << 8) | b;
					img.setRGB(col, row, pixel);
				}
			}
		}	
		
		try {
			outputFile = new File("output.png");
			ImageIO.write(img, "png", outputFile);
		} catch (IOException e) {
			System.out.println(e);
		}
		saveButton.setDisable(false);
		progress1.setProgress(1);
		label1.setText("Done!");
		
	}
	
	public static Integer changeBin(int rgb) {
		if(counter ==splitList.size())
			return rgb;
		String codeStr = Integer.toBinaryString(rgb); // original binary representation of the integer
		codeStr = codeStr.substring(0, codeStr.length() - 1) + splitList.get(counter);
		rgb = Integer.parseInt(codeStr, 2);
		counter++;
		return rgb;
	}
	
	public static void charList(List<Integer> list) {
		splitList = new ArrayList<Character>();
		for (int i = 0; i < list.size(); i++) {
			String binStr = Integer.toBinaryString(list.get(i)); // convert item in List of numbers to String of
																	// binaries;
			if(binStr.length()<8) {
				int binaryZeros = 8-binStr.length();
				binStr = appendZero(binStr, binaryZeros);
			}
			char[] a = binStr.toCharArray();
			// Collections.addAll(splitList, a);
			for (int j = 0; j < a.length; j++) {
				splitList.add(a[j]);
			}
		}
	}
	
	static String appendZero(String str, int n) {
	    StringBuilder builder = new StringBuilder(str);
	    for (int i = 0; i < n; i++) {
	        builder.insert(0,"0");
	    }
	    return builder.toString();
	}
	
	public static void convertToAscii(String str) {
		list = new ArrayList<Integer>();
		for (int i = 0; i < str.length(); i++) {
			char character = str.charAt(i);
			int ascii = (int) character;
			list.add(ascii);
			// System.out.println(ascii);
		}
	}
	
	//******************************************************************
	
	public static void retrieve(int rgb) {
		String codeStr = Integer.toBinaryString(rgb);
		//codeStr = codeStr.substring(codeStr.length()-1, codeStr.length());
		char ch = codeStr.charAt(codeStr.length()-1);
		allChars.add(ch);	
	}
	
	public static void fasten(List<Character> list) {
		int counter = 0;
		StringBuilder str = new StringBuilder();
		for(int i =0; i<list.size();i++) {
			 str.append(list.get(i));
			 counter++;
			 if(counter==8) {
				 asciiNumbers.add(str.toString());
				 str.setLength(0);
				 counter =0;
			 }
		}
	}
	
	public static String convertToString(List<String> list) {
		StringBuilder str = new StringBuilder();
		for(int i=0;i<list.size();i++) {
			if(list.get(i).length()==8) { 
			int num = Integer.parseInt(list.get(i), 2);
			if(num == 35)
				return str.toString();
			char ch = (char) num;
			str.append(ch);
			} else				
				list.remove(i);		// delete string if it's not a character
		}
		return str.toString();
	}
	
	public void openFileDecrypt(ActionEvent event) throws IOException, InvocationTargetException {
		//inputFile.createNewFile();
		FileChooser fc = new FileChooser();
		fc.setTitle("Open an image...");
		fc.getExtensionFilters().addAll(
		         new ExtensionFilter("Image Files", "*.png"));
		File inputFileDecrypt = fc.showOpenDialog(null);
		img1 = ImageIO.read(inputFileDecrypt);
		Image image1 = new Image(inputFileDecrypt.toURI().toString());
		imageView1.setImage(image1);
	}
	
	public void decrypt(ActionEvent event){
		allChars = new ArrayList<Character>();
		asciiNumbers = new ArrayList<String>();
		int width = img1.getWidth();
		int height = img1.getHeight();

		int[][] pixels = new int[height][width];
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[row].length; col++) {
				int pixel = img1.getRGB(col, row);
				int a = (pixel >> 24) & 0xff;
				//retrieve(a);
				
				int r = (pixel >> 16) & 0xff;
				retrieve(r);
				
				int g = (pixel >> 8) & 0xff;
				retrieve(g);
				
				int b = pixel & 0xff;
				retrieve(b);
			}
		}
		
		fasten(allChars);
		
		label2.setText("Decryption done!");
		progress2.setVisible(true);
		progress2.setProgress(1);
		
		String message = convertToString(asciiNumbers);
		
		text1.setDisable(false);
		text1.setText(message);
	}
	

}
