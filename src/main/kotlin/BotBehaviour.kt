import utils.BotUtils.doActionIfSubscribed
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
import dev.inmo.tgbotapi.types.toChatId
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.regular
import kotlinx.coroutines.runBlocking
import models.PersonTable.Person
import repositories.PersonRepository
import utils.Messages
import utils.Messages.addNumberMessage
import utils.Messages.alreadyExistsPhoneNumberMessage
import utils.Messages.checkMessage
import utils.Messages.finalMessage
import utils.Messages.startCommand
import utils.Messages.startCommandDescription
import utils.Messages.startMessage
import utils.Messages.thanksMessage
import utils.Messages.tryLotteryButton


class BotBehaviour(
    private val bot: TelegramBot,
    private val repository: PersonRepository,
) {
    @OptIn(RiskFeature::class)
    suspend fun startBot() {
        bot.buildBehaviourWithLongPolling {
            onCommand(startCommand) {
                val replyMarkup = ReplyKeyboardMarkup(
                    keyboard = listOf(
                        listOf(
                            SimpleKeyboardButton(tryLotteryButton),
                            //RequestContactKeyboardButton(addNumberMessage),
                        ),
                    )
                )
                reply(it, replyMarkup = replyMarkup) { regular(startMessage) }
            }

            onText {
                doActionIfSubscribed(it, bot) {
                    runBlocking {
                        when (it.content.text) {
                            tryLotteryButton -> {
                                reply(it) { regular(finalMessage) }
                            }

                        }
                    }
                }
            }

           /* onContact {
                doActionIfSubscribed(it, bot) {
                    runBlocking {
                        val person = Person(
                            it.contact?.userId?.chatId ?: throw RuntimeException(),
                            it.contact?.phoneNumber ?: throw RuntimeException(),
                        )
                        if (!repository.isPersonExists(person)) {
                            repository.savePerson(person)
                            reply(it) { regular(thanksMessage) }
                        } else {
                            reply(it) { regular(alreadyExistsPhoneNumberMessage) }
                        }

                    }
                }
            }*/

            setMyCommands(getBaseCommands())
        }.join()

    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand(startCommand, startCommandDescription)
    )
}