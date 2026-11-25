// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

data class Template(val title: String, val fileName: String, val body: String)

object Templates {
    private const val Q3 = "\"\"\""
    private const val TOKEN_PY =
        "TOKEN = open(\"token.txt\").read().strip()  # положи токен бота в файл token.txt рядом\n"

    val all = listOf(
        Template("Бот: pyTelegramBotAPI", "bot_telebot.py", """
import telebot

$TOKEN_PY
bot = telebot.TeleBot(TOKEN)


@bot.message_handler(commands=["start"])
def start(message):
    bot.reply_to(message, "Привет! Бот работает на YfeeCode")


@bot.message_handler(func=lambda m: True)
def echo(message):
    bot.reply_to(message, message.text)


print("Бот запущен")
bot.infinity_polling()
""".trimStart()),
        Template("Бот: aiogram 2.x", "bot_aiogram.py", """
import logging
from aiogram import Bot, Dispatcher, executor, types

logging.basicConfig(level=logging.INFO)
$TOKEN_PY
bot = Bot(token=TOKEN)
dp = Dispatcher(bot)


@dp.message_handler(commands=["start"])
async def start(message: types.Message):
    await message.answer("Привет! Бот работает на YfeeCode")


@dp.message_handler()
async def echo(message: types.Message):
    await message.answer(message.text)


if __name__ == "__main__":
    executor.start_polling(dp, skip_updates=True)
""".trimStart()),
        Template("Бот: pyrogram", "bot_pyrogram.py", """
from pyrogram import Client, filters

API_ID = 0          # https://my.telegram.org
API_HASH = ""
$TOKEN_PY
app = Client("yfeecode_bot", api_id=API_ID, api_hash=API_HASH, bot_token=TOKEN, in_memory=True)


@app.on_message(filters.command("start"))
async def start(client, message):
    await message.reply("Привет! Бот работает на YfeeCode")


app.run()
""".trimStart()),
        Template("Модуль Heroku", "MyMod.py", """
# meta developer: @yfeeeee
from .. import loader, utils


@loader.tds
class MyMod(loader.Module):
    ${Q3}Описание модуля${Q3}

    strings = {"name": "MyMod", "hello": "Привет из модуля!"}

    @loader.command()
    async def hellocmd(self, message):
        ${Q3}Поздороваться${Q3}
        args = utils.get_args_raw(message)
        await utils.answer(message, self.strings("hello") + (" " + args if args else ""))
""".trimStart()),
        Template("JS: бот на fetch", "bot.js", """
// Токен: впиши ниже. Работает через WebView (без Node).
const TOKEN = "PASTE_TOKEN_HERE";
const API = "https://api.telegram.org/bot" + TOKEN;
let offset = 0;

async function loop() {
  while (true) {
    try {
      const r = await fetch(API + "/getUpdates?timeout=25&offset=" + offset);
      const data = await r.json();
      for (const u of data.result || []) {
        offset = u.update_id + 1;
        if (u.message && u.message.text) {
          console.log("msg:", u.message.text);
          await fetch(API + "/sendMessage", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ chat_id: u.message.chat.id, text: u.message.text })
          });
        }
      }
    } catch (e) { console.error(e); await new Promise(r => setTimeout(r, 3000)); }
  }
}
loop();
""".trimStart()),
        Template("HTML-страница", "index.html", FileStoreTemplates.html),
        Template("C++: Hello", "main.cpp", FileStoreTemplates.cpp),
        Template("C#: Hello", "Program.cs", FileStoreTemplates.cs)
    )
}

private object FileStoreTemplates {
    val html = "<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\">\n    <title>Page</title>\n</head>\n<body>\n    \n</body>\n</html>\n"
    val cpp = "#include <iostream>\n\nint main() {\n    std::cout << \"Hello\" << std::endl;\n    return 0;\n}\n"
    val cs = "using System;\n\nclass Program\n{\n    static void Main()\n    {\n        Console.WriteLine(\"Hello\");\n    }\n}\n"
}
