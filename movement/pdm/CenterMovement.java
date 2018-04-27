/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Md Yusuf Sarwar Uddin (mduddin2@illinois.edu)
 */

package movement.pdm;

import core.Coord;
import core.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import movement.MapBasedMovement;
import movement.Path;
import movement.map.MapNode;
import movement.map.SimMap;


public class CenterMovement extends MapBasedMovement{

    private SimMap map;
    private MapNode center;
    private String centerType;
    
    public CenterMovement(CenterMovement rep)
    {
        super(rep);  
        this.map = rep.map;
        this.centerType = rep.centerType;
        this.center = findNodeAsCenter();
    }
    
    public CenterMovement(Settings settings)
    {
        super(settings);
        this.map = getMap();
        this.centerType = settings.getSetting("centerType");        
    }
    
    private MapNode findNodeAsCenter()
    {        
        Map<String, List<MapNode>> allCenters = PDMConfig.getCenters();
        
        if (allCenters.get(centerType) == null)
        {
            allCenters.put(centerType, new ArrayList<MapNode>());
        }
        
        List<MapNode> centers = allCenters.get(centerType);
        MapNode candidate = null;
        
        boolean again = false;
        do
        {
            again = false;
            candidate = map.getNodes().get(rng.nextInt(map.getNodes().size()));
            
            if (centerType.compareTo("mainreliefcenter") == 0)
            {
                List<MapNode> maincenters = PDMConfig.getCenters().get(centerType);
                if (maincenters == null || maincenters.size() == 0)
                {
                    again = candidate.getLocation().distance(new Coord(2500,1500)) > 500;                    
                }
                else
                {
                    for(MapNode mc : maincenters)
                        again = again || mc.getLocation().distance(candidate.getLocation()) < 1000;
                }
            }
            else if (centerType.equals("neighborhood"))
            {
            	List<MapNode> nhs = PDMConfig.getCenters().get("neighborhood");
            	if (PDMConfig.getCenters().get("mainreliefcenter").get(0).getLocation().distance(candidate.getLocation()) < 800)
            		again = true;
            	else
            	{
            		for (MapNode n : nhs)            			
            			again = again || n.getLocation().distance(candidate.getLocation()) < 800;
            	}
            }
            else if (centerType.compareTo("house") == 0)
            {
                List<MapNode> hhs = PDMConfig.getCenters().get("neighborhood");
                MapNode hh = hhs.get(rng.nextInt(hhs.size()));
                again = hh.getLocation().distance(candidate.getLocation()) > 1000;
            }       
            else if (centerType.compareTo("evacuationcenter") == 0)
            {
                List<MapNode> hhs = PDMConfig.getCenters().get("neighborhood");
                MapNode hh = hhs.get(rng.nextInt(hhs.size()));
                again = hh.getLocation().distance(candidate.getLocation()) < 500;   
            }            
            else
            {
                for(MapNode n : centers)
                if (n.getLocation().distance(candidate.getLocation()) < 500)
                {
                    again = true;
                    break;
                }
            }              
        }while((getOkMapNodeTypes()!= null && !candidate.isType(getOkMapNodeTypes())) || centers.contains(candidate) || again);
        
        centers.add(candidate);
        return candidate;               
    }  
    
    @Override
    public Path getPath() {                
        return null;
    }

    @Override
    public Coord getInitialLocation() {
        return center.getLocation();
    }
    
    @Override
    public CenterMovement replicate() {        
        return new CenterMovement(this);
    }
}
