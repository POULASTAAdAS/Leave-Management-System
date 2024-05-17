package com.poulastaa.domain.dao.department

import com.poulastaa.data.model.table.department.DepartmentTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Department(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Department>(DepartmentTable)

    val name by DepartmentTable.name

    enum class TYPE {

    }
}