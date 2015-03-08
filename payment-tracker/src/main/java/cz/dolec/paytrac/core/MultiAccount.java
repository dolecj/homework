package cz.dolec.paytrac.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

/**
 * Class represents one account that can have multiple currencies theirs amounts. The class must allways be instantiated with it's default currency.  
 * Exchange rates can be defined.    
 * @author jirik
 *
 */
public class MultiAccount {

	private final Currency defaultCurrency;
	private Map<Currency, BigDecimal> amounts = new HashMap<>();
	private Map<Currency, BigDecimal> exchangeRates = new HashMap<>();
	
	// not public constructor 
	MultiAccount(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}
	
	/**
	 * Initiate currency with it's amount.  
	 * @param currency
	 * @param amount
	 * @throws IllegalStateException
	 */
	public void initCurrency(Currency currency,  BigDecimal amount) throws IllegalStateException {
		if (this.amounts.containsKey(currency)) {
			throw new IllegalStateException(String.format("Currency '%s' already initialized.", currency));
		}
		synchronized(this) {
			this.amounts.put(currency, amount);
		}
	}
	
	/**
	 * Initiate currency with it's amount and exchange rate.
	 * @param currency
	 * @param amount
	 * @param exchangeRate
	 * @throws IllegalStateException
	 */
	public void initCurrency(Currency currency,  BigDecimal amount, BigDecimal exchangeRate) throws IllegalStateException {
		initCurrency(currency, amount);
		synchronized(this) {
			this.exchangeRates.put(currency, exchangeRate);
		}
	}
	
	/**
	 * Add value to accounts map. 
	 * @param currency
	 * @param amount
	 * @throws IllegalStateException
	 */
	public void add(Currency currency, BigDecimal amount) throws IllegalStateException {
		if (null == this.amounts.get(currency)) {
			throw new IllegalStateException(String.format("Could not add amount - currency '%s' is not initialized.", currency));
		}
		synchronized (this) {
			BigDecimal result = this.amounts.get(currency).add(amount); 
			this.amounts.put(currency, result);
		}
	}
	
	/**
	 * Parse formated input string: "$currencyCode $amount", for example: "USD 25" and add (or init) the value into amounts map.    
	 * @param input
	 */
	public void processInput(String input) {
		input = input.trim();
		Pattern pattern = Pattern.compile("([A-Z]{3})(\\s)(\\-?[0-9]+\\.?[0-9]*)(\\s?)([0-9]+\\.?[0-9]*)?");
		Matcher matcher = pattern.matcher(input);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Wrong input format.");
		}
		String currencyCode = matcher.group(1);
		String inMoney = currencyCode + " " + matcher.group(3); 
		String inExchRate = matcher.group(5);
		
		Money m = Money.parse(inMoney);
		Currency c = Currency.getInstance(m.getCurrencyUnit().getCurrencyCode());
		if (getCurrencySet().contains(c)) {
			this.add(c, m.getAmount());
		} else {
			this.initCurrency(c, m.getAmount());
		}
		
		if (null != inExchRate) { // exchange rate exists in the input
			if (defaultCurrency.toString().equals(currencyCode)) {
				throw new IllegalArgumentException("Cannot specify exchange rate for the default currency itself.");
			}
			setExchangeRate(c, new BigDecimal(inExchRate));
		}
		
	}
	
	/**
	 * Get current amount of given currency.
	 * @param currency
	 * @return
	 */
	public BigDecimal getAmount(Currency currency) {
		return this.amounts.get(currency);
	}
	
	/**
	 * Get amount converted to default currency.
	 * @param currency
	 * @return
	 */
	public BigDecimal getConvertedAmount(Currency currency) {
		BigDecimal exchangeRate = this.exchangeRates.get(currency);
		if (null == exchangeRate) {
			throw new IllegalArgumentException(String.format("Exchange rate for the currency '%s' is not set.", currency));
		}
		Money moneyAmount = Money.of(CurrencyUnit.of(currency),this.amounts.get(currency)); 
		return moneyAmount.convertedTo(CurrencyUnit.of(defaultCurrency), exchangeRate, RoundingMode.HALF_UP).getAmount(); 
	}
	
	/**
	 * Set exchange rate for given currency.
	 * @param currency
	 * @param exchangeRate
	 */
	public synchronized void setExchangeRate(Currency currency, BigDecimal exchangeRate) {
		this.exchangeRates.put(currency, exchangeRate);
	}
	
	
	/**
	 * Print account status.
	 */
	public synchronized void printAccountStatus() {
		for (Currency c : this.amounts.keySet()) {
			BigDecimal amount = this.amounts.get(c);
			if (!(amount.compareTo(BigDecimal.ZERO) == 0)) { // omit zero values 
				StringBuilder sb = new StringBuilder();
				sb.append(Money.of(CurrencyUnit.of(c), this.amounts.get(c)));
				if (this.exchangeRates.containsKey(c)) {
					sb.append(" (")
					.append(Money.of(CurrencyUnit.of(defaultCurrency),getConvertedAmount(c)).toString())
					.append(")");
				}
				System.out.println(sb.toString());
			}
		}
	}
	
	/**
	 * Get set of all currencies stored in this object.
	 * @return
	 */
	public Set<Currency> getCurrencySet() {
		return this.amounts.keySet();
	}
	
	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}
	
}
