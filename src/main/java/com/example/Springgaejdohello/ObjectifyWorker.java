package com.example.Springgaejdohello;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class ObjectifyWorker {
	
	private static Objectify ofy;
	
	public static Objectify getofy() {
		if(ofy == null) {
			ofy = ObjectifyService.ofy();
		}
		
		return ofy;
	}

}
