CREATE TABLE Users(
	userId int not null IDENTITY PRIMARY KEY, 
	firstName varchar(64) not null,
	lastName varchar(64) not null,
	email varchar(64) not null unique,
	birthDate date,  
	joinDate date,
	loggedIn bit default 0,
	loginId int,
	deleted bit default 0
);