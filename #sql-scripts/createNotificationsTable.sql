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