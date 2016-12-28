
This Javascribe example application runs on Tomcat 7 or later with a MySQL 5.X database back end.

This example application called "Bookshelf" makes use of basic server-side Core Patterns to create a simple web 
application for keeping track of books stored on a bookshelf.

This basic example will create an application which allows the user to enter information about books stored on 
the bookshelf.  The user can browse books, mark them as read, and discard (delete) them.

The application uses a server-side MVC 

The following must be installed to build the sample application:
	MySQL 5.X
	Tomcat 7 or later
	Ant 1.8.X

You must do the following:

In MySQL, set table names to "0 - Store as created, Case Sensitive"
	In my.ini set "lower_case_table_names=0".  
	Alternatively, in MySQL Admin UI go to "Startup Variables" -> "Advanced".  Set "Make table names" to "0 - Store as Created, Case Sensitive". 
Create a MySQL schema on your local environment called "bookshelf"
Create a MySQL user called js_example with password as "password".  Assign this user all permissions on schema "js_example1".
In build.properties, set appropriate values for these properties:
	tomcat.home - Loction of Tomcat 7.0.X
	build.root - Directory to build the application in.  Example: c:\build\example1



MySQL;
create database bookshelf;
CREATE USER 'js_example'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON bookshelf.* to js_example@localhost;
FLUSH PRIVILEGES;

