package com.poulastaa.data.model.table.address

import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.utils.DesignationTable
import com.poulastaa.data.model.table.utils.QualificationTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object TeacherDetailsTable : Table() {
    val teacherId = reference("teacherId", TeacherTable.id, onDelete = ReferenceOption.CASCADE)
    val hrmsId = varchar("hrmsId", 30).uniqueIndex()
    val name = varchar("name", 100)
    val phone_1 = varchar("phone_1", 10).uniqueIndex()
    val phone_2 = varchar("phone_2", 10).nullable().default(null)
    val bDate = date("bDate")
    val gender = varchar("gender", 1)
    val designationId = reference("designationId", DesignationTable.id, onDelete = ReferenceOption.CASCADE)
    val departmentId = reference("departmentId", DepartmentTable.id, onDelete = ReferenceOption.CASCADE)
    val joiningDate = date("joiningDate")
    val qualificationId = reference("qualificationId", QualificationTable.id, onDelete = ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(teacherId)
}