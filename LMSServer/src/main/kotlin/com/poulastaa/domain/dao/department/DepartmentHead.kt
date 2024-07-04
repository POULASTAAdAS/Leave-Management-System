package com.poulastaa.domain.dao.department

import com.poulastaa.data.model.table.department.DepartmentHeadTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DepartmentHead(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DepartmentHead>(DepartmentHeadTable)

    val teacherId by DepartmentHeadTable.teacherId
    val departmentId by DepartmentHeadTable.departmentId
}