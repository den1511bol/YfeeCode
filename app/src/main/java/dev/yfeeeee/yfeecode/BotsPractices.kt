// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Практика курса «Боты и Heroku» (проверка по шаблонам кода). */
fun botsPractices(): List<Practice> = listOf(
    Practice("bt1", "Первый бот", "Создай бота на telebot: импорт, TeleBot(TOKEN), обработчик /start с reply_to и infinity_polling.",
        "import telebot\n\nTOKEN = open(\"token.txt\").read().strip()\n", Lang.PYTHON,
        listOf(
            must("""telebot\.TeleBot\s*\(""", "Создай бота: telebot.TeleBot(TOKEN)"),
            must("""message_handler\s*\(\s*commands\s*=""", "Добавь обработчик @bot.message_handler(commands=[\"start\"])"),
            must("""reply_to\s*\(""", "Ответь пользователю через bot.reply_to(...)"),
            must("""infinity_polling\s*\(""", "Запусти бота: bot.infinity_polling()")
        ))
)
