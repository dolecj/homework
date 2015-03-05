package cz.dolec.paytrac.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Currency;

import cz.dolec.paytrac.core.AccountFactory;
import cz.dolec.paytrac.core.MultiAccount;

public class App {
	
	public static final int REPORT_INTERVAL = 60000; //60000 milliseconds is one minute.
	public static final String COMMAND_QUIT = "quit";
	public static final String COMMAND_PRINT = "print";
	
	
    public static void main( String[] args )
    {
    	System.out.println("Program started.");
    	
    	final MultiAccount ma = AccountFactory.buildAccount(Currency.getInstance("USD"));
    	ma.initCurrency(Currency.getInstance("USD"), new BigDecimal(111));
    	ma.initCurrency(Currency.getInstance("CZK"), new BigDecimal(222), new BigDecimal(1/24.683));
    	ma.initCurrency(Currency.getInstance("EUR"), new BigDecimal(333));
        
        Thread reporter = new Thread() {
            @Override
            public void run() {
            	try {
                	while (!isInterrupted()) {
                		ma.printAccountStatus();
                		Thread.sleep(REPORT_INTERVAL);                 
                	}
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        reporter.start();

        
        //  open up standard input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        do {
        	try {
        		input = br.readLine();
        		switch (input) {
        		case COMMAND_QUIT:
        			System.out.println("Program stopped.");
        	        System.exit(1);
        			break;
        		case COMMAND_PRINT:
        			ma.printAccountStatus();
        			break;
        		default:
        			try {
            			ma.add(input);
            		} catch (IllegalArgumentException e) {
            			e.printStackTrace();
            		}
        			break;
        		}
        	} catch (IOException ioe) {
        		System.out.println("IO error trying to read line!");
        		System.exit(1);
        	}
        } while (true);
   
        
        
   
     }
    
}
