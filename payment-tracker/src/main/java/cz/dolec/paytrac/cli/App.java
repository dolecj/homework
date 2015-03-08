package cz.dolec.paytrac.cli;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Currency;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import cz.dolec.paytrac.core.AccountFactory;
import cz.dolec.paytrac.core.MultiAccount;

public class App {
	
	public static final String APP_PREFIX = "java -jar payment-tracker-<version>-jar-with-dependencies.jar";
	public static final int REPORT_INTERVAL = 60000; //60000 milliseconds is one minute.
	public static final String DEFAULT_CURRENCY = "USD";
	public static final String COMMAND_QUIT = "quit";
	public static final String COMMAND_PRINT = "print";
	
	private MultiAccount ma;
	
	private CommandLine cmdLine;
	
	private Option cmdOptInitFile = OptionBuilder.withArgName("iniFilePath").hasArg().withDescription("Init account file name").isRequired(false).create("f");
	private Option cmdOptDefaultCurrency = OptionBuilder.withArgName("defaultCurrency").hasArg().withDescription("Default currency code").isRequired(false).create("c");
	private Option cmdOptHelp = OptionBuilder.withArgName("help").hasArg(false).withDescription("Print help and exit").isRequired(false).create("h");
	private Options cmdOptions = new Options();
	
	/**
	 * Print usage help. 
	 */
	private void printUsage() {
		HelpFormatter helpFormatter = new HelpFormatter();
		PrintWriter helpPrintWriter = new PrintWriter(System.out);
		helpFormatter.printWrapped(helpPrintWriter, helpFormatter.getWidth(), "Payment Tracker");
		helpFormatter.printUsage(helpPrintWriter, helpFormatter.getWidth(), APP_PREFIX, cmdOptions);
		helpFormatter.printOptions(helpPrintWriter, helpFormatter.getWidth(), cmdOptions, helpFormatter.getLeftPadding(), helpFormatter.getDescPadding());
		helpPrintWriter.flush();
	}
	
	
	/**
	 * Non-static main method of payment tracker. 
	 * @param args
	 * @throws Exception
	 */
	public void doMain(String [] args) throws Exception {
    	cmdOptions.addOption(cmdOptInitFile).addOption(cmdOptDefaultCurrency).addOption(cmdOptHelp);
    	CommandLineParser cmdParser = new GnuParser();
    	cmdLine = cmdParser.parse(cmdOptions, args);
    	
    	// check commandline arguments and if there are some unknown, exit the program.
    	String[] remainingArgs = cmdLine.getArgs();
    	if (remainingArgs.length > 0) {
    		err.println("\nInvalid arguments. See help.");
    		printUsage();
    		return;
    	}
    	
    	// print help if needed
    	if (cmdLine.hasOption(cmdOptHelp.getOpt())) {
    		printUsage();
    		return;
    	}

    	
    	// set default currency
    	final Currency currency;
    	if (cmdLine.hasOption(cmdOptDefaultCurrency.getOpt())) {
    		String paramCurrency = cmdLine.getOptionValue(cmdOptDefaultCurrency.getOpt());
    		currency = Currency.getInstance(paramCurrency);
    	} else {
    		currency = Currency.getInstance(DEFAULT_CURRENCY);
    	}
    	ma = AccountFactory.buildAccount(currency);
    	
    	
    	// read init file
    	if (cmdLine.hasOption(cmdOptInitFile.getOpt())) {
    		String fileName = cmdLine.getOptionValue(cmdOptInitFile.getOpt());
    		String line = "";
    		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
    			line = br.readLine();
    			while (line != null) {
    				ma.processInput(line);
    				line = br.readLine();
    			}
    		} catch (IOException e) {
    			err.println(e.getMessage());
    			throw e;
    		} catch (IllegalArgumentException|IllegalStateException e) {
    			err.println(String.format("ERROR parsing input file - %s: %s", e.getMessage(), line));
    			throw e;
    		}
    	}
    	
    	// start reporter thread - prints account status periodically
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
        boolean running = true;
        do {
        	try {
        		input = br.readLine();
        		switch (input) {
        		case COMMAND_QUIT:
        	        running = false;
        			break;
        		case COMMAND_PRINT:
        			ma.printAccountStatus();
        			break;
        		default:
        			try {
            			ma.processInput(input);
            		} catch (IllegalArgumentException e) {
            			err.println(String.format("ERROR: %s", e.getMessage()));
            			out.println("Commands: print | quit | <currency> <amount> [exchangeRate]");
            		}
        			break;
        		}
        	} catch (IOException ioe) {
        		out.println("IO error trying to read line!");
        		System.exit(1);
        	}
        } while (running);
        reporter.interrupt(); // stop the reporter thread
	}
	

	public static void main( String[] args ) throws Exception {
		App app = new App();
		app.doMain(args);
	}
    
}
