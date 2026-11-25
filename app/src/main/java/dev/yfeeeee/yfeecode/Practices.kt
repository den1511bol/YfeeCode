// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.os.Handler
import android.os.Looper
import com.chaquo.python.Python
import kotlin.concurrent.thread

object Practices {
    fun forCourse(id: String): List<Practice> = all[id] ?: emptyList()

    fun verify(p: Practice, code: String, onResult: (Boolean, String) -> Unit) {
        val ui = Handler(Looper.getMainLooper())
        thread {
            for (c in p.checks) {
                if (!c.regex.containsMatchIn(code)) { ui.post { onResult(false, c.message) }; return@thread }
            }
            if (p.expectOut != null && p.lang == Lang.PYTHON) {
                val r = try {
                    Python.getInstance().getModule("practice_run").callAttr("run", code).asList()
                } catch (e: Throwable) {
                    ui.post { onResult(false, "Не удалось запустить код: " + e.message) }
                    return@thread
                }
                val finished = r[0].toBoolean()
                val out = r[1].toString().replace("\r\n", "\n").trim()
                val err = r[2].toString()
                val msg = when {
                    !finished || err.isNotEmpty() -> "Ошибка: $err"
                    out != p.expectOut.trim() -> "Вывод не совпал.\nОжидалось:\n${p.expectOut.trim()}\nПолучено:\n${out.ifEmpty { "(пусто)" }}"
                    else -> null
                }
                ui.post { if (msg == null) onResult(true, "Код запущен, вывод верный.") else onResult(false, msg) }
                return@thread
            }
            ui.post { onResult(true, "Структура решения верная.") }
        }
    }

