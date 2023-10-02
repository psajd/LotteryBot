package utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager.Companion.defaultDatabase
import org.koin.java.KoinJavaComponent.inject

object Config {
    private val dotEnv: Dotenv by inject(Dotenv::class.java)

    object TelegramConfig {
        val telegramToken: String =
            dotEnv["TELEGRAM_TOKEN"] ?: error("TELEGRAM_TOKEN not found in environment variables.")
        val telegramChannelLink: String =
            dotEnv["TELEGRAM_CHANNEL_LINK"] ?: error("TELEGRAM_CHANNEL_LINK not found in environment variables.")
        val telegramChannelId: Long =
            (dotEnv["TELEGRAM_CHANNEL_ID"] ?: error("TELEGRAM_CHANNEL_ID not found in environment variables.")).toLong()
    }
}

fun configureDatabase() {
    val hikariConfig = HikariConfig("/db.properties")
    val dataSource = HikariDataSource(hikariConfig)
    defaultDatabase = Database.connect(dataSource)

    val flyway = Flyway.configure().dataSource(dataSource).load()
    flyway.migrate()
}