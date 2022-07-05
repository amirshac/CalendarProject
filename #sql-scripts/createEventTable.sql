CREATE TABLE Events(
	eventId int not null IDENTITY PRIMARY KEY, 
	ownerId int not null FOREIGN KEY REFERENCES Users(userId),
	title varchar(64) not null,
	starting datetime not null,
	ending datetime,  
	allDay bit default 0,
	address varchar(64),
	description varchar(100),
	repeatingOptions varchar(64),
	deleted bit default 0
);