package online.poulastaa.domain.dao.teacher

import online.poulastaa.data.model.table.teacher.TeacherTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TeacherType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TeacherType>(TeacherTypeTable)

    val type by TeacherTypeTable.type
}