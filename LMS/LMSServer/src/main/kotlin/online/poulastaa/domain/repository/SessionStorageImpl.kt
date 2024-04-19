package online.poulastaa.domain.repository

import io.ktor.server.sessions.*
import online.poulastaa.data.model.table.session.SessionStorageTable
import online.poulastaa.domain.dao.session.SessionStorageDB
import online.poulastaa.plugins.dbQuery

class SessionStorageImpl: SessionStorage {
    override suspend fun invalidate(id: String) {
        dbQuery {
            SessionStorageDB.find {
                SessionStorageTable.sessionId eq id
            }.firstOrNull()?.delete()
        }
    }

     override suspend fun read(id: String): String {
        return dbQuery {
            SessionStorageDB.find {
                SessionStorageTable.sessionId eq id
            }.firstOrNull()?.value ?: throw NoSuchElementException("Session $id not found")
        }
    }

    override suspend fun write(id: String, value: String) {
        val session = dbQuery {
            SessionStorageDB.find {
                SessionStorageTable.sessionId eq id
            }.firstOrNull()
        }

        if (session != null) dbQuery {
            session.value = value
        }
        else {
            dbQuery {
                SessionStorageDB.new {
                    this.sessionId = id
                    this.value = value
                }
            }
        }
    }
}