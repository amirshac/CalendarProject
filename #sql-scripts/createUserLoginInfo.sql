CREATE TABLE UserLoginInfo(
	loginId int not null IDENTITY PRIMARY KEY, 
	userId	int not null FOREIGN KEY REFERENCES Users(userId),
	email	varchar(64),
	endPoint varchar(1024),
	p256dhKey varchar(1024),
	auth varchar(1024),
	deleted bit default 0
);