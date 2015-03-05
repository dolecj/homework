package cz.dolec.paytrac.core;

import java.util.Currency;

public class AccountFactory {

	public static MultiAccount buildAccount(Currency defaultCurrency) {
		return new MultiAccount(defaultCurrency);
	}
}
