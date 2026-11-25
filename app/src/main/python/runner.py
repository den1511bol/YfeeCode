# YfeeCode — developer: @yfeeeee
"""Runs a user script in the :bot process. Output goes to a log file."""
import io
import os
import runpy
import sys
import traceback


class _LogWriter:
    def __init__(self, path):
        self.f = open(path, "a", encoding="utf-8", buffering=1)

    def write(self, s):
        self.f.write(s)
        self.f.flush()
        return len(s)

    def flush(self):
        self.f.flush()

    def isatty(self):
        return False


def _prepare_thread_env():
    """User scripts run in a worker thread: give them an event loop and tolerate signal calls."""
    import asyncio
    import signal

    asyncio.set_event_loop(asyncio.new_event_loop())

    _orig = signal.signal

    def _safe_signal(sig, handler):
        try:
            return _orig(sig, handler)
        except ValueError:
            return None

    signal.signal = _safe_signal
    try:
        import asyncio.unix_events as ue
        _orig_add = ue._UnixSelectorEventLoop.add_signal_handler

        def _safe_add(self, sig, cb, *args):
            try:
                return _orig_add(self, sig, cb, *args)
            except (ValueError, RuntimeError, NotImplementedError):
                return None

        ue._UnixSelectorEventLoop.add_signal_handler = _safe_add
    except Exception:
        pass
    # no console: input() would block forever
    sys.stdin = io.StringIO("")


def run(script_path, log_path):
    w = _LogWriter(log_path)
    sys.stdout = w
    sys.stderr = w
    workdir = os.path.dirname(script_path)
    os.chdir(workdir)
    if workdir not in sys.path:
        sys.path.insert(0, workdir)
    print("[run] " + os.path.basename(script_path))
    try:
        _prepare_thread_env()
        runpy.run_path(script_path, run_name="__main__")
        print("[done] exit 0")
    except SystemExit as e:
        print("[done] exit %s" % (e.code,))
    except BaseException:
        traceback.print_exc()
        print("[done] crashed")
