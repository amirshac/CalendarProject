create table UsersEvents
(
userId int not null,
eventId int not null,
primary key (userId,eventId),
FOREIGN KEY (eventId) REFERENCES Events(eventId),
FOREIGN KEY (userId) REFERENCES Users(userId)
)