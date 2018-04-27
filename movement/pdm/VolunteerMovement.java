/* 
 * Copyright 2007 TKK/Netlab
 * Released under GPLv3. See LICENSE.txt for details. 
 */

/**
 *
 * @author Md Yusuf Sarwar Uddin (mduddin2@illinois.edu)
 */

package movement.pdm;

import core.Coord;
import core.SimError;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.Settings;
import movement.MapBasedMovement;
import movement.Path;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.SimMap;

/**
 * Map based movement model that uses Dijkstra's algorithm to find shortest
 * paths between two random map nodes and Points Of Interest
 */
public class VolunteerMovement extends MapBasedMovement {

	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;
	private SimMap map;
	private MapNode home;
	private int neighborhood;
	private double radius;
	private boolean isReturn;

	MapNode dbMapNode;
	List<MapNode> rangeMapNodes;

	public VolunteerMovement(Settings settings) {
		super(settings);
		this.neighborhood = Integer.parseInt(settings
				.getSetting("neighborhood"));
		this.radius = Double.parseDouble(settings.getSetting("radius"));
		
		this.map = getMap();
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.isReturn = false;

		// int clusterno = settings.getInt("clusterno");
		// Settings s = new Settings("Group" + clusterno);
		String values[] = settings.getCsvSetting("dblocation");
		Coord dbCoord = new Coord(Double.parseDouble(values[0]),
				Double.parseDouble(values[1]));

		dbMapNode = map.getClosestMapNode(dbCoord);
		if (dbMapNode.getLocation().distance(home.getLocation()) > radius)
			System.out.println("out of radius");
		
		rangeMapNodes=new ArrayList<MapNode>();
		List<MapNode> nodes=map.getNodes();
		for(int i=0;i<nodes.size();i++) {
			if(nodes.get(i).getLocation().distance(dbCoord)<=radius) {
				rangeMapNodes.add(nodes.get(i));
			}
		}
	}

	/**
	 * Copyconstructor.
	 * 
	 * @param mbm
	 *            The ShortestPathMapBasedMovement prototype to base the new
	 *            object to
	 */
	protected VolunteerMovement(VolunteerMovement rem) {

		super(rem);
		this.pathFinder = rem.pathFinder;
		this.map = rem.map;
		this.neighborhood = rem.neighborhood;
		this.radius = rem.radius;
		this.dbMapNode=rem.dbMapNode;
		this.rangeMapNodes=rem.rangeMapNodes;
		this.home = getHome();
		this.isReturn = rem.isReturn;
	}

	private MapNode getHome() {
		MapNode currCenter = null;
		/*if (PDMConfig.getCenters().get("neighborhood") == null) {
			System.err
					.println("VolunteerMovement: No neighborhood type is declared.");
			System.exit(-1);
		}

		try {
			currCenter = PDMConfig.getCenters().get("neighborhood")
					.get(neighborhood);
		} catch (ArrayIndexOutOfBoundsException aiex) {
			System.err.println("VolunteerMovement: Invalid neighorhood "
					+ neighborhood);
			System.exit(-1);
		}*/
		
		currCenter=rangeMapNodes.get(rng.nextInt(rangeMapNodes.size()));

		return currCenter;
	}

	@Override
	public Path getPath() {

		Path p = new Path(generateSpeed());
		MapNode to = null;

		// System.out.println("home " + homePoints);

		do {
			to = map.getNodes().get(rng.nextInt(map.getNodes().size()));
			if (rng.nextDouble() < 0.1) {
				// to =
				// PDMConfig.getCenters().get("neighborhood").get(neighborhood);
				to = dbMapNode;
				break;
			}
			// to =
			// DisasterMovement.getCenters().get(servicingCenter).get(rng.nextInt(DisasterMovement.getCenters().get(servicingCenter).size()));
		} while (to.equals(lastMapNode)
				|| to.getLocation().distance(home.getLocation()) > radius);

		// System.out.println("Moving from " + lastMapNode + " to " + to + " " +
		// isReturn);

		List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);

		if (nodePath == null)
			return null;

		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to "
				+ to + ". The simulation map isn't fully connected";

		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}

		lastMapNode = to;

		return p;
	}

	@Override
	public Coord getInitialLocation() {
		lastMapNode = home;
		return home.getLocation().clone();
	}

	@Override
	public VolunteerMovement replicate() {
		return new VolunteerMovement(this);
	}
}
