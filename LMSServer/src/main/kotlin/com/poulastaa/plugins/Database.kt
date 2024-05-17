package com.poulastaa.plugins

import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.session.SessionStorageTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val driverClass = environment.config.property("storage.driverClassName").getString()
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()

    val db = Database.connect(
        provideDataSource(
            url = jdbcUrl,
            driverClass = driverClass
        )
    )

    transaction(db) {
        SchemaUtils.create(SessionStorageTable)
        SchemaUtils.create(TeacherDetailsTable)
    }
}


private fun provideDataSource(url: String, driverClass: String): HikariDataSource =
    HikariDataSource(
        HikariConfig().apply {
            driverClassName = driverClass
            jdbcUrl = url
            maximumPoolSize = 4
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }
    )

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(
    Dispatchers.IO,
    statement = { block() }
)