package utils

import dev.inmo.micro_utils.common.Warning
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.memberChatMemberOrNull
import dev.inmo.tgbotapi.extensions.utils.ownerChatMemberOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.URLInlineKeyboardButton
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.MessageContent

object BotUtils {
    @OptIn(Warning::class)
    suspend fun check(usId: UserId, cyId: IdChatIdentifier, bot: TelegramBot): Boolean {
        println(cyId)
        println(bot.getChatMember(cyId.toChatId(), usId).memberChatMemberOrNull())
        println(usId.userLink)

        return bot.getChatMember(cyId.toChatId(), usId).ownerChatMemberOrNull() != null ||
                bot.getChatMember(cyId.toChatId(), usId).memberChatMemberOrNull() != null
    }

    suspend fun <T : MessageContent> doActionIfSubscribed(
        it: CommonMessage<T>,
        bot: TelegramBot,
        block: () -> Unit
    ) {
        if (check(it.chat.id.toChatId(), bot.getChat(ChatId(Config.TelegramConfig.telegramChannelId)).id, bot)) {
            block.invoke()
        } else {
            val inlineKeyboardButton =
                InlineKeyboardMarkup(
                    URLInlineKeyboardButton(Messages.subscribeButton, Config.TelegramConfig.telegramChannelLink),
                )
            bot.sendMessage(it.chat.id, Messages.channelMessage, replyMarkup = inlineKeyboardButton)
        }
    }

}
