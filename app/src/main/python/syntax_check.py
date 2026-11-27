# YfeeCode — developer: @yfeeeee
"""Real Python syntax check. Returns '' or 'line|message' (message in Russian when known)."""
import ast

_RU = [
    ("Maybe you meant '==' or ':='", "похоже, нужно == (сравнение), а не = (присваивание)"),
    ("Perhaps you forgot a comma", "возможно, пропущена запятая между значениями"),
    ("expected 'except' or 'finally' block", "после try нужен except или finally"),
    ("'await' outside", "await можно писать только внутри async def"),
    ("'return' outside function", "return можно писать только внутри функции"),
    ("'break' outside loop", "break работает только внутри цикла for или while"),
    ("'continue' not properly in loop", "continue работает только внутри цикла for или while"),
    ("invalid decimal literal", "число и буквы слиплись: проверь пробелы и опечатки"),
    ("unexpected character after line continuation", "после \\ в конце строки не должно быть символов"),
    ("positional argument follows keyword argument", "аргументы без имени нужно писать раньше аргументов с именем"),
    ("f-string", "ошибка в f-строке: проверь фигурные скобки и кавычки"),
    ("invalid character", "в коде странный символ (например, косая кавычка из мессенджера)"),
    ("invalid non-printable", "в коде невидимый символ: сотри и набери заново"),
    ("did you forget parentheses", "кажется, не хватает скобок"),
    ("closing parenthesis", "скобки не совпадают по виду: ( ) [ ] { }"),
    ("expected 'else' after 'if' expression", "после условия в одну строку нужен else"),
    ("cannot assign to literal", "слева от = должно быть имя переменной, а не число или текст"),
    ("expected ':'", "ожидается «:»"),
    ("unterminated string literal", "незакрытая строка (не хватает кавычки)"),
    ("unterminated triple-quoted", "незакрытая тройная кавычка"),
    ("was never closed", "скобка не закрыта"),
    ("unmatched", "лишняя закрывающая скобка"),
    ("unexpected indent", "лишний отступ"),
    ("unindent does not match", "отступ не совпадает с предыдущими"),
    ("expected an indented block", "после этой строки нужен блок с отступом"),
    ("invalid syntax", "неверный синтаксис"),
    ("Missing parentheses in call to 'print'", "print нужно вызывать со скобками"),
    ("cannot assign to", "нельзя присвоить значение этому выражению"),
    ("EOL while scanning", "незакрытая строка"),
]


def check(code):
    try:
        compile(code, "<editor>", "exec", dont_inherit=True)
        return ""
    except SyntaxError as e:
        msg = e.msg or "ошибка"
        for k, v in _RU:
            if k in msg:
                msg = v
                break
        return "%d|%s" % (e.lineno or 1, msg)
    except Exception:
        return ""
