package repositories

import models.PersonTable
import models.PersonTable.Person
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

    fun userByPhoneNumber(phoneNumber: String) {
        return transaction {
            PersonTable.select {
                (PersonTable.phoneNumber eq phoneNumber)
            }.map { row ->
                Person(
                    userId = row[PersonTable.userId],
                    phoneNumber = row[PersonTable.phoneNumber]
                )
            }.singleOrNull()
        }
    }
}