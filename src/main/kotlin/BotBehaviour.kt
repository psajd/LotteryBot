import utils.BotUtils.doActionIfSubscribed
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.contact
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.regular
import kotlinx.coroutines.runBlocking
import models.PersonTable
import models.PersonTable.Person
import repositories.PersonRepository
import utils.Messages
import utils.Messages.tryLotteryButton
import java.lang.RuntimeException

class BotBehaviour(
    private val bot: TelegramBot,
    private val repository: PersonRepository,
) {
    @OptIn(RiskFeature::class)
    suspend fun startBot() {
        bot.buildBehaviourWithLongPolling {
            onCommand("start") {
                val replyMarkup = ReplyKeyboardMarkup(
                    keyboard = listOf(
                        listOf(
                            SimpleKeyboardButton(tryLotteryButton),
                            RequestContactKeyboardButton("Добавить номер телефона"),
                        ),
                    )
                )
                reply(it, replyMarkup = replyMarkup) { regular(Messages.startMessage) }
            }

            onText {
                doActionIfSubscribed(it, bot) {
                    runBlocking {
                        when (it.content.text) {
                            tryLotteryButton -> reply(it) { regular(Messages.checkMessage) }
                        }
                    }
                }
            }

            onContact {
                doActionIfSubscribed(it, bot) {
                    runBlocking {
                        repository.savePerson(
                            Person(
                                it.contact?.userId?.chatId ?: throw RuntimeException(),
                                it.contact?.phoneNumber ?: throw RuntimeException(),
                            )
                        )
                        reply(it) { regular(Messages.thanksMessage) }
                    }
                }
            }

            setMyCommands(getBaseCommands())
        }.join()

    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand("start", "Начать работу бота")
    )
}