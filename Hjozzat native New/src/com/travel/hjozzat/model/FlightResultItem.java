package com.travel.hjozzat.model;

import org.json.JSONArray;


public class FlightResultItem {

	public String strTripId = null;
	public int intApiId;
	
	public String str_AirlineName = null;
	
	public String DepartTimeOne  = null;
	public String ArrivalTimeOne = null;
	
	public Long DepartDateTimeOne  = null;
	public Long ArrivalDateTimeOne = null;
	
	public String DepartTimeTwo  = null;
	public String ArrivalTimeTwo = null;
	
	public Long DepartDateTimeTwo  = null;
	public Long ArrivalDateTimeTwo = null;
	
	public String DepartTimeThree  = null;
	public String ArrivalTimeThree = null;
	
	public Long DepartDateTimeThree  = null;
	public Long ArrivalDateTimeThree = null;
	
	public String DepartTimeFour  = null;
	public String ArrivalTimeFour = null;
	
	public Long DepartDateTimeFour  = null;
	public Long ArrivalDateTimeFour = null;
	
	public Long longFlightDurationInMinsOne;
	public Long longFlightDurationInMinsTwo;
	public Long longFlightDurationInMinsThree;
	public Long longFlightDurationInMinsFour;
	
	public int intFlightStopsOne	= 0;
	
	public Long longLayoverTimeInMins = null;
	public String strLayoverAirport = null; 
	
	public Double doubleFlightPrice = null;
	
	public String strDisplayRate = null;
	public Boolean blRefundType;
	public String strDeepLink  = null;
	
	public JSONArray jarray = null;
	
	public int samePriceCount = 1;
}
