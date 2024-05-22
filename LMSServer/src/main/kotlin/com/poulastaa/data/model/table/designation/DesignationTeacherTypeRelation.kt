package com.poulastaa.data.model.table.designation

import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DesignationTeacherTypeRelation : Table() {
    val designationId = integer("designationId").references(DesignationTable.id, ReferenceOption.CASCADE)
    val teacherTypeId = integer("teacherTypeId").references(TeacherTypeTable.id)

    override val primaryKey = PrimaryKey(designationId, teacherTypeId)
}