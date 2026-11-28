// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Short cheat sheets for the editor "Шпаргалка" button. */
object Cheats {
    private val py = """Кавычки
  'текст' или "текст" одинаково. Внутри "..." можно ' и наоборот.
  Внутри f-строки используй другие кавычки: f"Привет, {d['name']}"
  Многострочный текст: три кавычки
  Экранирование: \n новая строка, \" кавычка, \\ слэш

Цитата в Telegram (parse_mode="HTML")
  <blockquote>текст цитаты</blockquote>
  <blockquote expandable>длинная, сворачиваемая</blockquote>
  <b>жирный</b> <i>курсив</i> <code>код</code> <pre>блок</pre>
  <a href="https://адрес">ссылка</a>
  <tg-spoiler>спойлер</tg-spoiler>
  <tg-emoji emoji-id="ID">🙂</tg-emoji>
  Символы < > & в обычном тексте: &lt; &gt; &amp;
  <pre><code class="language-python">код</code></pre>
  <a href="tg://user?id=123">ссылка на пользователя</a>

Rich-сообщения (новые Bot API, sendRichMessage с полем html)
  <table bordered striped compact>   таблица с рамками, полосами, компактная
    <tr><td valign="middle">Имя</td><td><b>значение</b></td></tr>
  </table>
  <details><summary>Заголовок</summary>скрыто</details>   закрытый список
  <details open><summary>...</summary>...</details>        открытый список
  Работает только при отправке как rich-сообщение, в обычном parse_mode HTML таблицы и details не рисуются.
  Ещё в rich: <h1>заголовок</h1>  <ol><li>нумерованный</li></ol>  <ul><li>маркированный</li></ul>
  Чек-лист: <ul><li><input type="checkbox">пункт</li></ul>
  <details open><summary><b>Заголовок</b></summary>...</details>   открытый свёрнутый блок
  В юзерботе (как у модуля Always Online): <p>абзац</p> и <details><summary>Commands</summary><p>...</p></details> через utils.answer; зависит от версии юзербота.

Основа
  def f(x): ...        функция
  async def / await    асинхронность
  for i in range(3):   цикл
  try: ... except Exception as e:
  if a == b:           сравнение это ==, присваивание =

Частые ошибки
  Забыл двоеточие после if, for, def, class
  Разные отступы в блоке (всегда 4 пробела)
  await вне async def
  Не закрыта скобка или кавычка
"""
    private val js = """Кавычки
  'текст', "текст" и `шаблон ${'$'}{x}` (обратные кавычки)
Основа
  const / let          переменные
  () => {}             стрелочная функция
  async / await        асинхронность
  arr.map / filter / reduce
  JSON.parse / JSON.stringify
Ошибки
  Забыл ; или } или )
  === вместо == для сравнения
  await вне async-функции
"""
    private val html = """Структура
  <!DOCTYPE html> <html> <head> <body>
  <h1>..</h1> <p>..</p> <a href="..">..</a> <img src="..">
  <ul><li>..</li></ul>   <div> <span>
  <button onclick="f()">..</button>
Кавычки у атрибутов: href="адрес"
Каждый открытый тег нужно закрыть: <p>..</p>
"""
    private val css = """Правило
  селектор { свойство: значение; }
  .класс  #id  тег  a:hover
  color  background  margin  padding  border
  display: flex; gap: 8px; justify-content: center;
Ошибки
  Забыл ; или }
  Точка перед классом, решётка перед id
"""
    private val cpp = """Основа
  #include <iostream>
  int main() { std::cout << "Привет" << std::endl; return 0; }
  std::vector<int> v;  v.push_back(1);
  for (auto x : v) { }
  int& ссылка,  int* указатель
Ошибки
  Забыл ; в конце команды
  Не подключил заголовок #include
  Забыл std:: или using namespace std;
"""
    private val cs = """Основа
  using System;
  class Program { static void Main() { Console.WriteLine("Привет"); } }
  var list = new List<int>();   list.Add(1);
  foreach (var x in list) { }
  async Task Run() { await Task.Delay(100); }
Ошибки
  Забыл ; в конце команды
  Строки только в двойных кавычках "..."
  Нет using нужного пространства имён
"""

    fun forLang(l: Lang): String = when (l) {
        Lang.PYTHON -> py; Lang.JS -> js; Lang.HTML -> html; Lang.CSS -> css; Lang.CPP -> cpp; Lang.CSHARP -> cs
        else -> "Для этого типа файла шпаргалки пока нет."
    }
}
