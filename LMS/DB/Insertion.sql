insert into principal value (1 , 'Principal' , 'principalbgc56@gmail.com');
insert into headclark value (1 ,'Head Clark' , 'headclerkbgc56@gmail.com');

insert into Department (`name`) values
('Bengali'),
('ASP(Advertisement and Sales Promotion)'),
('Botany'),
('Chemistry'),
('Commerce'),
('Computer Science'),
('Economics'),
('Education'),
('Electronic Science'),
('English'),
('Food & Nutrition'),
('Environmental Science'),
('Geography'),
('Hindi'),
('History'),
('Journalism & Mass Com.'),
('Mathematics'),
('Philosophy'),
('Physical Education'),
('Physics'),
('Physiology'),
('Sanskrit'),
('Sociology'),
('Urdu'),
('Zoology');


insert ignore into TeacherType values
(1 ,'Permenent'),
(2 ,'SCAT');

insert ignore into teacher (email , teacherTypeId) values
('sacteacherone@gmail.com' , 2),
('poulastaadas2@gmail.com' , 1),
('permanentteacherone@gmail.com' , 1);

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

insert ignore into TeacherDetails values
(2, '948239' , 'Poulastaa Das' , '9330361292' ,null , '2003-01-13' , 1 , 6,'2023-10-1',7 , 'M'),
(3, '873294' , 'Jyoti Sharma' , '9123702518' ,null , '2003-01-13' , 1 , 6,'2023-10-1',7 , 'F'),
(1, '123098' , 'SCAT' , '4823984631' ,null , '2003-01-13' , 5 , 6,'2023-10-1' ,7 , 'O');


insert ignore into TeacherAddress values
(1 , 1 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' ),
(1 , 2 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' ),
(2 , 1 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' ),
(2 , 2 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' ),
(3 , 1 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' ),
(3 , 2 , '10' , 'street' , 'kolkata' , 321839 , 'state' , 'country' );



insert into leavebalance values 
( 1,2,1,14, 2024 ),
( 1,2,2,28, 2024 ), 
( 1,2,3,42, 2024 ), 
( 2,1,1,14, 2024 ), 
( 2,1,2,28, 2024 ), 
( 2,1,3,42, 2024 ), 
( 2,1,4,56, 2024 ), 
( 2,1,5,70, 2024 ), 
( 2,1,6,84, 2024 ), 
( 2,1,7,98, 2024 ), 
( 2,1,8,112, 2024 ), 
( 2,1,9,126, 2024 ), 
( 2,1,10,140, 2024 ), 
( 2,1,11,154, 2024 ), 
( 2,1,12,168, 2024 ), 
( 2,1,13,182, 2024 ), 
( 3,1,1,14, 2024 ), 
( 3,1,2,28, 2024 ), 
( 3,1,3,42, 2024 ), 
( 3,1,4,56, 2024 ), 
( 3,1,5,70, 2024 ), 
( 3,1,6,84, 2024 ), 
( 3,1,7,98, 2024 ), 
( 3,1,8,112, 2024 ), 
( 3,1,9,126, 2024 ), 
( 3,1,10,140, 2024 ), 
( 3,1,11,154, 2024 ), 
( 3,1,12,168, 2024 ), 
( 3,1,13,182, 2024 ); 































