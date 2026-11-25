# YfeeCode — developer: @yfeeeee
"""Runs a user script in the :bot process. Output goes to a log file."""
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
        runpy.run_path(script_path, run_name="__main__")
        print("[done] exit 0")
    except SystemExit as e:
        print("[done] exit %s" % (e.code,))
    except BaseException:
        traceback.print_exc()
        print("[done] crashed")
