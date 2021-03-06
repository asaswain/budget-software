Java Budget Software Program

by Asa Swain

---------------------------------------


Outline:
This software program is designed to allow you to enter a monthly ledger of income and expenses and keep track of a monthly budget 


Design Notes:
I started writing this program because I got tired of using a spreadsheet to keep track of my expenses and manage my monthly budgets. I wanted a program that would save my data in an SQL database so I could run reports on the data more easily.

Here's a brief description of the data structure I'm using to store my ledger and budget in the application.

1. General Ledger contains a list of accounts, list of monthly ledgers, and a default budget
2. The monthly ledger list contains a list of monthly ledgers
3. The monthly ledger contains a list of dated income and expense entries, and a budget of amount of for each account
4. The dated income and expense entries are sorted by date, and each entry contains a description, an account, and an amount 
5. The budgets contain a list of accounts and associated amounts
6. The accounts contain a name, a description, if it's an income or a expense, and if it is in the default budget


Writing data to SQL:

The program connects to the SQL server running on the local machine using XAMPP (which has to be running).  

The program saves the following data in a series of SQL tables:
1. A list of accounts to allow you to divide your income and expenses into different categories Food, Automotive, Fun, or Payroll Income.
2. A list of income and expense entries to keep track of each income or expense incurred during a month
3. A list of how much money you have budgeted for each account in each month


Future plans:

Currently the program just allows you to enter data using a simple text interface and saves the account list, income and expense amounts, and budgets for each month to an SQL database. 

Eventually I'd like to have the following options:
1. a GUI interface
2. reports you can run automatically (to display info like the amounts spent in each account for each month of the year)
3. an SQL parser to allow you to run SQL queries and see the results


SQL database outline:

This program depends on the following SQL databases to be set up on the local machine for it to run properly. 

A "javabudget" database with the following 4 tables: (I've listed the name and type of each column in each table)

If the SQL tables are missing than the program will generate them from scratch.

(this table store the list of accounts for income and expenses)
account_list: 
NAME 		varchar(25) - primary key
DESCRIPTION 	varchar(100)
IS_AN_EXPENSE 	varchar(1)
IS_IN_BUDGET	varchar(1)

(this table store the default budget which is used when creating ledgers for new months)
default_budget:
ACCOUNT		varchar(25) - primary key
AMOUNT		float

(this table stores a dated list of income and expenses)
general_ledger:
DATE		date  	    - primary key
DESCRIPTION	varchar(100)
ACCOUNT		varchar(25)
AMOUNT		float

(this table stores a list of budgeted amounts for each month)
monthly_budget:
MONTHYEAR	varchar(100) - primary key
MONTH		int(10)
YEAR		int(10)
ACCOUNT		varchat(25)
AMOUNT		float

