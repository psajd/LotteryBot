package repositories

import models.PersonTable
import models.PersonTable.Person
import models.PersonTable.userId
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PersonRepository {
    fun savePerson(person: Person) {
        return transaction {
            PersonTable.insert {
                it[userId] = person.userId
                it[phoneNumber] = person.phoneNumber
            }
        }
    }
    fun isPersonExists(person: Person): Boolean {
        return transaction {
            PersonTable.select {
                (PersonTable.userId eq person.userId)
            }.map { row ->
                Person(
                    userId = row[PersonTable.userId],
                    phoneNumber = row[PersonTable.phoneNumber]
                )
            }.singleOrNull() != null
        }
    }
}