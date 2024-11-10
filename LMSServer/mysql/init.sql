-- noinspection SqlNoDataSourceInspectionForFile

create
database if not exists LMS;
use
lms;

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
insert into headclark value (1 ,'Head Clark' , 'headclerkbgc56@gmail.com'); -- change email as your need

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



-- placeholders to test endpoints deleting this will not cause any issue

insert
ignore into teacher (email) values
('sacBeng@gmail.com'),
('permBeng@gmail.com'),
('hodBeng@gmail.com'),


('sacChem@gmail.com'),
('permChem@gmail.com'),
('hodChem@gmail.com'),

('sacChem@gmail.com'),
('permChem@gmail.com'),
('hodChem@gmail.com'),

('sacMath@gmail.com'),
('permMath@gmail.com'),
('hodMath@gmail.com'),


('sacPhy@gmail.com'),
('permPhy@gmail.com'),
('hodPhy@gmail.com'),

('sacHindi@gmail.com'),
('permhindi@gmail.com'),
('hodHindi@gmail.com'),

('sacCom@gmail.com'),
('permCom@gmail.com'),
('hodCom@gmail.com');

insert into teacherdetails
values (16, 2, null, 193485, 'sac beng', 9876549876, null, '2003-01-13', 'M', 5, 2, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (17, 1, null, 293485, 'perm beng', 9876149876, null, '2003-01-13', 'M', 1, 2, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (18, 1, null, 393485, 'hod beng', 9876349876, null, '2003-01-13', 'M', 1, 2, '2015-07-22', 7, '8 Y/11 M/12 D'),

       (19, 2, null, 493485, 'sac chem', 1876549876, null, '2003-01-13', 'M', 5, 4, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (20, 1, null, 593485, 'perm chem', 2876149876, null, '2003-01-13', 'M', 1, 4, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (21, 1, null, 693485, 'hod chem', 3876349876, null, '2003-01-13', 'M', 1, 4, '2015-07-22', 7, '8 Y/11 M/12 D'),

       (22, 2, null, 793485, 'sac math', 4876549876, null, '2003-01-13', 'M', 5, 17, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (23, 1, null, 893485, 'perm math', 5876149876, null, '2003-01-13', 'M', 1, 17, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (24, 1, null, 993485, 'hod math', 6876349876, null, '2003-01-13', 'M', 1, 17, '2015-07-22', 7, '8 Y/11 M/12 D'),

       (25, 2, null, 213485, 'sac phy', 7876549876, null, '2003-01-13', 'M', 5, 20, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (26, 1, null, 223485, 'perm phy', 8876149876, null, '2003-01-13', 'M', 1, 20, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (27, 1, null, 233485, 'hod phy', 9176349876, null, '2003-01-13', 'M', 1, 20, '2015-07-22', 7, '8 Y/11 M/12 D'),

       (28, 2, null, 243485, 'sac hindi', 7876549871, null, '2003-01-13', 'M', 5, 14, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (29, 1, null, 253485, 'perm hindi', 8876149872, null, '2003-01-13', 'M', 1, 14, '2015-07-22', 7,
        '8 Y/11 M/12 D'),
       (30, 1, null, 263485, 'hod hindi', 9176349873, null, '2003-01-13', 'M', 1, 14, '2015-07-22', 7, '8 Y/11 M/12 D'),

       (31, 2, null, 273485, 'sac com', 7876549874, null, '2003-01-13', 'M', 5, 5, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (32, 1, null, 283485, 'perm com', 8876149875, null, '2003-01-13', 'M', 1, 5, '2015-07-22', 7, '8 Y/11 M/12 D'),
       (33, 1, null, 203485, 'sac hod', 9176349877, null, '2003-01-13', 'M', 1, 5, '2015-07-22', 7, '8 Y/11 M/12 D');



insert into departmenthead (teacherId, departmentid)
values (18, 2),
       (21, 4),
       (24, 17),
       (27, 20),
       (30, 14),
       (33, 5);

select *
from leavebalance;

insert into leavebalance
values (16, 2, 1, 14, 2024),
       (16, 2, 2, 14, 2024),
       (16, 2, 5, 14, 2024),
       (16, 2, 8, 14, 2024),

       (17, 1, 1, 14, 2024),
       (17, 1, 2, 14, 2024),
       (17, 1, 3, 14, 2024),
       (17, 1, 4, 14, 2024),
       (17, 1, 5, 14, 2024),
       (17, 1, 6, 14, 2024),
       (17, 1, 8, 14, 2024),
       (17, 1, 9, 14, 2024),
       (17, 1, 10, 14, 2024),
       (17, 1, 11, 14, 2024),
       (17, 1, 12, 14, 2024),
       (17, 1, 13, 14, 2024),

       (18, 1, 1, 14, 2024),
       (18, 1, 2, 14, 2024),
       (18, 1, 3, 14, 2024),
       (18, 1, 4, 14, 2024),
       (18, 1, 5, 14, 2024),
       (18, 1, 6, 14, 2024),
       (18, 1, 8, 14, 2024),
       (18, 1, 9, 14, 2024),
       (18, 1, 10, 14, 2024),
       (18, 1, 11, 14, 2024),
       (18, 1, 12, 14, 2024),
       (18, 1, 13, 14, 2024),


       (19, 2, 1, 14, 2024),
       (19, 2, 2, 14, 2024),
       (19, 2, 5, 14, 2024),
       (19, 2, 8, 14, 2024),

       (20, 1, 1, 14, 2024),
       (20, 1, 2, 14, 2024),
       (20, 1, 3, 14, 2024),
       (20, 1, 4, 14, 2024),
       (20, 1, 5, 14, 2024),
       (20, 1, 6, 14, 2024),
       (20, 1, 8, 14, 2024),
       (20, 1, 9, 14, 2024),
       (20, 1, 10, 14, 2024),
       (20, 1, 11, 14, 2024),
       (20, 1, 12, 14, 2024),
       (20, 1, 13, 14, 2024),

       (21, 1, 1, 14, 2024),
       (21, 1, 2, 14, 2024),
       (21, 1, 3, 14, 2024),
       (21, 1, 4, 14, 2024),
       (21, 1, 5, 14, 2024),
       (21, 1, 6, 14, 2024),
       (21, 1, 8, 14, 2024),
       (21, 1, 9, 14, 2024),
       (21, 1, 10, 14, 2024),
       (21, 1, 11, 14, 2024),
       (21, 1, 12, 14, 2024),
       (21, 1, 13, 14, 2024),


       (22, 2, 1, 14, 2024),
       (22, 2, 2, 14, 2024),
       (22, 2, 5, 14, 2024),
       (22, 2, 8, 14, 2024),

       (23, 1, 1, 14, 2024),
       (23, 1, 2, 14, 2024),
       (23, 1, 3, 14, 2024),
       (23, 1, 4, 14, 2024),
       (23, 1, 5, 14, 2024),
       (23, 1, 6, 14, 2024),
       (23, 1, 8, 14, 2024),
       (23, 1, 9, 14, 2024),
       (23, 1, 10, 14, 2024),
       (23, 1, 11, 14, 2024),
       (23, 1, 12, 14, 2024),
       (23, 1, 13, 14, 2024),

       (24, 1, 1, 14, 2024),
       (24, 1, 2, 14, 2024),
       (24, 1, 3, 14, 2024),
       (24, 1, 4, 14, 2024),
       (24, 1, 5, 14, 2024),
       (24, 1, 6, 14, 2024),
       (24, 1, 8, 14, 2024),
       (24, 1, 9, 14, 2024),
       (24, 1, 10, 14, 2024),
       (24, 1, 11, 14, 2024),
       (24, 1, 12, 14, 2024),
       (24, 1, 13, 14, 2024),


       (25, 2, 1, 14, 2024),
       (25, 2, 2, 14, 2024),
       (25, 2, 5, 14, 2024),
       (25, 2, 8, 14, 2024),

       (26, 1, 1, 14, 2024),
       (26, 1, 2, 14, 2024),
       (26, 1, 3, 14, 2024),
       (26, 1, 4, 14, 2024),
       (26, 1, 5, 14, 2024),
       (26, 1, 6, 14, 2024),
       (26, 1, 8, 14, 2024),
       (26, 1, 9, 14, 2024),
       (26, 1, 10, 14, 2024),
       (26, 1, 11, 14, 2024),
       (26, 1, 12, 14, 2024),
       (26, 1, 13, 14, 2024),

       (27, 1, 1, 14, 2024),
       (27, 1, 2, 14, 2024),
       (27, 1, 3, 14, 2024),
       (27, 1, 4, 14, 2024),
       (27, 1, 5, 14, 2024),
       (27, 1, 6, 14, 2024),
       (27, 1, 8, 14, 2024),
       (27, 1, 9, 14, 2024),
       (27, 1, 10, 14, 2024),
       (27, 1, 11, 14, 2024),
       (27, 1, 12, 14, 2024),
       (27, 1, 13, 14, 2024),


       (28, 2, 1, 14, 2024),
       (28, 2, 2, 14, 2024),
       (28, 2, 5, 14, 2024),
       (28, 2, 8, 14, 2024),

       (29, 1, 1, 14, 2024),
       (29, 1, 2, 14, 2024),
       (29, 1, 3, 14, 2024),
       (29, 1, 4, 14, 2024),
       (29, 1, 5, 14, 2024),
       (29, 1, 6, 14, 2024),
       (29, 1, 8, 14, 2024),
       (29, 1, 9, 14, 2024),
       (29, 1, 10, 14, 2024),
       (29, 1, 11, 14, 2024),
       (29, 1, 12, 14, 2024),
       (29, 1, 13, 14, 2024),

       (30, 1, 1, 14, 2024),
       (30, 1, 2, 14, 2024),
       (30, 1, 3, 14, 2024),
       (30, 1, 4, 14, 2024),
       (30, 1, 5, 14, 2024),
       (30, 1, 6, 14, 2024),
       (30, 1, 8, 14, 2024),
       (30, 1, 9, 14, 2024),
       (30, 1, 10, 14, 2024),
       (30, 1, 11, 14, 2024),
       (30, 1, 12, 14, 2024),
       (30, 1, 13, 14, 2024),

       (31, 2, 1, 14, 2024),
       (31, 2, 2, 14, 2024),
       (31, 2, 5, 14, 2024),
       (31, 2, 8, 14, 2024),

       (32, 1, 1, 14, 2024),
       (32, 1, 2, 14, 2024),
       (32, 1, 3, 14, 2024),
       (32, 1, 4, 14, 2024),
       (32, 1, 5, 14, 2024),
       (32, 1, 6, 14, 2024),
       (32, 1, 8, 14, 2024),
       (32, 1, 9, 14, 2024),
       (32, 1, 10, 14, 2024),
       (32, 1, 11, 14, 2024),
       (32, 1, 12, 14, 2024),
       (32, 1, 13, 14, 2024),

       (33, 1, 1, 14, 2024),
       (33, 1, 2, 14, 2024),
       (33, 1, 3, 14, 2024),
       (33, 1, 4, 14, 2024),
       (33, 1, 5, 14, 2024),
       (33, 1, 6, 14, 2024),
       (33, 1, 8, 14, 2024),
       (33, 1, 9, 14, 2024),
       (33, 1, 10, 14, 2024),
       (33, 1, 11, 14, 2024),
       (33, 1, 12, 14, 2024),
       (33, 1, 13, 14, 2024);

insert into leavereq (teacherid, leaveTypeId, reqDate, fromDate, toDate, reason, addressDuringLeave, pathId, doc)
values (16, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (16, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (16, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (17, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (17, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (17, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (18, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (18, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (18, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null),


       (19, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (19, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (19, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (20, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (20, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (20, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (21, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (21, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (21, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null),


       (22, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (22, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (22, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (23, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (23, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (23, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (24, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (24, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (24, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null),


       (25, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (25, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (25, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (26, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (26, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (26, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (27, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (27, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (27, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null),


       (28, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (28, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (28, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (29, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (29, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (29, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (30, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (30, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (30, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null),


       (31, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (31, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (31, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (32, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 1, null),
       (32, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 1, null),
       (32, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 1, null),

       (33, 1, '2024-07-24 14:38:47', '2024-07-31', '2024-07-31', 'dummy', 'Present Address', 3, null),
       (33, 1, '2024-07-24 14:38:47', '2024-08-31', '2024-08-31', 'dummy', 'Present Address', 3, null),
       (33, 1, '2024-07-24 14:38:47', '2024-09-31', '2024-09-31', 'dummy', 'Present Address', 3, null);


insert into leavestatus
values (117, 1, 3, '', 1, 2, '2024-08-02'),
       (118, 1, 3, '', 1, 2, '2024-08-02'),
       (119, 1, 3, '', 1, 2, '2024-08-02'),

       (120, 1, 3, '', 1, 2, '2024-08-02'),
       (121, 1, 3, '', 1, 2, '2024-08-02'),
       (122, 1, 3, '', 1, 2, '2024-08-02'),

       (123, 1, 3, '', 1, 2, '2024-08-02'),
       (124, 1, 3, '', 1, 2, '2024-08-02'),
       (125, 1, 3, '', 1, 2, '2024-08-02'),


       (126, 1, 3, '', 1, 4, '2024-08-02'),
       (127, 1, 3, '', 1, 4, '2024-08-02'),
       (128, 1, 3, '', 1, 4, '2024-08-02'),


       (129, 1, 3, '', 1, 4, '2024-08-02'),
       (130, 1, 3, '', 1, 4, '2024-08-02'),
       (131, 1, 3, '', 1, 4, '2024-08-02'),


       (132, 1, 3, '', 1, 4, '2024-08-02'),
       (133, 1, 3, '', 1, 4, '2024-08-02'),
       (134, 1, 3, '', 1, 4, '2024-08-02'),


       (135, 1, 3, '', 1, 17, '2024-08-02'),
       (136, 1, 3, '', 1, 17, '2024-08-02'),
       (137, 1, 3, '', 1, 17, '2024-08-02'),


       (138, 1, 3, '', 1, 17, '2024-08-02'),
       (139, 1, 3, '', 1, 17, '2024-08-02'),
       (140, 1, 3, '', 1, 17, '2024-08-02'),


       (141, 1, 3, '', 1, 17, '2024-08-02'),
       (142, 1, 3, '', 1, 17, '2024-08-02'),
       (143, 1, 3, '', 1, 17, '2024-08-02'),


       (144, 1, 3, '', 1, 20, '2024-08-02'),
       (145, 1, 3, '', 1, 20, '2024-08-02'),
       (146, 1, 3, '', 1, 20, '2024-08-02'),


       (147, 1, 3, '', 1, 20, '2024-08-02'),
       (148, 1, 3, '', 1, 20, '2024-08-02'),
       (149, 1, 3, '', 1, 20, '2024-08-02'),


       (150, 1, 3, '', 1, 20, '2024-08-02'),
       (151, 1, 3, '', 1, 20, '2024-08-02'),
       (152, 1, 3, '', 1, 20, '2024-08-02'),


       (153, 1, 3, '', 1, 14, '2024-08-02'),
       (154, 1, 3, '', 1, 14, '2024-08-02'),
       (155, 1, 3, '', 1, 14, '2024-08-02'),


       (156, 1, 3, '', 1, 14, '2024-08-02'),
       (157, 1, 3, '', 1, 14, '2024-08-02'),
       (158, 1, 3, '', 1, 14, '2024-08-02'),


       (159, 1, 3, '', 1, 14, '2024-08-02'),
       (160, 1, 3, '', 1, 14, '2024-08-02'),
       (161, 1, 3, '', 1, 14, '2024-08-02'),


       (162, 1, 3, '', 1, 5, '2024-08-02'),
       (163, 1, 3, '', 1, 5, '2024-08-02'),
       (164, 1, 3, '', 1, 5, '2024-08-02'),


       (165, 1, 3, '', 1, 5, '2024-08-02'),
       (166, 1, 3, '', 1, 5, '2024-08-02'),
       (167, 1, 3, '', 1, 5, '2024-08-02'),


       (168, 1, 3, '', 1, 5, '2024-08-02'),
       (169, 1, 3, '', 1, 5, '2024-08-02'),
       (170, 1, 3, '', 1, 5, '2024-08-02');
