CREATE TABLE Users(
	userId int not null IDENTITY PRIMARY KEY, 
	firstName varchar(64) not null,
	lastName varchar(64) not null,
	email varchar(64) not null unique,
	birthDate date,  
	joinDate date,
	deleted bit default 0
);

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

CREATE TABLE Notifications(
	notificationId int not null IDENTITY PRIMARY KEY, 
	eventId	int not null FOREIGN KEY REFERENCES Events(eventId),
	title	varchar(64) not null,
	message	varchar(64),
	eventTime DateTime,
	reminderUnit varchar(64),
	reminderQuantity int,
	deleted bit default 0
);

create table UsersEvents
(
userId int not null,
eventId int not null,
primary key (userId,eventId),
FOREIGN KEY (eventId) REFERENCES Events(eventId),
FOREIGN KEY (userId) REFERENCES Users(userId)
);