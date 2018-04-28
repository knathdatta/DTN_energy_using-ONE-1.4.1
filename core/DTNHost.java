/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import movement.MovementModel;
import movement.Path;
import routing.MessageRouter;
import routing.util.RoutingInfo;

/**
 * A DTN capable host.
 */
public class DTNHost implements Comparable<DTNHost> {
	private static int nextAddress = 0;
	private int address;

	private Coord location; 	// where is the host
	private Coord destination;	// where is it going

	private MessageRouter router;
	private MovementModel movement;
	private Path path;
	private double speed;
	private double nextTimeToMove;
	private String name;
	private List<MessageListener> msgListeners;
	private List<MovementListener> movListeners;
	private List<NetworkInterface> net;
	private ModuleCommunicationBus comBus;

//AmanSingh Code
	public int energyflag=0;
	function fun=new function();
	//ranfunction ranfun=new ranfunction();
	public int ranflag=0;
	public double energy=10;
	public double transferLoss=0;
	public double recieverLoss=0;
	public boolean wakeupState=true;
	public double offTime=1;
	public double wakeupTime=1;
	public double nextTimeToScan=0;
	private double lastScanTime=0;
	public double scannerLoss=0;
	private boolean flag=false;
	public double energyCounter=10;
	private boolean reported=false;
	public int relayCount=0;
	public String modname;
	private String gId;
	public double node1Time=0;
	public double node2Time=0;
	public double node3Time=0;
	public double node4Time=0;
	public double node5Time=0;
	public int R1,R2,R3,R4,R5;
	public int flag0=0;
	public double Nodeflag=-1;
        public double gridcount=1;
	public int flag2=-1;
        public int flag3=-1;
	public int flag4=-1;
	public int flag5=-1;
        public int R=0;
         //$$$
        public double received_energy=0;
        public double transmitted_energy=0;
       // public String ON;
       // public String OFF;
//AmanSingh Code

	static {
		DTNSim.registerForReset(DTNHost.class.getCanonicalName());
		reset();
	}
	/**
	 * Creates a new DTNHost.
	 * @param msgLs Message listeners
	 * @param movLs Movement listeners
	 * @param groupId GroupID of this host
	 * @param interf List of NetworkInterfaces for the class
	 * @param comBus Module communication bus object
	 * @param mmProto Prototype of the movement model of this host
	 * @param mRouterProto Prototype of the message router of this host
	 */
	public DTNHost(List<MessageListener> msgLs,
			List<MovementListener> movLs,
			String groupId, List<NetworkInterface> interf,
			ModuleCommunicationBus comBus,
			MovementModel mmProto, MessageRouter mRouterProto) {
		this.comBus = comBus;
		this.location = new Coord(0,0);
		this.address = getNextAddress();
		this.name = groupId+address;
		this.net = new ArrayList<NetworkInterface>();
		this.modname = groupId+address;
		for (NetworkInterface i : interf) {
			NetworkInterface ni = i.replicate();
			ni.setHost(this);
			net.add(ni);

		//AmanSingh Code

		Settings s = new Settings();
		this.gId = groupId;
		if (s.contains("offTime"))
		this.offTime = s.getDouble("offTime");
		if (s.contains("WakeUpTime"))
		this.wakeupTime = s.getDouble("WakeUpTime");
		if (s.contains("initialEnergy"))
		this.energy = s.getDouble("initialEnergy");
		if (s.contains("scannerLoss"))
		this.scannerLoss = s.getDouble("scannerLoss");
		if (s.contains("transferLoss"))
		this.transferLoss = s.getDouble("transferLoss");
		if (s.contains("recieverLoss"))
		this.recieverLoss = s.getDouble("recieverLoss");
		this.energyCounter = s.getDouble("initialEnergy");

 //$$$
        this.received_energy = received_energy;
        this.transmitted_energy =  transmitted_energy;




	//if(flag1==0){
		
		//R2=(int)(Math.random()*10);
		//R3=(int)(Math.random()*10);
		//R4=(int)(Math.random()*6);
		//R5=(int)(Math.random()*6);
               
		//flag1=1;
	//}

		//AmanSingh Code................................................

		}

		// TODO - think about the names of the interfaces and the nodes

		this.msgListeners = msgLs;
		this.movListeners = movLs;

		// create instances by replicating the prototypes
		this.movement = mmProto.replicate();
		this.movement.setComBus(comBus);
		this.movement.setHost(this);
		setRouter(mRouterProto.replicate());

		this.location = movement.getInitialLocation();

		this.nextTimeToMove = movement.nextPathAvailable();
		this.path = null;

		if (movLs != null) { // inform movement listeners about the location
			for (MovementListener l : movLs) {
				l.initialLocation(this, this.location);
			}
		}
	}

