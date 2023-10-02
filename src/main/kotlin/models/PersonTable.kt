package models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PersonTable : Table() {
    val userId: Column<Long> = long("user_id")
    val phoneNumber: Column<String> = varchar("phoneNumber", 20)
    override val primaryKey = PrimaryKey(userId, name = "PK_User_Id")

    data class Person(
        var userId: Long,
        var phoneNumber: String,
    )
}