# YfeeCode — developer: @yfeeeee
"""Real Python syntax check. Returns '' or 'line|message' (message in Russian when known)."""
import ast

_RU = [
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
        ast.parse(code)
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
