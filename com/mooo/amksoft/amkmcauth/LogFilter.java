package com.mooo.amksoft.amkmcauth;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LogFilter implements Filter {

	@Override
	public boolean isLoggable(LogRecord record) {

		// Filter werkt niet helaas, een mooie voor later om uit te zoeken.
		if(record.getMessage().contains("/login")) {
			record.setMessage("Command /logon issued"); 
			return false;  // Suppress Message, alleen "/login" word nooit gevonden.... 
		}
		else {
			//record.setMessage("Command: >" + record.getMessage() + "<"); 
			return true;  // Show Message
		}
	}
}