	/**
	 * Returns a new network interface address and increments the address for
	 * subsequent calls.
	 * @return The next address.
	 */
	private synchronized static int getNextAddress() {
		return nextAddress++;
	}

	/**
	 * Reset the host and its interfaces
	 */
	public static void reset() {
		nextAddress = 0;
	}

	/**
	 * Returns true if this node is actively moving (false if not)
	 * @return true if this node is actively moving (false if not)
	 */
	public boolean isMovementActive() {
		return this.movement.isActive();
	}


	/**
	 * Returns true if this node's radio is active (false if not)
	 * @return true if this node's radio is active (false if not)
	 */
	public boolean isRadioActive() {
//AmanSingh Code
		if(this.wakeupState){
		/* TODO: make this work for multiple interfaces */
		return this.getInterface(1).isActive();
		}
		else
		return false;
//AmanSingh Code...............
	}

	/**
	 * Set a router for this host
	 * @param router The router to set
	 */
	private void setRouter(MessageRouter router) {
		router.init(this, msgListeners);
		this.router = router;
	}

	/**
	 * Returns the router of this host
	 * @return the router of this host
	 */
	public MessageRouter getRouter() {
		return this.router;
	}

	/**
	 * Returns the network-layer address of this host.
	 */
	public int getAddress() {
		return this.address;
	}

	/**
	 * Returns this hosts's ModuleCommunicationBus
	 * @return this hosts's ModuleCommunicationBus
	 */
	public ModuleCommunicationBus getComBus() {
		return this.comBus;
	}

    /**
	 * Informs the router of this host about state change in a connection
	 * object.
	 * @param con  The connection object whose state changed
	 */
	public void connectionUp(Connection con) {
		this.router.changedConnection(con);

	}

	public void connectionDown(Connection con) {
		this.router.changedConnection(con);
	}

	/**
	 * Returns a copy of the list of connections this host has with other hosts
	 * @return a copy of the list of connections this host has with other hosts
	 */
	public List<Connection> getConnections() {
		List<Connection> lc = new ArrayList<Connection>();

		for (NetworkInterface i : net) {
			lc.addAll(i.getConnections());
		}

		return lc;
	}

	/**
	 * Returns the current location of this host.
	 * @return The location
	 */
	public Coord getLocation() {
		return this.location;
	}

	/**
	 * Returns the Path this node is currently traveling or null if no
	 * path is in use at the moment.
	 * @return The path this node is traveling
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Sets the Node's location overriding any location set by movement model
	 * @param location The location to set
	 */
	public void setLocation(Coord location) {
		this.location = location.clone();
	}

	/**
	 * Sets the Node's name overriding the default name (groupId + netAddress)
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
		this.modname = name;
	}

	/**
	 * Returns the messages in a collection.
	 * @return Messages in a collection
	 */
	public Collection<Message> getMessageCollection() {
		return this.router.getMessageCollection();
	}

	/**
	 * Returns the number of messages this node is carrying.
	 * @return How many messages the node is carrying currently.
	 */
	public int getNrofMessages() {
		return this.router.getNrofMessages();
	}

	/**
	 * Returns the buffer occupancy percentage. Occupancy is 0 for empty
	 * buffer but can be over 100 if a created message is bigger than buffer
	 * space that could be freed.
	 * @return Buffer occupancy percentage
	 */
	public double getBufferOccupancy() {
		double bSize = router.getBufferSize();
		double freeBuffer = router.getFreeBufferSize();
		return 100*((bSize-freeBuffer)/bSize);
	}

	/**
	 * Returns routing info of this host's router.
	 * @return The routing info.
	 */
	public RoutingInfo getRoutingInfo() {
		return this.router.getRoutingInfo();
	}

	/**
	 * Returns the interface objects of the node
	 */
	public List<NetworkInterface> getInterfaces() {
		return net;
	}

	/**
	 * Find the network interface based on the index
	 */
	public NetworkInterface getInterface(int interfaceNo) {
		NetworkInterface ni = null;
		try {
			ni = net.get(interfaceNo-1);
		} catch (IndexOutOfBoundsException ex) {
			throw new SimError("No such interface: "+interfaceNo +
					" at " + this);
		}
		return ni;
	}

	/**
	 * Find the network interface based on the interfacetype
	 */
	protected NetworkInterface getInterface(String interfacetype) {
		for (NetworkInterface ni : net) {
			if (ni.getInterfaceType().equals(interfacetype)) {
				return ni;
			}
		}
		return null;
	}

