#!/usr/bin/env python3
"""Fail-closed composition-root verifiers used by frozen acceptance cases."""
import os, subprocess, sys, tempfile
import xml.etree.ElementTree as ET
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
FIXTURE = ROOT / "composeApp/src/jvmTest/resources/android-startup-composition-root-duplicate-red.yaml"


def parse_fixture(path):
    try:
        import yaml
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
    except Exception as exc:
        raise ValueError("unsafe or malformed YAML") from exc

    required = {"name", "description", "feature", "target", "anchors", "verify", "steps", "assertions"}
    if not isinstance(data, dict) or set(data) != required:
        raise ValueError("schema mismatch")
    if data["name"] != "android-startup-composition-root-duplicate-red":
        raise ValueError("wrong fixture")
    for key in ("description", "feature"):
        if not isinstance(data[key], str) or not data[key]:
            raise ValueError("invalid metadata")
    if data["target"] != "android" or data["verify"] != "manual":
        raise ValueError("wrong target")

    if (not isinstance(data["anchors"], list) or not data["anchors"]
            or not all(isinstance(value, str) and value for value in data["anchors"] )):
        raise ValueError("invalid anchors")
    if not isinstance(data["steps"], list) or not data["steps"]:
        raise ValueError("invalid steps")
    for step in data["steps"]:
        if not isinstance(step, dict) or set(step) - {"action", "label", "args"}:
            raise ValueError("invalid nested step")
        if not isinstance(step.get("action"), str) or not step["action"]:
            raise ValueError("invalid nested step")
        if "label" in step and (not isinstance(step["label"], str) or not step["label"]):
            raise ValueError("invalid step label")
        if "args" in step and not isinstance(step["args"], dict):
            raise ValueError("invalid args")
        if "args" in step and not all(isinstance(key, str) for key in step["args"]):
            raise ValueError("invalid args")

    if not isinstance(data["assertions"], list) or not data["assertions"]:
        raise ValueError("invalid assertions")
    for assertion in data["assertions"]:
        if not isinstance(assertion, dict) or set(assertion) - {"kind", "target", "equals", "note"}:
            raise ValueError("invalid assertion")
        if assertion.get("kind") not in {"visible", "gone", "text"}:
            raise ValueError("invalid assertion kind")
        if not isinstance(assertion.get("target"), str) or not assertion["target"]:
            raise ValueError("invalid assertion target")
        for key in ("equals", "note"):
            if key in assertion and not isinstance(assertion[key], str):
                raise ValueError("invalid assertion value")
    return data


def yaml_mode():
    try:
        parsed = parse_fixture(FIXTURE)
        if parsed["name"] != "android-startup-composition-root-duplicate-red":
            raise ValueError("fixture discovery mismatch")
        with tempfile.NamedTemporaryFile("w", suffix=".yaml", delete=True) as f:
            f.write("name: android-startup-composition-root-duplicate-red\nsteps: [!!python/object/apply:os.system [echo pwned]]\n")
            f.flush()
            try:
                parse_fixture(Path(f.name))
            except ValueError:
                pass
            else:
                raise ValueError("unsafe YAML accepted")
    except (OSError, ValueError):
        return 1
    print("target=jvm case=YamlDiscoveryTest.correctedStartupFixture PASS")
    print("target=jvm case=YamlDiscoveryTest.safeParseAndDiscovery PASS")
    return 0

def test_mode(kind):
    if kind == "desktop":
        out = ROOT / "composeApp/build/test-results"; names = ["singleJvmLifetimeResource","cleanExit"]; cls = "desktop.DesktopRuntimeTest"
        cmd = ["./gradlew", "composeApp:jvmTest", "--tests", cls, "--no-daemon"]
    else:
        out = ROOT / "composeApp/build/outputs/androidTest-results"; names = ["currentActivityAndPickerAfterRecreation","roomRowPersistsAcrossRecreation","singleCompositionBootstrap","settingsEventBusProductionUpdate"]; cls = "tech.mobiledeveloper.jethabit.app.MainActivityTest"
        cmd = ["./gradlew", "composeApp:connectedDebugAndroidTest", "-Pandroid.testInstrumentationRunnerArguments.class=" + cls, "--no-daemon"]
    if out.exists():
        for p in out.rglob("TEST-*.xml"): p.unlink()
    with tempfile.NamedTemporaryFile(prefix="composition-", suffix=".log", delete=False) as f:
        report = Path(f.name)
        env = dict(os.environ); env["ANDROID_SERIAL"] = "emulator-5554"
        rc = subprocess.run(cmd, cwd=ROOT, stdout=f, stderr=subprocess.STDOUT, env=env).returncode
    found = []; clean = True
    for path in out.rglob("TEST-*.xml") if out.exists() else []:
        try:
            root = ET.parse(path).getroot()
            suites = [root] if root.tag == "testsuite" else root.findall(".//testsuite")
            for suite in suites:
                clean &= all(suite.get(k,"0") == "0" for k in ("failures","errors","skipped"))
                for case in suite.findall("testcase"):
                    if case.get("classname") == cls: found.append(case.get("name","").split("[")[0])
        except (ET.ParseError, OSError): clean = False
    ok = rc == 0 and clean and len(found) == len(set(found)) and all(n in found for n in names)
    if ok:
        for n in names: print(f"target={'android' if kind == 'android' else 'jvm'} case={cls.split('.')[-1]}.{n} PASS")
    else: print(f"target={'android' if kind == 'android' else 'jvm'} verifier FAIL", file=sys.stderr)
    report.unlink(missing_ok=True)
    return 0 if ok else 1

if __name__ == "__main__":
    if len(sys.argv) != 2 or sys.argv[1] not in {"yaml","desktop","android"}: sys.exit(2)
    sys.exit(yaml_mode() if sys.argv[1] == "yaml" else test_mode(sys.argv[1]))
