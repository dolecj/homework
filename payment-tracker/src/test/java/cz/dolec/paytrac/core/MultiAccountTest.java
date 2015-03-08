package cz.dolec.paytrac.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MultiAccountTest {

	private MultiAccount md;
	
	@Before
	public void ínit() {
		Currency defaultCurrency = Currency.getInstance("USD");
		this.md = AccountFactory.buildAccount(defaultCurrency);
	}
	
	@Test
	public void testInitCurrencyCurrencyBigDecimal() {
		Currency c = Currency.getInstance("CZK");
		md.initCurrency(c, new BigDecimal(100));
		assertEquals(1, md.getCurrencySet().size());
	}

	@Test
	public void testInitCurrencyCurrencyBigDecimalBigDecimal() {
		Currency c = Currency.getInstance("CZK");
		md.initCurrency(c, new BigDecimal(100));
		assertEquals(1, md.getCurrencySet().size());		
	}

	@Test
	public void testAddCurrencyBigDecimal() {
		Currency c = Currency.getInstance("CZK");
	    md.initCurrency(c, new BigDecimal(100));
		md.add(c, new BigDecimal(100));
		
	}
	
	@Test
	public void testProcessInputString() {
		md.processInput("CZK 10 0.4");
		md.processInput("CZK -100 0.4");
		md.processInput("CZK 50 0.4");
		assertEquals(md.getAmount(Currency.getInstance("CZK")).intValue(),  -40);
	}

	@Test
	public void testGetAmount() {
		assertNull(md.getAmount(Currency.getInstance("CZK")));
		Currency c = Currency.getInstance("CZK");
	    md.initCurrency(c, new BigDecimal(100));
	    assertEquals(md.getAmount(Currency.getInstance("CZK")).compareTo(new BigDecimal(100)), 0);
	}
	

	@Test
	public void testGetConvertedAmount() {
		Currency c = Currency.getInstance("CZK");
		md.initCurrency(c, new BigDecimal(100), new BigDecimal(0.05));
		assertEquals(md.getConvertedAmount(c).intValue(), 5);
	}

	@Test
	public void testSetExchangeRate() {
		Currency c = Currency.getInstance("CZK");
		md.initCurrency(c, new BigDecimal(100));
		md.setExchangeRate(c, new BigDecimal(0.05));
		assertEquals(md.getConvertedAmount(c).intValue(), 5);
	}

	@Test
	public void testPrintAccountStatus() {
		Currency c = Currency.getInstance("CZK");
		md.initCurrency(c, new BigDecimal(100), new BigDecimal(0.05));
		//md.printAccountStatus();
	}
	

	@Test
	public void testGetCurrencySet() {
		Currency c1 = Currency.getInstance("CZK");
		md.initCurrency(c1, new BigDecimal(100), new BigDecimal(0.05));
		Currency c2 = Currency.getInstance("EUR");
		md.initCurrency(c2, new BigDecimal(100));
		Set<Currency> expected = new HashSet<Currency>(Arrays.asList(Currency.getInstance("EUR"), Currency.getInstance("CZK")));
		assertTrue(expected.containsAll(md.getCurrencySet()));
		assertTrue(md.getCurrencySet().containsAll(expected));
	}

	@Test
	public void testGetDefaultCurrency() {
		assertTrue(md.getDefaultCurrency().toString().equals("USD"));
	}

}
