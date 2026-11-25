# YfeeCode — developer: @yfeeeee
"""Tiny Python REPL for the in-app terminal."""
import code
import io
import contextlib

_console = code.InteractiveConsole({"__name__": "__console__"})
_more = False


def feed(line):
    """Returns (output, needs_more_lines)."""
    global _more
    buf = io.StringIO()
    with contextlib.redirect_stdout(buf), contextlib.redirect_stderr(buf):
        _more = _console.push(line)
    return [buf.getvalue(), bool(_more)]


def reset():
    global _more
    _console.resetbuffer()
    _more = False
