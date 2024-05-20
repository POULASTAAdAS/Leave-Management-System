insert into principal value (1 , 'Principal' , 'principalbgc56@gmail.com');
insert into headclark value (1 ,'Head Clark' , 'headclerkbgc56@gmail.com');

insert into Department values
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
(25,'Zoology');


insert ignore into TeacherType values
(1 ,'Permenent'),
(2 ,'SCAT');

insert ignore into teacher (email) values
('sacteacherone@gmail.com'),
('poulastaadas2@gmail.com'),
('permanentteacherone@gmail.com');

insert ignore into DepartmentHead (teacherId,departmentId ) values
(2 ,6);

insert ignore into Designation (`type`) values
('Assistant Professor-I'),
('Assistant Professor-II'),
('Assistant Professor-III'),
('Associate Professor'),
('SCAT-I'),
('SCAT-II');


insert ignore into Qualification (`type`) values
('Dr.'),
('Ph.D'),
('M.Tech'),
('M.Com'),
('MBA'),
('MSC'),
('MCA'),
('MLib');



insert ignore into AddressType (type) values
('PRESENT'),
('HOME');

insert ignore into `Path` (type) values
('Department Head'),
('Head Clark'),
('Principal');


insert ignore into LeaveType (type) values
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


insert ignore into Status (type) values
('Approved'),
('Pending'),
('Rejected'),
('Accepeted');


insert ignore into PendingEnd (type) values
('Principal Level'),
('Department Level'),
('Not Pending');


insert ignore into LeaveAction (type) values
('Approve'),
('Reject'),
('Forword');



































