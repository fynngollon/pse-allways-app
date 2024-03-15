## How to install the MySql-Server

1. Install MySQL with the MySQL Installer - Community.
1.1 Type and Networking
Select the Config type with Development Computer
Select TCI/IP on Port 3306 and X Protocol Port 33060
Check: Open Windows Firewall ports for network access
1.2 Authentication Method
Check: Use Strong Password Encryption for Authentication (RECOMMENDED)
1.3 Account and Roles
Select Password and save it for later
1.4 Windows Services
Check everything and Check: Standard System Account
Give a Windows Service Name
1.5 Apply configuration
Press Execute
Now the MySQL Server is installed on your PC
We need a User Interface to interact with it
2. Install MySQL Workbench
2.1 Install MySQL Workbench
2.2 Connect it with your Server you installed 
3. Prepare the Database
3.1 Create Schemas
Right Click in the Schema Slide and create two new Schemas
Name one allways-app and the other allways-app-accounts
3.2 Prepare the account database
Create 3 Tables:
3.2.1 Name it tblaccounts, add a email as a varchar(100) with primary-key and not null and unique
add pseudonym as a varchar(100) not null
add passwordHash as a varchar(100) not null
add passwordSalt as a varchar(100) not null
3.2.2 Name it tblhouseholdquestionnaire, add a jsonString as a varchar(500) with primary-key and not null
3.2.3 Name it tblprofilequestionnaire, add a jsonString as a varchar(500) with primary-key and not null
4. Add RemoteUser Account
4.1 Log in MySQL Workbench and go to Administration
4.2 Add A Account
Press on Add Account
Name The Account RemoteUser
Authentication Type is standard
Limit to Hosts Matching is %
Select the Password Allways#App and confirm it.
Apply the Changes
4.3 Give the RemoteUser Access to the Database
Go to Schema Privileges and select Add Entry
Select All Schemas % and give all Privileges
5. Add Remote Connection
5.1 Disconnect from Root Connection and press add Connection
5.2 Select the Details
Name it Remote Connection
Select the Connection Method: Standard (TCP/IP)
Host Name is the IP-Address of the Computer u installed the MySQL-Server on
The Port is 3306
User Name is RemoteUser
Password is Allways#App
Test the connection and if its successful press ok (if not go to 7.)
6. Change Code to connect to the new Server
6.1 Go to the BaseNetworkDataSource and change in the functions : createRemoteAccountConnection
and createRemoteDataConnection the Url of the server to the Url of the new server. If you
changed the Name of the User or used a different password change it here as well.
7. Change Windows Firewall settings
7.1 Open Windows Defender Firewall
7.2 Select Erweiterte Einstellungen and go to outgoing rules. Press add new rule
7.3 add a new rule
Select Port
Select TCP and Bestimmte Remoteports and add 3306
Select Verbindung Zulassen
Check all boxes
Name the Connection Port 3306
Press Ok

With these steps the server should be online and accessible with other devices. If the router
blocks the entry for port 3306 the devices have to be in the same Wifi or Internet access.

