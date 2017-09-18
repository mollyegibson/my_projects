Datasets used:

NHTSA FARS data from 2013 and 2015, available at:
ftp://ftp.nhtsa.dot.gov/fars/

Census data, available from:
https://www.nhgis.org/

All data used is included in folder called ‘data’. Most are in the form of CSV files, except the 2013 Crash Report data I received from Professor Miller, which she parsed into a pipe-separated txt file.  The NHTSA FARS data was only available as SAS datasets, so I downloaded SAS University Edition and created a virtual machine using VirtualBox and converted it into CSV files which I could save on my local machine.

Analysis was performed using Jupyter Notebook - version 4.3.1, and database was created in PostgresQL - version 9.6.2.  I created a Python package called ‘dbconnect’ so that it was easy to connect the Notebook to the database.  Everything was kept in the same local folder on my machine.

Description of the IPython Notebooks:

crash-reports: parsing pipe-separated Crash Report 2013 data (received from Prof. Miller)

plotly-1: initial use of plotly to visualize data

groupby: testing creating a new data frame by aggregating values 

tract-census: downloaded Census data by TRACT instead of by FIPS codes from IPUMS

categorical: pivot tables, looking at breakdown of data

statsmodels: initial use of statsmodels.api Python package to run linear regressions 

box-data-4-24: NHTSA data of ALL motor vehicle accidents in Missouri instead of just fatal accidents - received from Prof. Miller

analysis-final: final linear regressions run on cleaned data

