import utils.BotUtils.doActionIfSubscribed
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.utils.regular
import kotlinx.coroutines.runBlocking
import utils.Messages
import utils.Messages.tryLotteryButton

class BotBehaviour(
    private val bot: TelegramBot,
    // TODO: доделать
) {
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