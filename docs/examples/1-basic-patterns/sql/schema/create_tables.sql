
drop table Author;
create table Author (
	authorId int not null auto_increment primary key,
	name varchar(128) not null
);

drop table Publisher;
create table Publisher (
	publisherId int not null auto_increment primary key,
	name varchar(128) not null
);

drop table Book;
create table Book (
	bookId int not null auto_increment primary key,
	title varchar(128) not null,
	authorId int not null,
	publisherId int not null,
	isFavorite int not null,
	isRead int not null
);

