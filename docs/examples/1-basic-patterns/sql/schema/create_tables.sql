
drop table User;
create table User (
	userId int not null auto_increment primary key,
	firstName varchar(64) not null,
	lastName varchar(64) not null,
	email varchar(128) not null,
	companyId int not null,
	userType int,
	userStatus int not null
);

drop table Company;
create table Company (
	companyId int not null auto_increment primary key,
	companyName varchar(128) not null,
	companyType int
);