    private val all: Map<String, List<Practice>> = mapOf(
        "py" to listOf(
            Practice("py1", "Привет, мир", "Выведи строку: Привет, мир!", "# Напиши код ниже\n", Lang.PYTHON,
                listOf(must("""print\s*\(""", "Нужна команда print(...)")), "Привет, мир!"),
            Practice("py2", "Сумма", "Выведи сумму чисел 17 и 25.", "# Напиши код ниже\n", Lang.PYTHON,
                listOf(must("""print\s*\(""", "Нужна команда print(...)")), "42"),
            Practice("py3", "Числа 1–5", "Выведи числа от 1 до 5, каждое с новой строки. Используй цикл.", "# Цикл for или while\n", Lang.PYTHON,
                listOf(must("""\b(for|while)\b""", "Используй цикл for или while")), "1\n2\n3\n4\n5"),
            Practice("py4", "Квадрат", "Напиши функцию square(x), которая возвращает квадрат числа, и выведи square(7).", "def square(x):\n    \n", Lang.PYTHON,
                listOf(must("""def\s+square\s*\(""", "Нужна функция def square(x):"), must("""\breturn\b""", "Функция должна возвращать значение через return")), "49"),
            Practice("py5", "Сортировка", "Создай список nums = [3, 1, 2], отсортируй его и выведи.", "nums = [3, 1, 2]\n", Lang.PYTHON,
                listOf(must("""sort""", "Используй sort() или sorted()")), "[1, 2, 3]"),
            Practice("py6", "FizzBuzz", "Для чисел от 1 до 15: если делится на 3 — Fizz, на 5 — Buzz, на оба — FizzBuzz, иначе само число.", "for i in range(1, 16):\n    \n", Lang.PYTHON,
                listOf(must("""%|\bdivmod\b""", "Подсказка: остаток от деления считается знаком %")),
                "1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz"),
            Practice("py7", "Заготовка бота", "Напиши заготовку Telegram-бота на telebot: импорт, создание бота, обработчик /start и запуск polling.", "TOKEN = \"123:ABC\"\n", Lang.PYTHON,
                listOf(must("""import\s+telebot""", "Нужен import telebot"), must("""TeleBot\s*\(""", "Создай бота: telebot.TeleBot(TOKEN)"),
                    must("""message_handler""", "Нужен обработчик @bot.message_handler(...)"), must("""polling""", "Запусти bot.infinity_polling()"))),
            Practice("py8", "Модуль Heroku", "Напиши модуль Heroku: декоратор @loader.tds, класс от loader.Module и команду с @loader.command().", "from .. import loader, utils\n\n", Lang.PYTHON,
                listOf(must("""@loader\.tds""", "Нужен декоратор @loader.tds"), must("""class\s+\w+\s*\(\s*loader\.Module\s*\)""", "Нужен класс: class MyMod(loader.Module):"),
                    must("""@loader\.command""", "Нужен декоратор @loader.command()"), must("""async\s+def\s+\w+cmd""", "Команда: async def hellocmd(self, message):"))),
            Practice("py9", "Класс Dog", "Создай класс Dog с методом speak, который выводит Гав. Создай объект и вызови speak.", "", Lang.PYTHON,
                listOf(must("""class\s+Dog""", "Нужен class Dog:"), must("""def\s+speak\s*\(\s*self""", "Нужен метод def speak(self):")), "Гав"),
            Practice("py10", "Сумма значений", "Создай словарь prices с ключами a и b (значения 1 и 2) и выведи сумму его значений.", "", Lang.PYTHON,
                listOf(must("""prices\s*=\s*\{""", "Создай словарь prices = {...}")), "3"),
            Practice("py11", "Асинхронный main", "Напиши async def main(), который печатает Готово, и запусти её через asyncio.run.", "import asyncio\n\n", Lang.PYTHON,
                listOf(must("""async\s+def\s+main""", "Нужна async def main():"), must("""asyncio\.run\s*\(""", "Запусти через asyncio.run(main())")), "Готово")        ),
        "js" to listOf(
            Practice("js1", "Привет", "Выведи в консоль слово Hello.", "// Напиши код ниже\n", Lang.JS,
                listOf(must("""console\.log\s*\(""", "Нужен console.log(...)"), must("""Hello""", "Выведи слово Hello"))),
            Practice("js2", "Функция sum", "Напиши функцию sum(a, b), которая возвращает сумму.", "", Lang.JS,
                listOf(must("""(function\s+sum|const\s+sum\s*=|let\s+sum\s*=)""", "Нужна функция sum"), must("""\breturn\b|=>""", "Функция должна вернуть значение"))),
            Practice("js3", "Цикл", "Выведи числа от 1 до 5 в консоль с помощью цикла.", "", Lang.JS,
                listOf(must("""\b(for|while)\s*\(""", "Нужен цикл for или while"), must("""console\.log""", "Выводи через console.log")))
        ),
        "html" to listOf(
            Practice("html1", "Каркас страницы", "Собери каркас: DOCTYPE, html, head с title, body.", "", Lang.HTML,
                listOf(must("""<!DOCTYPE\s+html>""", "Нужен <!DOCTYPE html>"), must("""<html[\s>]""", "Нужен тег <html>"), must("""<title>[\s\S]*</title>""", "Нужен <title>…</title>"), must("""<body[\s>][\s\S]*</body>""", "Нужен <body>…</body>"))),
            Practice("html2", "Список из трёх", "Сделай маркированный список ul из трёх пунктов li.", "", Lang.HTML,
                listOf(must("""<ul[\s>]""", "Нужен тег <ul>"), must("""(<li>[\s\S]*?</li>[\s\S]*?){3}""", "Нужно три пункта <li>…</li>"))),
            Practice("html3", "Ссылка и картинка", "Добавь ссылку (a с href) и картинку (img с src).", "", Lang.HTML,
                listOf(must("""<a\s+[^>]*href\s*=""", "Нужна ссылка <a href=…>"), must("""<img\s+[^>]*src\s*=""", "Нужна картинка <img src=…>")))
        ),
        "css" to listOf(
            Practice("css1", "Тёмный фон", "Задай для body тёмный фон и светлый цвет текста.", "", Lang.CSS,
                listOf(must("""body\s*\{[^}]*background""", "Нужно правило body с background"), must("""body\s*\{[^}]*[^-]color\s*:""", "Добавь color для текста"))),
            Practice("css2", "Карточка", "Сделай правило .card со скруглением и внутренним отступом.", "", Lang.CSS,
                listOf(must("""\.card\s*\{""", "Нужен селектор .card"), must("""border-radius\s*:""", "Добавь border-radius"), must("""padding\s*:""", "Добавь padding"))),
            Practice("css3", "Центр flex", "Отцентруй содержимое контейнера .box через flex по обеим осям.", "", Lang.CSS,
                listOf(must("""display\s*:\s*flex""", "Нужен display: flex"), must("""justify-content\s*:\s*center""", "Нужен justify-content: center"), must("""align-items\s*:\s*center""", "Нужен align-items: center")))
        ),
        "cpp" to listOf(
            Practice("cpp1", "Hello, C++", "Напиши программу, которая выводит Hello.", "", Lang.CPP,
                listOf(must("""#include\s*<iostream>""", "Подключи <iostream>"), must("""int\s+main\s*\(""", "Нужна функция int main()"), must("""cout""", "Вывод через std::cout"), must("""return\s+0""", "Заверши программу return 0;"))),
            Practice("cpp2", "Функция add", "Напиши функцию int add(int a, int b), возвращающую сумму.", "", Lang.CPP,
                listOf(must("""int\s+add\s*\(\s*int\s+\w+\s*,\s*int\s+\w+\s*\)""", "Нужно: int add(int a, int b)"), must("""\breturn\b""", "Функция должна возвращать значение"))),
            Practice("cpp3", "Цикл 1–5", "Выведи числа от 1 до 5 циклом for.", "", Lang.CPP,
                listOf(must("""for\s*\(""", "Нужен цикл for"), must("""cout""", "Вывод через std::cout")))
        ),
        "cs" to listOf(
            Practice("cs1", "Hello, C#", "Напиши программу с классом Program, методом Main и выводом Hello.", "", Lang.CSHARP,
                listOf(must("""class\s+Program""", "Нужен class Program"), must("""static\s+void\s+Main\s*\(""", "Нужен static void Main()"), must("""Console\.WriteLine\s*\(""", "Вывод: Console.WriteLine(...)"))),
            Practice("cs2", "Метод Add", "Напиши метод static int Add(int a, int b), возвращающий сумму.", "", Lang.CSHARP,
                listOf(must("""static\s+int\s+Add\s*\(\s*int\s+\w+\s*,\s*int\s+\w+\s*\)""", "Нужно: static int Add(int a, int b)"), must("""\breturn\b""", "Метод должен возвращать значение"))),
            Practice("cs3", "foreach", "Пройди по списку чисел через foreach и выведи каждое.", "", Lang.CSHARP,
                listOf(must("""foreach\s*\(""", "Нужен цикл foreach"), must("""Console\.WriteLine""", "Вывод: Console.WriteLine")))
        )
    )
}
