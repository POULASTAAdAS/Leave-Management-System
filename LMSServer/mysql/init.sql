-- noinspection SqlNoDataSourceInspectionForFile

create
database if not exists LMS;
use LMS;

-- these schemas are directly connected to database object in ktor application any changes to this values will cause fatal error

create table Department
(
    id   Int primary key auto_increment,
    name varchar(200) not null unique
);

create Table Principal
(
    id         Int primary key auto_increment,
    `name`     text         not null,
    email      varchar(255) not null unique,
    profilePic varchar(300) default (null)
);

Create Table HeadClark
(
    id         Int primary key auto_increment,
    `name`     text         not null,
    email      varchar(255) not null unique,
    profilePic varchar(300) default (null)
);

Create Table TeacherType
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table Teacher
(
    id            Int primary key auto_increment,
    email         varchar(255) not null unique,
    emailVerified bool default (false)
);

Create Table DepartmentHead
(
    id           Int primary key auto_increment,
    teacherId    Int references Teacher (id) on delete cascade,
    departmentId Int references Department (id) on delete cascade
);


Create Table Designation
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table DesignationTeacherTypeRelation
(
    designationId int references designation (id) on delete cascade,
    teacherTypeId int references teachertype (id) on delete cascade,
    primary key (designationId, teacherTypeId)
);

Create Table Qualification
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table AddressType
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table `Path`
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table LeaveType
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table `Status`
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table PendingEnd
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table LeaveAction
(
    id     Int primary key auto_increment,
    `type` varchar(400) not null unique
);

Create Table TeacherDetails
(
    teacherId       Int references Teacher (id) on delete cascade,
    teacherTypeId   Int references TeacherType (id) on delete cascade,
    profilePic      varchar(300) default (null),
    hrmsId          varchar(20)        not null unique,
    `name`          varchar(100)       not null,
    phone_1         varchar(10) unique not null,
    phone_2         varchar(10)  default (null),
    bDate           Date               not null,
    gender          varchar(1)         not null,
    designationId   Int references Designation (id) on delete cascade,
    departmentId    Int references Department (id) on delete cascade,
    joiningDate     Date               not null,
    qualificationId Int references Qualification (id) on delete cascade,
    exp             varchar(20)  default ("0"),
    primary key (teacherId, teacherTypeId)
);

Create Table TeacherAddress
(
    teacherId     Int references Teacher (id) on delete cascade,
    addressTypeId Int references AddressType (id) on delete cascade,
    houseNumb     text not null,
    street        text not null,
    city          text not null default ('Kolkata'),
    zip           Int  not null,
    state         text not null default ('West Bengal'),
    country       text not null default ('India'),
    primary key (teacherId, addressTypeId)
);

Create Table LeaveReq
(
    id                 BigInt primary key auto_increment,
    teacherId          Int references Teacher (id) on delete cascade,
    leaveTypeId        Int references LeaveType (id) on delete cascade,
    reqDate            TimeStamp not null,
    fromDate           Date      not null,
    toDate             Date      not null,
    reason             text      not null,
    addressDuringLeave text      not null,
    pathId             Int references `Path` (id) on delete cascade,
    doc                varchar(255) default (null)
);


Create Table LeaveStatus
(
    leaveId      BigInt primary key references LeaveReq (id) on delete cascade,
    approveDate  date          default null,
    statusId     Int references `Status` (id) on delete cascade,
    pendingEndId Int references PendingEnd (id) on delete cascade,
    cause        text not null default (""),
    actionId     Int           default (null) references LeaveAction (id) on delete cascade,
    departmentId int references Department (id) on delete cascade
);

Create Table LeaveBalance
(
    teacherId     Int references Teacher (id) on delete cascade,
    teacherTypeId Int references TeacherType (id) on delete cascade,
    leaveTypeId   Int references LeaveType (id) on delete cascade,
    leaveBalance Double not null,
    `year`        int not null,
    primary key (teacherId, teacherTypeId, leaveTypeId)
);


create table TeacherProfilePic
(
    teacherId  Int references Teacher (id) on delete cascade,
    name       varchar(200) not null,
    profilePic blob         not null,
    primary key (teacherId)
);


-- add dummy data

insert into principal value (1 , 'Principal' , 'principalbgc56@gmail.com' , null); -- change email as your need
insert into headclark value (1 ,'Head Clark' , 'headclerkbgc56@gmail.com' , null); -- change email as your need

insert
ignore into Department values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
(1,'ASP(Advertisement and Sales Promotion)'),
(2,'Bengali'),
(3,'Botany'),
(4,'Chemistry'),
(5,'Commerce'),
(6,'Computer Science'),
(7,'Economics'),
(8,'Education'),
(9,'Electronic Science'),
(10,'English'),
(11,'Food & Nutrition'),
(12,'Environmental Science'),
(13,'Geography'),
(14,'Hindi'),
(15,'History'),
(16,'Journalism & Mass Com.'),
(17,'Mathematics'),
(18,'Philosophy'),
(19,'Physical Education'),
(20,'Physics'),
(21,'Physiology'),
(22,'Sanskrit'),
(23,'Sociology'),
(24,'Urdu'),
(25,'Zoology'),
(26,'Other');


insert
ignore into TeacherType values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
(1 ,'Permenent'),
(2 ,'SACT'),
(3 ,'Non Teach');

insert
ignore into teacher (email) values -- change email as your need
('sacteacherone@gmail.com'),
('poulastaadas2@gmail.com'),
('nonteach56@gmail.com'),
('permanentteacherone@gmail.com');

insert
ignore into DepartmentHead (teacherId,departmentId ) values
(2 ,6); -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error

insert
ignore into Designation (`type`) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Assistant Professor-I'),
('Assistant Professor-II'),
('Assistant Professor-III'),
('Associate Professor'),
('SACT-I'),
('SACT-II'),
('Lab. Attendant'),
('Accountant'),
('Elec. Cum Caretaker'),
('Clerk'),
('Typist'),
('Lab. Attendant'),
('Gen./Pump Operator Cum Mechanic'),
('Mali'),
('Guard'),
('Peon');


insert
ignore into DesignationTeacherTypeRelation values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
(1,1),
(2,1),
(3,1),
(4,1),
(5,2),
(6,2),

(7,1),
(8,1),
(9,1),
(10,1),
(11,1),
(12,1),
(13,1),
(14,1),
(15,1);


insert
ignore into Qualification (`type`) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Dr.'),
('Ph.D'),
('M.Tech'),
('M.Com'),
('MBA'),
('MSC'),
('MCA'),
('MLib'),
('Other');




insert
ignore into AddressType (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('PRESENT'),
('HOME');

insert
ignore into `Path` (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Department Head'),
('Head Clark'),
('Principal');


insert
ignore into LeaveType (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Casual Leave'),
('Medical Leave'),
('Study Leave'),
('Earned Leave'),
('On Duty Leave'),
('Special Study Leave'),
('Maternity Leave'),
('Quarintine Leave'),
('Commuted Leave'),
('Extraordinary Leave'),
('Compensatory Leave'),
('Leave Not Due'),
('Special Disability Leave');


insert
ignore into Status (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Approved'),
('Pending'),
('Rejected'),
('Accepeted');


insert
ignore into PendingEnd (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Principal Level'),
('Department Level'),
('Head Clark Level'),
('Not Pending');


insert
ignore into LeaveAction (type) values -- this data are directly connected to server department object in ktor project any changes to this values will cause fatal error
('Approve'),
('Reject'),
('Forward');