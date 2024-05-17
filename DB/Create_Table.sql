create database LMSCopy;

create table Department(
id Int primary key auto_increment,
name varchar(200)	not null unique
);

create Table Principal(
id Int primary key auto_increment,
`name` text not null,
email varchar(255) not null unique
);

Create Table HeadClark(
id Int primary key auto_increment,
`name` text not null,
email varchar(255) not null unique
);

Create Table TeacherType(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table Teacher(
id Int primary key auto_increment,
email varchar(255) not null unique,
teacherTypeId Int references TeacherType(id) on delete cascade,
emailVerified bool default(false)
);

Create Table DepartmentHead(
id Int primary key auto_increment,
teacherId Int references Teacher(id) on delete cascade,
departmentId Int references Department(id) on delete cascade
);


Create Table Designation(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table Qualification(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table AddressType(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table `Path`(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table LeaveType(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table `Status`(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table PendingEnd(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);

Create Table LeaveAction(
id Int primary key auto_increment,
`type` varchar(400) not null unique
);




Create Table TeacherDetails( -- map with thercahrId with email from req
teacherId Int primary key references Teacher(id) on delete cascade,
hrmsId varchar(40) not null unique,
`name` text not null,
phone_1 varchar(10) not null,
phone_2 varchar(10) default(null),
bDate Date not null,
gender varchar(1) not null ,
designationId Int references Designation(id) on delete cascade,
departmentId Int references Department(id) on delete cascade,
joiningDate Date not null,
qualificationId Int references Qualification(id) on delete cascade
);

Create Table TeacherAddress(
teacherId Int references Teacher(id) on delete cascade,
addressTypeId Int references AddressType(id) on delete cascade,
houseNumb text not null,
street text not null,
city text not null default('Kolkata'),
zip Int not null,
state text not null default('West Bengal'),
country text not null default ('India'),
primary key (teacherId , addressTypeId)
);

Create Table LeaveReq(
id BigInt primary key auto_increment,
teacherId Int references Teacher(id) on delete cascade,
leaveTypeId Int references LeaveType(id) on delete cascade,
reqDate TimeStamp not null,
fromDate Date not null,
toDate Date not null,
reason text not null,
addressDuringLeave text not null,
pathId Int references  `Path`(id) on delete cascade,
doc blob default(null)
);


Create Table LeaveStatus(
leaveId BigInt primary key references LeaveReq(id) on delete cascade,
statusId Int references `Status`(id) on delete cascade,
pendingEndId Int references PendingEnd(id) on delete cascade,
cause text not null default(""),
actionId Int references LeaveAction(id) on delete cascade
);


Create Table LeaveBalance(
teacherId Int references Teacher(id) on delete cascade,
teacherTypeId Int references TeacherType(id) on delete cascade,
leaveTypeId Int references LeaveType(id) on delete cascade,
leaveBalance Double not null,
`year` Year DEFAULT(CURRENT_DATE),
primary key (teacherId ,teacherTypeId, leaveTypeId)
);
























































