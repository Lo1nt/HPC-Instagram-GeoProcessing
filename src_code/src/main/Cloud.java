package main;


import java.util.List;

import entity.Property;

public class Cloud {
	private static String dataFile = "bigInstagram.json";
	
	public static void main(String[] args) {
		
		GridReader gr = new GridReader();
		List<Property> pros = gr.readGrid();
		
		int linePointer = 0;
		InsReader ir = new InsReader();
		
		ir.analyzeFile(dataFile, linePointer, pros, args);
	}
	
	
}