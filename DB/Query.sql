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















































































































