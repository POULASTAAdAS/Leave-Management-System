SET SQL_SAFE_UPDATES = 0;
use lms;

SELECT CONCAT('"', type, '",') AS type FROM designation;


select * from leavereq;
select * from leavestatus;


select teacherdetails.name , leavetype.type,
leavereq.reqDate , leavereq.fromDate , leavereq.toDate , status.type , leavestatus.cause , department.name from leavereq
join leavestatus on leavestatus.leaveId = leavereq.id
join teacherdetails on teacherdetails.teacherId = leavereq.teacherId
join leavetype on leavetype.id = leavereq.leaveTypeId
join status on status.id = leavestatus.statusId
join department on department.id = leavestatus.departmentId
where leavestatus.pendingEndId = 3 and department.id = 6 order by department.id;

select * from pendingend;


select * from teacher;

update teacher set emailVerified = 0 where id = 2;

select * from teachertype;

select * from designation;
select* from department;

select * from headclark;


delete from teacherdetails where teacherId = 2;

select * from teacherdetails;
select * from leavebalance;
select * from leavereq;
select * from leavestatus;

select * from DesignationTeacherTypeRelation;

select * from designation;

select * from departmenthead;

















































































































































