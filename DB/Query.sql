SET SQL_SAFE_UPDATES = 0;
use lmscopy;

SELECT CONCAT('"', type, '",') AS type FROM path;



select * from leavestatus;

select * from pendingend;

select * from leavereq;

alter table leavestatus add column departmentId int references Department(id) on delete cascade;

update leavestatus set departmentId = 6;

select * from department;


select * from teacher;
select * from teacherdetails;
select * from leavebalance;

delete from teacherdetails where teacherId = 2;
delete from leavebalance where teacherId = 2;
select * from designation;


select * from designationteachertyperelation;

select * from teachertype;

select * from departmenthead;

select * from teacherdetails;

select * from leavereq
join leavestatus on leavestatus.leaveId = leavereq.id
where leavestatus.departmentId = 6 and(leavestatus.actionId is null) order by leavereq.reqDate;

select * from leavereq
join leavestatus on leavestatus.leaveId = leavereq.id
where leavestatus.departmentId = 6 order by leavereq.reqDate;


select * from leaveaction;


select * from leavestatus where departmentid = 6 and actionid is null;

select * from status;


select * from teachertype;




select * from leavereq where teacherId = 1;

select * from leavestatus;

select * from status;


select * from path;


select * from principal;








































































































































































































