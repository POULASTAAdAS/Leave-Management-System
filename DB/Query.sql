SET SQL_SAFE_UPDATES = 0;

use lmscopy;

select * from leaveaction;

select * from teacher;

select * from teacherType;

select * from TeacherDetails;

select * from designation;
select * from department;


select * from addresstype;

select * from designation;

select * from qualification;


select * from teacher;
select * from TeacherDetails;


update teacher set emailVerified = false where id = 1;


select * from loginemail;

select * from designation;

select * from department;

SELECT CONCAT('"', type, '",') AS type FROM qualification;

select * from qualification;

select * from departmenthead;


select * from loginEmail;

delete from loginemail;

select * from teacheraddress where teacherId = 1;


select * from addresstype;

SELECT addresstype.id, addresstype.`type` FROM addresstype WHERE addresstype.id = 1;






