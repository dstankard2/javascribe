
This example shows many of the basic bundled patterns for Javascribe, and 
their integration into a simple web application.  The domain-level logic is 
written by hand and depends on generated back end code.  Web controller and 
web service code is dependent on domain-level logic.

The following must be installed to build the sample application:
	MySQL 5.X
	Tomcat 7 or later
	Ant 1.8.X

You must do the following:

In MySQL, set table names to "0 - Store as created, Case Sensitive"
	In my.ini set "lower_case_table_names=0".  
	Alternatively, in MySQL Admin UI go to "Startup Variables" -> "Advanced".  Set "Make table names" to "0 - Store as Created, Case Sensitive". 
Create a MySQL schema on your local environment called "js_example1"
Create a MySQL user called js_example with password as "password".  Assign this user all permissions on schema "js_example1".
In build.properties, set appropriate values for these properties:
	tomcat.home - Loction of Tomcat 7.0.X
	build.root - Directory to build the application in.  Example: c:\build\example1

