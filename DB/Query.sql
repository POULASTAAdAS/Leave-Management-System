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



select leavereq.id , department.name , teacherdetails.name, leavetype.type ,  leavereq.reqDate, leavereq.fromDate , leavereq.toDate , leavereq.reason  from leavereq
join leavestatus on leavestatus.leaveId = leavereq.id
join department on department.id = leavestatus.departmentId
join leavetype on leavetype.id = leavereq.leaveTypeId
join teacherdetails on teacherdetails.teacherId = leavereq.teacherId
order by department.name , teacherdetails.name;


select * from leavereq;
select * from leavestatus;


select * from principal;
select * from headclark;

select * from teacher;


select * from departmenthead;
select * from designation;
select * from qualification;

update department set name = "NTS" where id = 26;

select * from teacherdetails where teacherId = 11;

select * from designationteachertyperelation;
select * from teachertype;

select * from leavereq where teacherId = 11;
select * from leavereq;
select * from leavestatus;

select * from leavestatus where leaveId in (47 , 48 , 49);

select * from status;
select * from path;
select * from pendingend;


select * from leavetype;


select * from principal;



select * from teacherdetails;
update teacherdetails set gender = 'F' where teacherid = 7;




select * from teacher;


select* from loginemail;
delete from loginemail;


select * from departmenthead;
select * from principal;


select * from leavetype;

select * from teachertype;
select * from leavebalance where teacherid = 7;
select * from teacher;
select * from teacherdetails;

select * from leavebalance where teacherid = 7;

select * from leavereq;
select * from leavestatus;
select * from pendingend;
select * from principal;

delete from teacherdetails where teacherId = 10;
select * from leavebalance;
select * from teacherdetails;

select * from leavetype;


select * from leavebalance;

delete from teacher where id = 14;
select * from teacher;

insert into teacehr values (14, sacteacherone.gmail.com,1);
































