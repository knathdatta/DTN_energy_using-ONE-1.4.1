/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Md Yusuf Sarwar Uddin (mduddin2@illinois.edu)
 */

package movement.pdm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movement.map.MapNode;
import core.Coord;
import core.DTNSim;
import core.Settings;


public class PDMConfig {
    private static Map<String, List<MapNode>>centers;
    public static Coord disasterCenter;
    public static double criticalRadius;    
    public static double repairRate;
    private static boolean instantiated = false;
    public static double startTime;
    public static double intensity;
    public static double constant;
    
    static {
    	DTNSim.registerForReset("movement.pdm.PDMConfig");
    	reset();
    }
    
    public PDMConfig(Settings settings)
    {        
        criticalRadius = Double.parseDouble(settings.getSetting("criticalRadius"));
        repairRate = Double.parseDouble(settings.getSetting("repairRate"));
        instantiate();
    }
    
    public static void reset() {
    	centers =  new HashMap<String, List<MapNode>>();
    }
    
    public static void instantiate()
    {
        if (instantiated) return;        
        centers = new HashMap<String, List<MapNode>>();
        disasterCenter = new Coord(2000,2000);        
        startTime = 3600.0;
        intensity = 5;
        constant = criticalRadius * criticalRadius / 10.0;        
        instantiated = true;
    }
    
    public static Map<String, List<MapNode>> getCenters()
    {
        instantiate();
        return centers;
    }
    
}