	/**
	 * Force a connection event
	 */
	public void forceConnection(DTNHost anotherHost, String interfaceId,
			boolean up) {
		NetworkInterface ni;
		NetworkInterface no;

		if (interfaceId != null) {
			ni = getInterface(interfaceId);
			no = anotherHost.getInterface(interfaceId);

			assert (ni != null) : "Tried to use a nonexisting interfacetype "+interfaceId;
			assert (no != null) : "Tried to use a nonexisting interfacetype "+interfaceId;
		} else {
			ni = getInterface(1);
			no = anotherHost.getInterface(1);

			assert (ni.getInterfaceType().equals(no.getInterfaceType())) :
				"Interface types do not match.  Please specify interface type explicitly";
		}

		if (up) {
			ni.createConnection(no);
		} else {
			ni.destroyConnection(no);
		}
	}

	/**
	 * for tests only --- do not use!!!
	 */
	public void connect(DTNHost h) {
		System.err.println(
				"WARNING: using deprecated DTNHost.connect(DTNHost)" +
		"\n Use DTNHost.forceConnection(DTNHost,null,true) instead");
		forceConnection(h,null,true);
	}

	/**
	 * Updates node's network layer and router.
	 * @param simulateConnections Should network layer be updated too
	 */
	public void update(boolean simulateConnections) {
// AmanSingh Code

	 if(this.ranflag==0)
                    {  Random rn = new Random();
	                this.R = rn.nextInt(10);
			//System.out.println(",,,,,,,,,,,,,,,");
                       // System.out.println(R);
                        this.ranflag=1;
                      }
	if(this.energy>0)
	   {   
                

		double time=SimClock.getTime();
                
	
	if(time>=this.nextTimeToScan)
		{        
				
				
				        
					if(this.nextTimeToScan<this.R)
						{//System.out.println(nextTimeToScan);
                                                  
						     this.Nodeflag=0;
						     this.wakeupState=false;	
                                                    //  System.out.println(Nodeflag);
                                                     
                                                     }
					
					else 
					    { this.Nodeflag=1;
                                                                  if(this.Nodeflag==1 && this.gridcount<8)
                                                                     {
                                                                     if(fun.function(this.gridcount))
										{
										   this.wakeupState=true;
                                                                                   // System.out.println(gridcount); 	
                                                                                   this.gridcount=this.gridcount+1;
										   	
                                                                                      
										}
										else
										{
										
											this.wakeupState=false;
                                                                                        this.gridcount=this.gridcount+1;
								
                                                                     		}
                                                                     }
                                                          else 
                                                            {this.gridcount=1;}              

                                          }					
								


					  
					
								//if(this.Nodeflag==8){//System.out.println(this.Nodeflag);
								//	this.Nodeflag=1;
								//}
						


			if(this.wakeupState==true){
				this.nextTimeToScan=this.nextTimeToScan+wakeupTime;
                                  // System.out.println(nextTimeToScan);
			}
			else{
				this.nextTimeToScan=this.nextTimeToScan+offTime;
				//System.out.println(nextTimeToScan);
			}

		}

	}
	else
	{
		if(!this.reported)
		{
			this.reported=true;
		}

	this.wakeupState=false;
	}
	//code end for wakeup management

	//code to reduce scan energy
	flag=false;
	List<Connection> con=this.getConnections();
	for (Connection c : con){
		if(c.isTransferring())
		{

		this.flag=true;
		break;

		}
	}
	if(this.wakeupState && (!this.flag))
	this.energy=this.energy-(scannerLoss*wakeupTime);
	//AmanSingh Code....................


			if (!isRadioActive()) {
				// Make sure inactive nodes don't have connections
				tearDownAllConnections();
				return;
			}

			if (simulateConnections) {
				for (NetworkInterface i : net) {
					i.update();
				}
			}
			this.router.update();

	}

	/**
	 * Tears down all connections for this host.
	 */

//AmanSingh Code
	    public double getEnergy(){
		return this.energy;
		}
		//$$$
		public double receivedEnergy()
		{//System.out.println(received_energy);
		return this.received_energy;
		//System.out.println("@@@@@@");
		
		}
		public double transmittedEnergy(){
		return this.transmitted_energy;
		}
//kashi
	//public int getOnOff(){
             //if( this.wakeupState=true)
                       //{return 1;}
            //else 
		//return 0;
		//}
//AmanSingh Code................
	private void tearDownAllConnections() {
		for (NetworkInterface i : net) {
			// Get all connections for the interface
			List<Connection> conns = i.getConnections();
			if (conns.size() == 0) continue;

			// Destroy all connections
			List<NetworkInterface> removeList =
				new ArrayList<NetworkInterface>(conns.size());
			for (Connection con : conns) {
				removeList.add(con.getOtherInterface(i));
			}
			for (NetworkInterface inf : removeList) {
				i.destroyConnection(inf);
			}
		}
	}

