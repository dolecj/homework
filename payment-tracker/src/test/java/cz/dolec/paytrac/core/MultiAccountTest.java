package cz.dolec.paytrac.core;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

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

//	@Test
//	public void testInitCurrencyCurrencyBigDecimalBigDecimal() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddCurrencyBigDecimal() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testAddString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetAmount() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetConvertedAmount() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetExchangeRate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPrintAccountStatus() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetCurrencySet() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetDefaultCurrency() {
//		fail("Not yet implemented");
//	}

}
