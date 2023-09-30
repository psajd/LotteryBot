import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.contact
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.regular
import utils.Messages


class BotBehaviour(
    private val bot: TelegramBot,
) {
    @OptIn(RiskFeature::class)
    suspend fun startBot() {
        bot.buildBehaviourWithLongPolling {
            onCommand("start") {
                val replyMarkup = ReplyKeyboardMarkup(
                    keyboard = listOf(
                        listOf(
                            SimpleKeyboardButton("Принять участие"),
                            RequestContactKeyboardButton("Добавить номер телефона"),
                        ),
                    )
                )
                reply(it, replyMarkup = replyMarkup) { regular(Messages.startMessage) }
            }
            onText {
                when (it.content.text) {
                    "Принять участие" -> reply(it) { regular(Messages.startMessage) }
                }
            }
            onContact {
                if (it.from?.id   == it.content.contact.userId) {
                    reply(it) { regular(Messages.thanksMessage) }
                } else {
                    reply(it) { regular(Messages.wrongNumberMessage) }
                }
            }
            setMyCommands(getBaseCommands())
        }.join()


    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand("start", "Начать работу бота")
    )
}