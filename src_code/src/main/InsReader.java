package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonParser;

import entity.Constant;
import entity.Grid;
import entity.Property;
import mpi.MPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class InsReader {
	
	//tinyInstagram.json  :: /Users/minglun/Documents/Unimelb/COMP90024_CCC/project_1/tinyInstagram.json
	//mediumInstagram.json :: /Users/minglun/Documents/Unimelb/COMP90024_CCC/project_1/mediumInstagram.json
	//

	
	//read file by line and depart its area
	@SuppressWarnings("rawtypes")
	public void analyzeFile(String pathname, int lineNum, List<Property> pros, String[] args) {
		Long begin = System.currentTimeMillis();
		File file;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = "";
		InsHelper insHelper = new InsHelper();
		Map[] sendbuf = new HashMap[1];
		MPI.Init(args);
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		int master = 0;
		try {
			file = new File(pathname);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			line = br.readLine();
			int countLine = 1;
			
			while (line != null) {			
				line = br.readLine();
				if (line == null || !line.contains("coordinates")) {
					continue;
				}
				if (line.indexOf("{") != 0) {
					break;
				}
				countLine++;
				try {
					if (rank == countLine % size) {
						String data = pretreatString(line);
						if (!data.endsWith("}")) {
							continue;
						}
						InsLocation insLocation = getIns(data);
						if (insLocation == null) {
							continue;
						}					
						insHelper.classify(insLocation, pros);
					}
				} catch (com.google.gson.JsonSyntaxException jse) {
					continue;
				}
			}
			
			sendbuf[0] = insHelper.gridMap;
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				isr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		Map[] recvbuf = new HashMap[size];
		MPI.COMM_WORLD.Barrier();
		MPI.COMM_WORLD.Gather(sendbuf, 0, 1, MPI.OBJECT, recvbuf, 0, 1, MPI.OBJECT, 0);
		if (rank == master) {
			
			for (int i = 0;i < size;i++) {
				for (String key : Constant.gridMap.keySet()) {
					
					int count1 = Constant.gridMap.get(key) ;
					int count2 = 0;
					if (recvbuf[i] == null) {
						continue;
					}
					if (recvbuf[i].get(key) != null) {
						count2 = (int) recvbuf[i].get(key);
					}
					Constant.gridMap.put(key, count1 + count2);
				}
			}

		}
		if (rank == master) {
			System.out.println("row calc: ");
			showRows();
			System.out.println("\n");
			System.out.println("Column clac: ");
			showColumns();
			System.out.println("\n");
			System.out.println("Boxes: ");
			showResult();
//			for (String key: Constant.gridMap.keySet()) {
//				System.out.println(key + " : " + Constant.gridMap.get(key));
//			}
		
		
			Long end = System.currentTimeMillis();
		
			System.out.println("time: " + (end - begin));
		}
		MPI.Finalize();
		
	}
	
	
	
	
	//delete if the line end with ","; do nothing with the last line; return String that has been processed
	private String pretreatString(String tmp) {
		String data = "";
		if (tmp.endsWith(",")) {
			data = tmp.substring(0, tmp.length()-1);
		}
		else {
			data = tmp;
		}
		return data;
	}
	
	
	//get point (x,y) return as InsHelper Object
	private InsLocation getIns(String data) {
		InsLocation ins = new InsLocation();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(data);
		JsonObject jo = je.getAsJsonObject();
		je = jo.get("doc").getAsJsonObject().get("coordinates");
		if (je == null) {
			return null;
		}
		je = je.getAsJsonObject().get("coordinates");
		

		if (!je.getAsJsonArray().get(0).isJsonNull()) {	
			ins.setPointX(je.getAsJsonArray().get(1).getAsDouble());
			ins.setPointY(je.getAsJsonArray().get(0).getAsDouble());
		}
		return ins;
	}
	
	private void showRows() {
		Grid[] rows = new Grid[4];
		rows[0] = new Grid("A", 0);
		rows[1] = new Grid("B", 0);
		rows[2] = new Grid("C", 0);
		rows[3] = new Grid("D", 0);
		for (String key : Constant.gridMap.keySet()) {
			switch (key.charAt(0)) {
			case 'A' : {
				rows[0].setCount(rows[0].getCount() + Constant.gridMap.get(key));
				break;
			}
			case 'B' : {
				rows[1].setCount(rows[1].getCount() + Constant.gridMap.get(key));
				break;
			}
			case 'C' : {
				rows[2].setCount(rows[2].getCount() + Constant.gridMap.get(key));
				
				break;
			}
			case 'D' : {
				rows[3].setCount(rows[3].getCount() + Constant.gridMap.get(key));
				break;
			}
			}
		}
		Arrays.sort(rows);
		for (Grid grid : rows) {
			System.out.println(grid.getId() + "-Row: " + grid.getCount() + " posts");
		}
		
		
	}
	
	
	private void showColumns() {
		Grid[] columns = new Grid[5];
		columns[0] = new Grid("1", 0);
		columns[1] = new Grid("2", 0);
		columns[2] = new Grid("3", 0);
		columns[3] = new Grid("4", 0);
		columns[4] = new Grid("5", 0);

		for (String key : Constant.gridMap.keySet()) {
			switch (key.charAt(1)) {
			case '1' : {
				columns[0].setCount(columns[0].getCount() + Constant.gridMap.get(key));
				break;
			}
			case '2' : {
				columns[1].setCount(columns[1].getCount() + Constant.gridMap.get(key));
				break;
			}
			case '3' : {
				columns[2].setCount(columns[2].getCount() + Constant.gridMap.get(key));
				break;
			}
			case '4' : {
				columns[3].setCount(columns[3].getCount() + Constant.gridMap.get(key));
				break;
			}
			case '5' : {
				columns[4].setCount(columns[4].getCount() + Constant.gridMap.get(key));
				break;
			}
			}
		}
		Arrays.sort(columns);
		for (Grid grid : columns) {
			System.out.println("Column " + grid.getId() + ": " + grid.getCount() + " posts");
		}
		
	}
	
	private void showResult() {
		Grid[] boxes = new Grid[16];
		int index = 0;
		for (String key : Constant.gridMap.keySet()) {
			boxes[index] = new Grid(key, Constant.gridMap.get(key));
			index++;
		}
		Arrays.sort(boxes);
		for (Grid grid : boxes) {
			System.out.println(grid.getId() + ": " + grid.getCount() + " posts");
		}
	}
}
