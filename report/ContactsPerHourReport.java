/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import core.ConnectionListener;
import core.DTNHost;
import core.SimClock;
import core.Connection;
import java.util.List;
import core.SimError;
import core.UpdateListener;
/**
 * This report counts the number of contacts each hour
 * 
 * @author Frans Ekman
 */
public class ContactsPerHourReport extends Report implements ConnectionListener {

	private LinkedList<Integer> contactCounts;
	private int currentHourCount;
	private int currentHour;
	
	public ContactsPerHourReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		contactCounts = new LinkedList<Integer>();
	}
	
	public void hostsConnected(DTNHost host1, DTNHost host2) {
		
		
		int time = SimClock.getIntTime()/3600;
		
		//int time = SimClock.getIntTime();
		while (Math.floor(time) > currentHour) {
			contactCounts.add(new Integer(currentHourCount));
			currentHourCount = 0;
			currentHour++;
	//System.out.println(time+"......."+id1+".........."+id2);			
		}
		//} 
		
		currentHourCount++;
	
	}

	public void hostsDisconnected(DTNHost host1, DTNHost host2) {
		// Do nothing
	}




	



	public void done() {
		Iterator<Integer> iterator = contactCounts.iterator();
//my code anindita-2/07/15
		//List<DTNHost> host =new ArrayList<DTNHost>();
		//int i=0;
		//for (DTNHost h : hosts) {

			
	//my code end
		
		int hour = 0;
		while (iterator.hasNext()) {
			Integer count = (Integer)iterator.next();
			write(hour + "\t" + count);
			hour++;
		}
		super.done();
	}
	//}
}
