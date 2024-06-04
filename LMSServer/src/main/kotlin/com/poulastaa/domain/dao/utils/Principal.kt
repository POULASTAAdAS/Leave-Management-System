package com.poulastaa.domain.dao.utils

import com.poulastaa.data.model.table.utils.PrincipalTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Principal(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Principal>(PrincipalTable)

    var name by PrincipalTable.name
    var email by PrincipalTable.email
    var profilePic by PrincipalTable.profilePic
}