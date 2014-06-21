
delete from Company;
insert into Company (companyId,companyName,companyType) values
(1,'Test Company',1),
(2,'Test Company 2',1);

delete from User;
insert into User (userId,firstName,lastName,email,companyId,userType,userStatus) values
(1,'Dave','Stankard','dcs@myserver.com',1,1,1);

