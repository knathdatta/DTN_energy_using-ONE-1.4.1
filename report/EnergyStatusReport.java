/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.HashSet;
import java.util.List;

import core.DTNHost;
import core.Settings;
import core.SimError;
import core.UpdateListener;

/**
 * Node energy level report. Reports the energy level of all (or only some) 
 * nodes every configurable-amount-of seconds. Writes reports only after
 * the warmup period.
 */
public class EnergyStatusReport extends Report implements UpdateListener {
	/** Reporting granularity -setting id ({@value}). 
	 * Defines the interval how often (seconds) a new snapshot of energy levels
	 * is created */
	public static final String GRANULARITY = "granularity";
	/** Optional reported nodes (comma separated list of network addresses). 
	 * By default all nodes are reported. */
	//public static final String REPORTED_NODES = "nodes";
	/** value of the granularity setting */
	protected final int granularity;
	/** time of last update*/
	protected double lastUpdate; 
	protected double hostTemp[];
	protected double hostTemp_rec[];
	protected double hostTemp_tra[];
	/** Networks addresses (integers) of the nodes which are reported */
	//protected HashSet<Integer> reportedNodes;
	
	/**
	 * Constructor. Reads the settings and initializes the report module.
	 */
	public EnergyStatusReport() {
		Settings settings = getSettings();
		this.lastUpdate = 0;	
		this.granularity = settings.getInt(GRANULARITY);
		
	//	if (settings.contains(REPORTED_NODES)) {
	//		this.reportedNodes = new HashSet<Integer>();
	//		for (Integer nodeId : settings.getCsvInts(REPORTED_NODES)) {
	//			this.reportedNodes.add(nodeId);
	//		}
	//	}
	//	else {
	//		this.reportedNodes = null;
	//	}
		
		init();
	}

	/**
	 * Creates a new snapshot of the energy levels if "granularity" 
	 * seconds have passed since the last snapshot. 
	 * @param hosts All the hosts in the world
	 */
	public void updated(List<DTNHost> hosts) {
		if(getSimTime()==0.1){
		hostTemp=new double[hosts.size()];
		//$$$
		hostTemp_rec= new double[hosts.size()];
	    hostTemp_tra= new double[hosts.size()];
		int i=0;
		Settings s = new Settings();
			for (DTNHost h : hosts) {
				hostTemp[i]=s.getDouble("initialEnergy");
				i++;
			}
		}

		double simTime = getSimTime();
		if (isWarmup()) {
			return; /* warmup period is on */
		}
		/* creates a snapshot once every granularity seconds */
		if (simTime - lastUpdate >= granularity) {
			createSnapshot(hosts);
			
			int i=0;
			for (DTNHost h : hosts) {
				hostTemp[i]=h.getEnergy();
				//$$$
				hostTemp_rec[i]=h.receivedEnergy();
				hostTemp_tra[i]=h.transmittedEnergy();
				i++;
			}
			this.lastUpdate = simTime - simTime % granularity;
		}
	}
	
	/**
	 * Creates a snapshot of energy levels 
	 * @param hosts The list of hosts in the world
	 */
	private void createSnapshot(List<DTNHost> hosts) {
			

		//write ("[" + (int)getSimTime() + "]"); /* simulation time stamp */
		int i=0;
		for (DTNHost h : hosts) {
			//if (this.reportedNodes != null && 
			//	!this.reportedNodes.contains(h.getAddress())) {
			//	continue; /* node not in the list */
			//}
			Double value = (Double)h.getEnergy();
			//$$$
		    Double value_rec = (Double)h.receivedEnergy();
		    Double value_tra = (Double)h.transmittedEnergy();


			if (value == null) {
				throw new SimError("Host " + h + 
						" is not using energy model");
			}
		//Aman Singh Code
			write((float)(getSimTime())+ " " +h.toString() + " " +  format(value) + " "+ format(hostTemp[i]) + " " + format(hostTemp[i]-value)+" "+h.offTime+" "+h.wakeupTime+" " +format(value_rec)+ " " + format(value_tra) );
			//System.out.println(" " +format(value_rec)+ " " + format(value_tra));
			i++;
		}
	
	}
	
}
