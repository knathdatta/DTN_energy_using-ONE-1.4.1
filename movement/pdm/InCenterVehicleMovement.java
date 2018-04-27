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

import java.util.ArrayList;
import java.util.List;

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

public class InCenterVehicleMovement extends MapBasedMovement {

	/** the Dijkstra shortest path finder */
	private DijkstraPathFinder pathFinder;
	private SimMap map;
	private String targets[];
	private String homeCenterType;
	private MapNode home;
	private boolean isReturn;

	private static int nextTarget = 0;

	private MapNode targetCenter;
	private List<MapNode> targetCenters = new ArrayList<MapNode>();

	int waypointIndex = 0;
	private Coord waypoints[];

	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * 
	 * @param settings
	 *            The Settings object where the settings are read from
	 */
	public InCenterVehicleMovement(Settings settings) {
		super(settings);

		this.homeCenterType = settings.getSetting("homeCenterType");
		this.targets = settings.getCsvSetting("targetCenters");

		String[] values = settings.getCsvSetting("waypoints");
		waypoints = new Coord[values.length / 2];
		for (int i = 0; i < values.length / 2; i++) {
			waypoints[i] = new Coord(Double.parseDouble(values[i * 2]),
					Double.parseDouble(values[i * 2 + 1]));
			System.out.println(waypoints[i]);
		}

		this.map = getMap();
		this.pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
		this.isReturn = false;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param mbm
	 *            The ShortestPathMapBasedMovement prototype to base the new
	 *            object to
	 */
	protected InCenterVehicleMovement(InCenterVehicleMovement rem) {

		super(rem);
		this.pathFinder = rem.pathFinder;
		this.map = rem.map;
		this.homeCenterType = rem.homeCenterType;
		this.targets = rem.targets;
		this.waypoints = rem.waypoints;
		this.home = getHome();
		this.isReturn = rem.isReturn;

		nextTarget = 0;
	}

	private MapNode getHome() {
		if (PDMConfig.getCenters().get(homeCenterType) == null)
			return null;
		MapNode h;

		List<MapNode> centers = PDMConfig.getCenters().get(homeCenterType);
		h = centers.get(rng.nextInt(centers.size()));

		if (homeCenterType.compareTo("mainreliefcenter") == 0) {
			List<MapNode> trgs = PDMConfig.getCenters().get(targets[0]);
			// targetCenter = trgs.get(rng.nextInt(trgs.size()));
			targetCenter = trgs.get(nextTarget++);
		}

		/*
		 * List<MapNode> mapsNodes = map.getNodes(); Coord coord = new
		 * Coord(2255, 105); MapNode minMapNode = mapsNodes.get(0); double
		 * mindist = minMapNode.getLocation().distance(coord);
		 * 
		 * for (int i = 1; i < mapsNodes.size(); i++) { MapNode mapNode =
		 * mapsNodes.get(i); double dist =
		 * mapNode.getLocation().distance(coord); if (dist < mindist) { mindist
		 * = dist; minMapNode = mapNode; } }
		 */
		// h = this.map.getClosestMapNode(new Coord(2255, 105));
		// h= this.selectRandomOkNode(this.map.getNodes());
		// h = new MapNode(new Coord(2921.75, 937.59));

		if (waypoints != null && waypoints.length > 0) {
			h = this.map.getClosestMapNode(waypoints[0]);
		} else {
			h = this.selectRandomOkNode(this.map.getNodes());
		}

		return h;
	}

	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		MapNode to = null;

		// System.out.println("home " + homePoints);
		if (isReturn) {
			to = home;
			isReturn = false;
		} else {
			// to=new MapNode(new Coord(1473.75, 736.25));
			/*
			 * if (targetCenters.size() == 0) { for (int i = 0; i <
			 * targets.length; i++) { String target = targets[i];
			 * targetCenters.addAll(PDMConfig.getCenters().get(target)); } }
			 */

			// do{
			// if (homeCenterType.compareTo("mainreliefcenter") == 0)
			// {
			// to = targetCenter;
			// break;
			// }
			// else
			/*
			 * int index = rng.nextInt(targetCenters.size()); to =
			 * targetCenters.get(index); targetCenters.remove(index);
			 */
			// System.out.println("Going to : " + to + ": "+ targetCenters);
			// }while(to.equals(home));

			// to = this.selectRandomOkNode(this.map.getNodes());
			// to = new MapNode(new Coord(859.82, 1245.70));

			/*
			 * List<MapNode> mapsNodes = map.getNodes(); Coord coord = new
			 * Coord(1070, 876); MapNode minMapNode = mapsNodes.get(0); double
			 * mindist = minMapNode.getLocation().distance(coord);
			 * 
			 * for (int i = 1; i < mapsNodes.size(); i++) { MapNode mapNode =
			 * mapsNodes.get(i); double dist =
			 * mapNode.getLocation().distance(coord); if (dist < mindist) {
			 * mindist = dist; minMapNode = mapNode; } } to = minMapNode;
			 */

			if (waypoints != null && waypoints.length > 0) {
				waypointIndex = (waypointIndex + 1) % waypoints.length;
				to = this.map.getClosestMapNode(this.waypoints[waypointIndex]);
			} else {
				to = this.selectRandomOkNode(this.map.getNodes());
			}

			//isReturn = true;
		}

		// System.out.println("Moving from: " + lastMapNode + " to " + to + " "
		// + isReturn);

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
	public InCenterVehicleMovement replicate() {
		return new InCenterVehicleMovement(this);
	}
}
