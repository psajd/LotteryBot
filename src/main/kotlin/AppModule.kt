import dev.inmo.tgbotapi.bot.ktor.telegramBot
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.transactions.TransactionManager.Companion.defaultDatabase
import org.koin.dsl.module
import utils.Config.TelegramConfig.telegramToken


val appModule = module {
    single { dotenv() }

    single {
        telegramBot(telegramToken)
    }

    single { BotBehaviour(get()) }
    single { defaultDatabase }
}