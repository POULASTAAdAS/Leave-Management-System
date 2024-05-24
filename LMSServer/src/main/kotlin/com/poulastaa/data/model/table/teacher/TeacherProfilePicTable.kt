package com.poulastaa.data.model.table.teacher

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TeacherProfilePicTable : Table() {
    val teacherId = reference("teacherId", TeacherTable, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 200)
    val profilePic = blob("profilePic")

    override val primaryKey = PrimaryKey(teacherId)
}