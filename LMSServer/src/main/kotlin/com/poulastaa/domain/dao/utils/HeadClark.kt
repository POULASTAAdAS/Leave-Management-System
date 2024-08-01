package com.poulastaa.domain.dao.utils

import com.poulastaa.data.model.table.utils.HeadClarkTable
import com.poulastaa.data.model.table.utils.PrincipalTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HeadClark(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HeadClark>(HeadClarkTable)

    var name by HeadClarkTable.name
    var email by HeadClarkTable.email
    var profilePic by PrincipalTable.profilePic
}