SET SQL_SAFE_UPDATES = 0;
use lmscopy;

SELECT CONCAT('"', type, '",') AS type FROM path;


select * from leavereq;
select * from leavestatus;


select teacherdetails.name , leavetype.type,
leavereq.reqDate , leavereq.fromDate , leavereq.toDate , status.type , leavestatus.cause , department.name from leavereq
join leavestatus on leavestatus.leaveId = leavereq.id
join teacherdetails on teacherdetails.teacherId = leavereq.teacherId
join leavetype on leavetype.id = leavereq.leaveTypeId
join status on status.id = leavestatus.statusId
join department on department.id = leavestatus.departmentId
where leavestatus.pendingEndId = 3 order by department.id;

select * from pendingend;

insert ignore into leavereq (teacherId, leaveTypeId, reqDate, fromDate, toDate, reason, addressDuringLeave, pathId, doc) values
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null),
(2 , 3 , '2024-06-03 02:58:09' , '2024-06-09' , '2024-06-23'  ,'test' , 'Present' ,1, null);





insert ignore into leavestatus values
(12 , 3,3,'test'  , 1, 1),
(13 , 3,3,'test'  , 1, 1),
(14 , 3,3,'test'  , 1, 1),
(15 , 3,3,'test'  , 1, 1),
(16 , 3,3,'test'  , 1, 2),
(17 , 3,3,'test'  , 1, 2),
(18 , 3,3,'test'  , 1, 2),
(19 , 3,3,'test'  , 1, 2),
(20 , 3,3,'test'  , 1, 4),
(21 , 3,3,'test'  , 1, 5),
(22 , 3,3,'test'  , 1, 5),
(23 , 3,3,'test'  , 1, 5),
(24 , 3,3,'test'  , 1, 7);

















































































































































