	/**
	 * Moves the node towards the next waypoint or waits if it is
	 * not time to move yet
	 * @param timeIncrement How long time the node moves
	 */
	public void move(double timeIncrement) {
		double possibleMovement;
		double distance;
		double dx, dy;

		if (!isMovementActive() || SimClock.getTime() < this.nextTimeToMove) {
			return;
		}
		if (this.destination == null) {
			if (!setNextWaypoint()) {
				return;
			}
		}

		possibleMovement = timeIncrement * speed;
		distance = this.location.distance(this.destination);

		while (possibleMovement >= distance) {
			// node can move past its next destination
			this.location.setLocation(this.destination); // snap to destination
			possibleMovement -= distance;
			if (!setNextWaypoint()) { // get a new waypoint
				return; // no more waypoints left
			}
			distance = this.location.distance(this.destination);
		}

		// move towards the point for possibleMovement amount
		dx = (possibleMovement/distance) * (this.destination.getX() -
				this.location.getX());
		dy = (possibleMovement/distance) * (this.destination.getY() -
				this.location.getY());
		this.location.translate(dx, dy);
	}

	/**
	 * Sets the next destination and speed to correspond the next waypoint
	 * on the path.
	 * @return True if there was a next waypoint to set, false if node still
	 * should wait
	 */
	private boolean setNextWaypoint() {
		if (path == null) {
			path = movement.getPath();
		}

		if (path == null || !path.hasNext()) {
			this.nextTimeToMove = movement.nextPathAvailable();
			this.path = null;
			return false;
		}

		this.destination = path.getNextWaypoint();
		this.speed = path.getSpeed();

		if (this.movListeners != null) {
			for (MovementListener l : this.movListeners) {
				l.newDestination(this, this.destination, this.speed);
			}
		}

		return true;
	}

	/**
	 * Sends a message from this host to another host
	 * @param id Identifier of the message
	 * @param to Host the message should be sent to
	 */
	public void sendMessage(String id, DTNHost to) {
		this.router.sendMessage(id, to);
	}

	/**
	 * Start receiving a message from another host
	 * @param m The message
	 * @param from Who the message is from
	 * @return The value returned by
	 * {@link MessageRouter#receiveMessage(Message, DTNHost)}
	 */
	public int receiveMessage(Message m, DTNHost from) {
		int retVal = this.router.receiveMessage(m, from);

		if (retVal == MessageRouter.RCV_OK) {
			m.addNodeOnPath(this);	// add this node on the messages path
		}

		return retVal;
	}

	/**
	 * Requests for deliverable message from this host to be sent trough a
	 * connection.
	 * @param con The connection to send the messages trough
	 * @return True if this host started a transfer, false if not
	 */
	public boolean requestDeliverableMessages(Connection con) {
		return this.router.requestDeliverableMessages(con);
	}

	/**
	 * Informs the host that a message was successfully transferred.
	 * @param id Identifier of the message
	 * @param from From who the message was from
	 */
	public void messageTransferred(String id, DTNHost from) {
		this.router.messageTransferred(id, from);
	}

	/**
	 * Informs the host that a message transfer was aborted.
	 * @param id Identifier of the message
	 * @param from From who the message was from
	 * @param bytesRemaining Nrof bytes that were left before the transfer
	 * would have been ready; or -1 if the number of bytes is not known
	 */
	public void messageAborted(String id, DTNHost from, int bytesRemaining) {
		this.router.messageAborted(id, from, bytesRemaining);
	}

	/**
	 * Creates a new message to this host's router
	 * @param m The message to create
	 */
	public void createNewMessage(Message m) {
		if(this.getEnergy() > 0)
	//System.out.println("Current host in DTNHost"+this.toString());
	//System.out.println("Router information " + this.router.toString());
			this.router.createNewMessage(m);

	}

	/**
	 * Deletes a message from this host
	 * @param id Identifier of the message
	 * @param drop True if the message is deleted because of "dropping"
	 * (e.g. buffer is full) or false if it was deleted for some other reason
	 * (e.g. the message got delivered to final destination). This effects the
	 * way the removing is reported to the message listeners.
	 */
	public void deleteMessage(String id, boolean drop) {
		this.router.deleteMessage(id, drop);
	}

	/**
	 * Returns a string presentation of the host.
	 * @return Host's name
	 */
	public String toString() {
		return name;
	}

	/**
	 * Checks if a host is the same as this host by comparing the object
	 * reference
	 * @param otherHost The other host
	 * @return True if the hosts objects are the same object
	 */
	public boolean equals(DTNHost otherHost) {
		return this == otherHost;
	}

	/**
	 * Compares two DTNHosts by their addresses.
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(DTNHost h) {
		return this.getAddress() - h.getAddress();
	}

}
