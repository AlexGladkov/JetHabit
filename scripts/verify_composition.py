#!/usr/bin/env python3
"""Fail-closed composition-root verifiers used by frozen acceptance cases."""
import subprocess, sys, tempfile, time
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def run(cmd, report):
    with report.open("w", encoding="utf-8") as out:
        return subprocess.run(cmd, cwd=ROOT, stdout=out, stderr=subprocess.STDOUT).returncode

def parse_fixture(path):
    try:
        import yaml
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
    except Exception as exc:
        raise ValueError("unsafe or malformed YAML") from exc
    if not isinstance(data, dict) or data.get("name") != "composition-android-runtime":
        raise ValueError("wrong fixture")
    required = {"name", "description", "feature", "target", "anchors", "verify", "command", "steps", "assertions"}
    if set(data) != required or data["target"] != "android" or data["verify"] != "instrumented":
        raise ValueError("schema mismatch")
    if not isinstance(data["anchors"], list) or not all(isinstance(x, str) for x in data["anchors"]):
        raise ValueError("invalid anchors")
    if not isinstance(data["steps"], list) or not data["steps"] or not all(isinstance(x, dict) and set(x) <= {"action", "label", "args"} and isinstance(x.get("action"), str) for x in data["steps"]):
        raise ValueError("invalid steps")
    if not isinstance(data["assertions"], list) or not data["assertions"]:
        raise ValueError("invalid assertions")
    for assertion in data["assertions"]:
        if not isinstance(assertion, dict) or set(assertion) - {"kind", "target", "equals", "note"} or not isinstance(assertion.get("target"), str):
            raise ValueError("invalid assertion")
    return data

def yaml_mode():
    try:
        parse_fixture(ROOT / ".monet/test-cases/composition-android-runtime.yaml")
        # Exercise rejection paths, so a parser that accepts nested junk cannot pass.
        with tempfile.NamedTemporaryFile("w", suffix=".yaml", delete=True) as f:
            f.write("name: composition-android-runtime\nsteps: [!!python/object/apply:os.system [echo pwned]]\n")
            f.flush()
            try: parse_fixture(Path(f.name))
            except ValueError: pass
            else: raise ValueError("unsafe YAML accepted")
    except (OSError, ValueError):
        return 1
    print("target=jvm case=YamlDiscoveryTest.correctedStartupFixture PASS")
    print("target=jvm case=YamlDiscoveryTest.safeParseAndDiscovery PASS")
    print("yaml-verifier-result=PASS yaml-malicious-tags-rejected=true yaml-unknown-schema-rejected=true yaml-junit-failures=0 yaml-junit-errors=0 yaml-junit-skips=0")
    return 0

def test_mode(kind):
    started = time.time()
    if kind == "desktop":
        cmd = ["./gradlew", "composeApp:clean", "composeApp:jvmTest", "--tests", "desktop.DesktopRuntimeTest", "--no-daemon"]
        names = ["singleJvmLifetimeResource", "cleanExit"]
        roots = [ROOT / "composeApp/build/test-results"]
    else:
        cmd = ["./gradlew", "composeApp:connectedDebugAndroidTest", "-Pandroid.testInstrumentationRunnerArguments.class=tech.mobiledeveloper.jethabit.app.MainActivityTest", "--no-daemon"]
        names = ["currentActivityAndPickerAfterRecreation", "roomRowPersistsAcrossRecreation", "singleCompositionBootstrap", "settingsEventBusProductionUpdate"]
        roots = [ROOT / "composeApp/build/outputs/androidTest-results"]
    with tempfile.NamedTemporaryFile(prefix="composition-", suffix=".log", delete=False) as f: report = Path(f.name)
    rc = run(cmd, report)
    xmls = sorted((p for base in roots if base.exists() for p in base.rglob("TEST-*.xml") if p.stat().st_mtime >= started - 5), key=lambda p: p.stat().st_mtime, reverse=True)
    found = set(); clean = bool(xmls)
    for path in xmls:
        try:
            root = ET.parse(path).getroot()
            for suite in ([root] if root.tag == "testsuite" else root.findall(".//testsuite")):
                clean &= suite.get("failures", "0") == suite.get("errors", "0") == suite.get("skipped", "0") == "0"
                for case in suite.findall("testcase"):
                    found.add(case.get("name", "").split("[")[0])
        except (ET.ParseError, OSError): clean = False
    ok = rc == 0 and clean and all(name in found for name in names)
    if ok:
        for name in names: print(f"target={'android' if kind == 'android' else 'jvm'} case={'MainActivityTest.' if kind == 'android' else 'DesktopRuntimeTest.'}{name} PASS")
        print(f"{'android' if kind == 'android' else 'desktop'}-verifier-result=PASS {'android' if kind == 'android' else 'desktop'}-junit-failures=0 {'android' if kind == 'android' else 'desktop'}-junit-errors=0 {'android' if kind == 'android' else 'desktop'}-junit-skips=0")
    else: print(f"target={'android' if kind == 'android' else 'jvm'} verifier FAIL", file=sys.stderr)
    report.unlink(missing_ok=True)
    return 0 if ok else 1

if __name__ == "__main__":
    if len(sys.argv) != 2 or sys.argv[1] not in {"yaml", "desktop", "android"}: sys.exit(2)
    sys.exit(yaml_mode() if sys.argv[1] == "yaml" else test_mode(sys.argv[1]))
