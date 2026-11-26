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
                listOf(must("""async\s+def\s+main""", "Нужна async def main():"), must("""asyncio\.run\s*\(""", "Запусти через asyncio.run(main())")), "Готово"),
            Practice("py12", "Ловим ошибку", "Попробуй выполнить int(\"abc\"), поймай ValueError и выведи слово Ошибка.", "", Lang.PYTHON,
                listOf(must("""\btry\s*:""", "Нужен блок try:"), must("""except\s+ValueError""", "Лови except ValueError:")), "Ошибка"),
            Practice("py13", "Модуль math", "Подключи math и выведи корень из 81.", "", Lang.PYTHON,
                listOf(must("""import\s+math|from\s+math\s+import""", "Подключи модуль math")), "9.0"),
            Practice("py14", "Квадраты", "Создай список squares из квадратов чисел от 0 до 4 и выведи его.", "", Lang.PYTHON,
                listOf(must("""\bfor\b.*\bin\b""", "Используй списковое включение [x * x for x in ...]")), "[0, 1, 4, 9, 16]"),
            Practice("py15", "Наследование", "Создай класс Animal с методом speak (возвращает \"звук\") и класс Dog(Animal). Выведи Dog().speak().", "", Lang.PYTHON,
                listOf(must("""class\s+Animal""", "Нужен class Animal"), must("""class\s+Dog\s*\(\s*Animal\s*\)""", "Нужно class Dog(Animal):")), "звук"),
            Practice("py16", "Декоратор", "Напиши декоратор deco, который печатает start перед вызовом функции, и примени его к функции f. Вызови f().", "", Lang.PYTHON,
                listOf(must("""def\s+deco\s*\(""", "Нужна функция deco"), must("""@deco""", "Примени декоратор: @deco")), "start")
        ),
        "js" to listOf(
            Practice("js1", "Привет", "Выведи в консоль слово Hello.", "// Напиши код ниже\n", Lang.JS,
                listOf(must("""console\.log\s*\(""", "Нужен console.log(...)"), must("""Hello""", "Выведи слово Hello"))),
            Practice("js2", "Функция sum", "Напиши функцию sum(a, b), которая возвращает сумму.", "", Lang.JS,
                listOf(must("""(function\s+sum|const\s+sum\s*=|let\s+sum\s*=)""", "Нужна функция sum"), must("""\breturn\b|=>""", "Функция должна вернуть значение"))),
            Practice("js3", "Цикл", "Выведи числа от 1 до 5 в консоль с помощью цикла.", "", Lang.JS,
                listOf(must("""\b(for|while)\s*\(""", "Нужен цикл for или while"), must("""console\.log""", "Выводи через console.log"))),
            Practice("js4", "Обработчик клика", "Найди элемент по селектору и добавь обработчик события click.", "", Lang.JS,
                listOf(must("""(querySelector|getElementById)\s*\(""", "Найди элемент через document.querySelector(...)"), must("""addEventListener\s*\(\s*["']click["']""", "Добавь addEventListener(\"click\", ...)"))),
            Practice("js5", "Пауза через промис", "Напиши async-функцию, которая ждёт 1 секунду через Promise и setTimeout.", "", Lang.JS,
                listOf(must("""\basync\b""", "Нужна async-функция"), must("""new\s+Promise""", "Создай new Promise(...)"), must("""setTimeout\s*\(""", "Используй setTimeout(...)"))),
            Practice("js6", "map удвоение", "Удвой каждое число массива [1, 2, 3] через map.", "", Lang.JS,
                listOf(must("""\.map\s*\(""", "Используй метод map"), must("""\*\s*2|2\s*\*""", "Умножай на 2"))),
            Practice("js7", "Класс Dog", "Объяви класс Dog с constructor(name) и создай объект через new.", "", Lang.JS,
                listOf(must("""class\s+Dog""", "Нужен class Dog"), must("""constructor\s*\(""", "Нужен constructor(...)"), must("""new\s+Dog\s*\(""", "Создай объект: new Dog(...)"))),
            Practice("js8", "Деструктуризация", "Достань name из объекта dog через деструктуризацию.", "", Lang.JS,
                listOf(must("""(const|let)\s*\{\s*name\s*\}\s*=\s*dog""", "Нужно: const { name } = dog;"))),
            Practice("js9", "Шаблонная строка", "Создай переменную name и выведи через шаблонную строку Привет, и имя.", "", Lang.JS,
                listOf(must("""`[^`]*\$\{\s*name\s*\}[^`]*`""", "Нужна строка в обратных кавычках с \${name}"), must("""console\.log""", "Выведи через console.log"))),
            Practice("js10", "Объект user", "Создай объект user с полями name и age.", "", Lang.JS,
                listOf(must("""(const|let|var)\s+user\s*=\s*\{""", "Нужно: const user = { ... }"), must("""name\s*:""", "Нужно поле name"), must("""age\s*:""", "Нужно поле age"))),
            Practice("js11", "Стрелочная функция", "Напиши стрелочную функцию add, складывающую a и b.", "", Lang.JS,
                listOf(must("""(const|let)\s+add\s*=\s*\(?\s*a\s*,\s*b\s*\)?\s*=>""", "Нужно: const add = (a, b) => ..."), must("""a\s*\+\s*b""", "Верни a + b"))),
            Practice("js12", "map", "Умножь каждый элемент массива arr на 2 через map.", "", Lang.JS,
                listOf(must("""\.map\s*\(""", "Используй arr.map(...)"), must("""\*\s*2""", "Умножь на 2"))),
            Practice("js13", "filter", "Оставь в массиве arr только чётные числа через filter.", "", Lang.JS,
                listOf(must("""\.filter\s*\(""", "Используй arr.filter(...)"), must("""%\s*2""", "Проверь остаток от деления на 2"))),
            Practice("js14", "Деструктуризация", "Достань поля name и age из объекта user одной строкой.", "", Lang.JS,
                listOf(must("""(const|let)\s*\{\s*name\s*,\s*age\s*\}\s*=\s*user""", "Нужно: const { name, age } = user;"))),
            Practice("js15", "async и await", "Напиши async-функцию load, которая ждёт fetch и вызывает json().", "", Lang.JS,
                listOf(must("""async\s+function\s+load|async\s*\(""", "Нужна async-функция load"), must("""await\s+fetch\s*\(""", "Дождись: await fetch(...)"), must("""\.json\s*\(""", "Вызови .json()"))),
            Practice("js16", "try/catch", "Оберни JSON.parse(text) в try/catch и выведи ошибку.", "", Lang.JS,
                listOf(must("""try\s*\{""", "Нужен блок try {"), must("""catch\s*\(""", "Нужен catch (e)"), must("""JSON\.parse\s*\(""", "Используй JSON.parse")))
        ),
        "html" to listOf(
            Practice("html1", "Каркас страницы", "Собери каркас: DOCTYPE, html, head с title, body.", "", Lang.HTML,
                listOf(must("""<!DOCTYPE\s+html>""", "Нужен <!DOCTYPE html>"), must("""<html[\s>]""", "Нужен тег <html>"), must("""<title>[\s\S]*</title>""", "Нужен <title>…</title>"), must("""<body[\s>][\s\S]*</body>""", "Нужен <body>…</body>"))),
            Practice("html2", "Список из трёх", "Сделай маркированный список ul из трёх пунктов li.", "", Lang.HTML,
                listOf(must("""<ul[\s>]""", "Нужен тег <ul>"), must("""(<li>[\s\S]*?</li>[\s\S]*?){3}""", "Нужно три пункта <li>…</li>"))),
            Practice("html3", "Ссылка и картинка", "Добавь ссылку (a с href) и картинку (img с src).", "", Lang.HTML,
                listOf(must("""<a\s+[^>]*href\s*=""", "Нужна ссылка <a href=…>"), must("""<img\s+[^>]*src\s*=""", "Нужна картинка <img src=…>"))),
            Practice("html4", "Таблица 2x2", "Сделай таблицу: две строки по две ячейки.", "", Lang.HTML,
                listOf(must("""<table[\s>]""", "Нужен тег <table>"), must("""(<tr>[\s\S]*?</tr>[\s\S]*?){2}""", "Нужно две строки <tr>…</tr>"), must("""(<td>[\s\S]*?</td>[\s\S]*?){4}""", "Нужно четыре ячейки <td>…</td>"))),
            Practice("html8", "Мета viewport", "Добавь в head тег meta с name=\"viewport\".", "", Lang.HTML,
                listOf(must("""<meta[^>]*name\s*=\s*["']viewport["']""", "Нужен <meta name=\"viewport\" ...>"))),
            Practice("html9", "Поле почты", "Создай обязательное поле для почты с подсказкой placeholder.", "", Lang.HTML,
                listOf(must("""<input[^>]*type\s*=\s*["']email["']""", "Нужен <input type=\"email\">"), must("""\brequired\b""", "Добавь атрибут required"), must("""placeholder\s*=""", "Добавь placeholder"))),
            Practice("html10", "Форма входа", "Сделай форму с полем email, полем пароля и кнопкой отправки.", "", Lang.HTML,
                listOf(must("""<form""", "Нужен тег form"), must("""type\s*=\s*["\']email["\']""", "Поле type=email"), must("""type\s*=\s*["\']password["\']""", "Поле type=password"), must("""<button|type\s*=\s*["\']submit["\']""", "Нужна кнопка отправки"))),
            Practice("html11", "Список ul", "Сделай маркированный список из трёх пунктов.", "", Lang.HTML,
                listOf(must("""<ul""", "Нужен тег ul"), must("""(<li[^>]*>[\s\S]*){3}""", "Нужно три пункта li"))),
            Practice("html12", "Таблица", "Сделай таблицу с заголовком и одной строкой данных.", "", Lang.HTML,
                listOf(must("""<table""", "Нужен тег table"), must("""<th""", "Нужна ячейка заголовка th"), must("""<td""", "Нужна ячейка данных td"))),
            Practice("html13", "Картинка с alt", "Вставь картинку a.png с описанием в alt.", "", Lang.HTML,
                listOf(must("""<img[^>]*src\s*=\s*["\']a\.png""", "Нужен img с src=a.png"), must("""alt\s*=\s*["\'][^"\']+""", "Заполни alt"))),
            Practice("html14", "Семантика", "Собери страницу из header, main и footer.", "", Lang.HTML,
                listOf(must("""<header""", "Нужен header"), must("""<main""", "Нужен main"), must("""<footer""", "Нужен footer"))),
            Practice("html15", "Видео", "Вставь видео v.mp4 с кнопками управления.", "", Lang.HTML,
                listOf(must("""<video[^>]*src\s*=\s*["\']v\.mp4""", "Нужен video с src=v.mp4"), must("""controls""", "Добавь атрибут controls")))
        ),
        "css" to listOf(
            Practice("css1", "Тёмный фон", "Задай для body тёмный фон и светлый цвет текста.", "", Lang.CSS,
                listOf(must("""body\s*\{[^}]*background""", "Нужно правило body с background"), must("""body\s*\{[^}]*[^-]color\s*:""", "Добавь color для текста"))),
            Practice("css2", "Карточка", "Сделай правило .card со скруглением и внутренним отступом.", "", Lang.CSS,
                listOf(must("""\.card\s*\{""", "Нужен селектор .card"), must("""border-radius\s*:""", "Добавь border-radius"), must("""padding\s*:""", "Добавь padding"))),
            Practice("css3", "Центр flex", "Отцентруй содержимое контейнера .box через flex по обеим осям.", "", Lang.CSS,
                listOf(must("""display\s*:\s*flex""", "Нужен display: flex"), must("""justify-content\s*:\s*center""", "Нужен justify-content: center"), must("""align-items\s*:\s*center""", "Нужен align-items: center"))),
            Practice("css4", "Сетка и адаптивность", "Сделай grid из трёх колонок и добавь media-запрос для узких экранов.", "", Lang.CSS,
                listOf(must("""display\s*:\s*grid""", "Нужен display: grid"), must("""grid-template-columns\s*:""", "Нужен grid-template-columns"), must("""@media""", "Добавь @media (max-width: …)"))),
            Practice("css7", "Плавный hover", "Добавь кнопке .btn плавный переход transition.", "", Lang.CSS,
                listOf(must("""\.btn""", "Нужен селектор .btn"), must("""transition\s*:""", "Добавь свойство transition"))),
            Practice("css8", "CSS-переменная", "Объяви переменную --main в :root и используй её как цвет текста через var().", "", Lang.CSS,
                listOf(must(""":root""", "Нужен блок :root"), must("""--main\s*:""", "Объяви переменную --main: ..."), must("""var\(\s*--main\s*\)""", "Используй var(--main)"))),
            Practice("css9", "Центрирование flex", "Отцентрируй содержимое блока .box по горизонтали и вертикали через flex.", "", Lang.CSS,
                listOf(must("""\.box\s*\{""", "Нужен селектор .box"), must("""display\s*:\s*flex""", "display: flex"), must("""justify-content\s*:\s*center""", "justify-content: center"), must("""align-items\s*:\s*center""", "align-items: center"))),
            Practice("css10", "Grid из трёх колонок", "Сделай сетку .grid из трёх равных колонок.", "", Lang.CSS,
                listOf(must("""display\s*:\s*grid""", "display: grid"), must("""grid-template-columns\s*:\s*repeat\s*\(\s*3\s*,\s*1fr\s*\)|grid-template-columns\s*:\s*1fr\s+1fr\s+1fr""", "Три колонки: repeat(3, 1fr)"))),
            Practice("css11", "Кнопка при наведении", "Сделай так, чтобы кнопка button меняла фон при наведении.", "", Lang.CSS,
                listOf(must("""button\s*:hover""", "Нужен селектор button:hover"), must("""background""", "Поменяй background"))),
            Practice("css12", "Скругление и тень", "Скругли углы .card на 12px и добавь тень.", "", Lang.CSS,
                listOf(must("""\.card\s*\{""", "Нужен селектор .card"), must("""border-radius\s*:\s*12px""", "border-radius: 12px"), must("""box-shadow""", "Добавь box-shadow"))),
            Practice("css13", "Переменные", "Объяви переменную --main в :root и используй её как цвет.", "", Lang.CSS,
                listOf(must(""":root\s*\{""", "Нужен :root"), must("""--main\s*:""", "Объяви --main"), must("""var\s*\(\s*--main\s*\)""", "Используй var(--main)"))),
            Practice("css14", "Адаптивность", "Сделай media-запрос для экранов уже 600px, где .menu скрывается.", "", Lang.CSS,
                listOf(must("""@media\s*\([^)]*max-width\s*:\s*600px""", "Нужен @media (max-width: 600px)"), must("""display\s*:\s*none""", "display: none"))),
            Practice("css15", "Анимация", "Создай @keyframes pulse и подключи её через animation.", "", Lang.CSS,
                listOf(must("""@keyframes\s+pulse""", "Нужен @keyframes pulse"), must("""animation\s*:[^;]*pulse""", "Подключи animation: pulse ...")))
        ),
        "cpp" to listOf(
            Practice("cpp1", "Hello, C++", "Напиши программу, которая выводит Hello.", "", Lang.CPP,
                listOf(must("""#include\s*<iostream>""", "Подключи <iostream>"), must("""int\s+main\s*\(""", "Нужна функция int main()"), must("""cout""", "Вывод через std::cout"), must("""return\s+0""", "Заверши программу return 0;"))),
            Practice("cpp2", "Функция add", "Напиши функцию int add(int a, int b), возвращающую сумму.", "", Lang.CPP,
                listOf(must("""int\s+add\s*\(\s*int\s+\w+\s*,\s*int\s+\w+\s*\)""", "Нужно: int add(int a, int b)"), must("""\breturn\b""", "Функция должна возвращать значение"))),
            Practice("cpp3", "Цикл 1–5", "Выведи числа от 1 до 5 циклом for.", "", Lang.CPP,
                listOf(must("""for\s*\(""", "Нужен цикл for"), must("""cout""", "Вывод через std::cout"))),
            Practice("cpp4", "Класс Dog", "Объяви класс Dog с открытым полем name типа std::string.", "", Lang.CPP,
                listOf(must("""class\s+Dog""", "Нужен class Dog"), must("""public\s*:""", "Нужна секция public:"), must("""string\s+name""", "Нужно поле std::string name;"))),
            Practice("cpp5", "Наследование", "Сделай класс Animal и класс Dog, наследник Animal.", "", Lang.CPP,
                listOf(must("""class\s+Animal""", "Нужен class Animal"), must("""class\s+Dog\s*:\s*public\s+Animal""", "Нужно: class Dog : public Animal"))),
            Practice("cpp6", "Чтение строки", "Объяви std::string name и прочитай строку через getline.", "", Lang.CPP,
                listOf(must("""string\s+name""", "Нужна переменная std::string name"), must("""getline\s*\(""", "Используй getline(cin, name)"))),
            Practice("cpp7", "Функция со ссылкой", "Напиши функцию inc, которая принимает int по ссылке и увеличивает его на 1.", "", Lang.CPP,
                listOf(must("""void\s+inc\s*\(\s*int\s*&""", "Нужно: void inc(int& x)"), must("""\+\+|\+=\s*1|=\s*\w+\s*\+\s*1""", "Увеличь значение на 1"))),
            Practice("cpp8", "Вектор", "Создай std::vector<int> v и добавь в него число 5 методом push_back.", "", Lang.CPP,
                listOf(must("""vector\s*<\s*int\s*>\s+v""", "Нужно: vector<int> v;"), must("""push_back\s*\(\s*5\s*\)""", "Добавь 5: v.push_back(5);"))),
            Practice("cpp9", "Структура Point", "Объяви структуру Point с полями int x и int y.", "", Lang.CPP,
                listOf(must("""struct\s+Point""", "Нужна struct Point"), must("""int\s+x""", "Нужно поле int x"), must("""int\s+y""", "Нужно поле int y"))),
            Practice("cpp10", "Switch", "Напиши switch по переменной x с веткой case 1 и break.", "", Lang.CPP,
                listOf(must("""switch\s*\(""", "Нужен switch (x)"), must("""case\s+1\s*:""", "Нужна ветка case 1:"), must("""\bbreak\b""", "Нужен break;"))),
            Practice("cpp11", "Словарь map", "Создай map<string, int> m и положи m[\"a\"] = 1.", "", Lang.CPP,
                listOf(must("""map\s*<\s*string\s*,\s*int\s*>""", "Нужно: map<string, int> m;"), must("""\[\s*"a"\s*\]\s*=\s*1""", "Запиши m[\"a\"] = 1;"))),
            Practice("cpp12", "Лямбда", "Создай лямбду sq, которая возвращает квадрат числа x.", "", Lang.CPP,
                listOf(must("""\[\s*[&=]?\s*\]\s*\(\s*int\s+\w+\s*\)""", "Лямбда: [](int x) { ... }"), must("""\breturn\b""", "Лямбда должна вернуть значение"))),
            Practice("cpp13", "Сортировка", "Отсортируй вектор v функцией sort.", "", Lang.CPP,
                listOf(must("""sort\s*\(\s*v\.begin\(\)\s*,\s*v\.end\(\)""", "Нужно: sort(v.begin(), v.end());"))),
            Practice("cpp14", "Запись в файл", "Открой файл a.txt через ofstream и запиши в него слово.", "", Lang.CPP,
                listOf(must("""ofstream\s+\w+\s*\(\s*"a\.txt"(?:)""", "Нужно: ofstream out(\"a.txt\");"), must("""<<""", "Запиши текст через <<"))),
            Practice("cpp15", "Умный указатель", "Создай unique_ptr на int со значением 5 через make_unique.", "", Lang.CPP,
                listOf(must("""make_unique\s*<\s*int\s*>\s*\(\s*5\s*\)""", "Нужно: make_unique<int>(5)")))
        ),
        "cs" to listOf(
            Practice("cs1", "Hello, C#", "Напиши программу с классом Program, методом Main и выводом Hello.", "", Lang.CSHARP,
                listOf(must("""class\s+Program""", "Нужен class Program"), must("""static\s+void\s+Main\s*\(""", "Нужен static void Main()"), must("""Console\.WriteLine\s*\(""", "Вывод: Console.WriteLine(...)"))),
            Practice("cs2", "Метод Add", "Напиши метод static int Add(int a, int b), возвращающий сумму.", "", Lang.CSHARP,
                listOf(must("""static\s+int\s+Add\s*\(\s*int\s+\w+\s*,\s*int\s+\w+\s*\)""", "Нужно: static int Add(int a, int b)"), must("""\breturn\b""", "Метод должен возвращать значение"))),
            Practice("cs3", "foreach", "Пройди по списку чисел через foreach и выведи каждое.", "", Lang.CSHARP,
                listOf(must("""foreach\s*\(""", "Нужен цикл foreach"), must("""Console\.WriteLine""", "Вывод: Console.WriteLine"))),
            Practice("cs4", "Класс со свойством", "Создай класс Dog со свойством Name ({ get; set; }), создай объект и задай имя.", "", Lang.CSHARP,
                listOf(must("""class\s+Dog""", "Нужен class Dog"), must("""\{\s*get;\s*set;\s*\}""", "Нужно свойство { get; set; }"), must("""new\s+Dog\s*\(""", "Создай объект: new Dog()"))),
            Practice("cs5", "LINQ: чётные", "Есть список nums. Оставь только чётные числа через Where.", "using System.Linq;\n\n", Lang.CSHARP,
                listOf(must("""\.Where\s*\(""", "Используй nums.Where(...)"), must("""%\s*2""", "Чётность проверяется через % 2"))),
            Practice("cs6", "Словарь", "Создай Dictionary<string, int> и добавь в него ключ a.", "", Lang.CSHARP,
                listOf(must("""Dictionary\s*<\s*string\s*,\s*int\s*>""", "Нужен Dictionary<string, int>"), must("""[\[(]\s*"a""", "Добавь ключ \"a\""))),
            Practice("cs7", "try/catch", "Оберни int.Parse в try и поймай FormatException.", "", Lang.CSHARP,
                listOf(must("""\btry\b""", "Нужен блок try"), must("""catch\s*\(\s*FormatException""", "Лови catch (FormatException e)"), must("""int\.Parse\s*\(""", "Используй int.Parse(...)"))),
            Practice("cs8", "Список", "Создай List<int> list и добавь в него число 5.", "", Lang.CSHARP,
                listOf(must("""List\s*<\s*int\s*>""", "Нужно: List<int>"), must("""\.Add\s*\(\s*5\s*\)""", "Добавь 5: list.Add(5);"))),
            Practice("cs9", "Switch", "Напиши switch по x с веткой case 1 и break.", "", Lang.CSHARP,
                listOf(must("""switch\s*\(""", "Нужен switch (x)"), must("""case\s+1\s*:""", "Нужна ветка case 1:"), must("""\bbreak\b""", "Нужен break;"))),
            Practice("cs10", "LINQ Where", "Оставь в списке nums числа больше 2 через Where.", "", Lang.CSHARP,
                listOf(must("""\.Where\s*\(""", "Используй nums.Where(...)"), must("""=>""", "Нужна лямбда n => ..."))),
            Practice("cs11", "Интерфейс", "Объяви интерфейс IAnimal с методом Speak, который ничего не возвращает.", "", Lang.CSHARP,
                listOf(must("""interface\s+IAnimal""", "Нужен interface IAnimal"), must("""void\s+Speak\s*\(""", "Нужно: void Speak();"))),
            Practice("cs12", "Словарь", "Создай Dictionary<string, int> d и запиши d[\"a\"] = 1.", "", Lang.CSHARP,
                listOf(must("""Dictionary\s*<\s*string\s*,\s*int\s*>""", "Нужно: Dictionary<string, int>"), must("""\[\s*"a"\s*\]\s*=\s*1""", "Запиши d[\"a\"] = 1;"))),
            Practice("cs13", "async и await", "Напиши async-метод Work, который возвращает Task и ждёт Task.Delay(100).", "", Lang.CSHARP,
                listOf(must("""async\s+Task""", "Нужно: async Task Work()"), must("""await\s+Task\.Delay""", "Дождись: await Task.Delay(100);"))),
            Practice("cs14", "Запись в файл", "Запиши слово в файл a.txt через File.WriteAllText.", "", Lang.CSHARP,
                listOf(must("""File\.WriteAllText\s*\(\s*"a\.txt"(?:)""", "Нужно: File.WriteAllText(\"a.txt\", ...);"))),
            Practice("cs15", "record", "Объяви record Point с целыми X и Y.", "", Lang.CSHARP,
                listOf(must("""record\s+Point\s*\(""", "Нужно: record Point(int X, int Y);"), must("""int\s+X""", "Нужен параметр int X"), must("""int\s+Y""", "Нужен параметр int Y")))
        )
    )
}
