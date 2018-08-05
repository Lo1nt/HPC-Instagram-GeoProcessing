package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Property;

public class InsHelper {
	public Map<String, Integer> gridMap;
	
	public InsHelper() {
		gridMap = new HashMap<String, Integer>();
	}
	
	//input inslocation object; decide which area it belongs to 
	public void classify(InsLocation ins, List<Property> pros) {
		for (Property property : pros) {
			if (belongTo(ins, property)) {
				break;
			}
		}
	}
	
	//decide whether ins belong to pro
	private boolean belongTo(InsLocation ins, Property pro) {
		double x = ins.getPointX();
		double y = ins.getPointY();
		String id = pro.getId();
		if (x < pro.getXmin() || x >= pro.getXmax()) {
			return false;
		}
		if (y < pro.getYmin() || y >= pro.getYmax()) {
			return false;
		}
		if (gridMap.containsKey(id)) {
			gridMap.put(id, gridMap.get(id) + 1);
		}
		else {
			gridMap.put(id, 1);
		}
		return true;
	}
}
