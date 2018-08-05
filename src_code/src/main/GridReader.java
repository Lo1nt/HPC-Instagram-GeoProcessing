package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import entity.Feature;
import entity.MelEntity;
import entity.Property;

import com.google.gson.Gson;

// melbGrid.json  :: /Users/minglun/Documents/Unimelb/COMP90024_CCC/project_1/melbGrid.json
//
//

public class GridReader {
	public static final String pathname = 
			"melbGrid.json";
	
	
	public List<Property> readGrid() {
		
		Gson gson = new Gson();
		MelEntity me;
		File file;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		List<Feature> features = null;
		List<Property> property = new ArrayList<Property>();
		try {
			file = new File(GridReader.pathname);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			
			me = gson.fromJson(isr, MelEntity.class);
			
			features = me.getFeatures();
			for (Feature f : features) {
				property.add(f.getProperties());
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return property;
	}

}
