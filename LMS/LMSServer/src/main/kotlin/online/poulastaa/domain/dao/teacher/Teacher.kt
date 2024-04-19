package online.poulastaa.domain.dao.teacher

import online.poulastaa.data.model.table.teacher.TeacherTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Teacher(id : EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Teacher>(TeacherTable)

    val email by TeacherTable.email
    val teacherTypeId by TeacherTable.teacherTypeId
}