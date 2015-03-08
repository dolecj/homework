----------------------------------------------------------------------------
PAYMENT TRACKER
----------------------------------------------------------------------------
** build:
mvn install

----------------------------------------------------------------------------
** usage:
 
java -jar payment-tracker-0.0.1-jar-with-dependencies.jar [-c <defaultCurrency>] [-f <iniFilePath>] [-h]
 -c <defaultCurrency>   Default currency code
 -f <iniFilePath>       Init account file name
 -h                     Print help and exit


examples:
* Start program with default currency USD
java -jar payment-tracker-0.0.1-jar-with-dependencies.jar

* Start program and specify input file with account status
java -jar payment-tracker-0.0.1-jar-with-dependencies.jar -f .\classes\initfile.txt

* Start program with default currency CZK
java -jar payment-tracker-0.0.1-jar-with-dependencies.jar -c CZK

----------------------------------------------------------------------------
** how to use running program:

commands:
<currency> <amount> [exchangeRate]  add amount, specify exchangeRate related to default currency
print                               prints status (prints only non-zero currencies status 
                                    converted value is shown in brackets, only for currencies with exchange rate set
quit                                stop the program


examples:
EUR 100                             add 100 EUR
EUR -100                            subtract 100 EUR
CZK 50.40 0.04016                   add 50.40 CZK and set new exchange rate
GBP 0 1.5185                        set new exchange rate for GBP


----------------------------------------------------------------------------
** known issues

- printed account status is not sorted
- exchangeRate should not be set in MultiAccount object itself, but somewhere else
- some currencies are not supported (e.g. RMB) - extend Joda-Money org/joda/money/MoneyData.csv
