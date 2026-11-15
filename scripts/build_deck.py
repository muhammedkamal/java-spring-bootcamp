#!/usr/bin/env python3
"""Build a session deck from a content spec, reusing the Session 10 slide designs.

Usage:  python3 scripts/build_deck.py scripts/decks/session11.py

A spec file defines OUTPUT (file name under files/) and SLIDES (a list of dicts):
  {"t": "title",     "title", "week", "notes"}
  {"t": "list",      "title", "items": [5 strings], "notes"}
  {"t": "section",   "title", "num", "sub", "notes"}
  {"t": "code",      "title", "label", "code": str, "card", "bullets": [..], "notes"}
  {"t": "cards3",    "title", "cards": [(title, [lines]) x 3], "notes"}
  {"t": "materials", "title", "cards": [(title, [lines]) x 4], "notes"}
Needs python-pptx (pip install python-pptx).
"""
import copy
import runpy
import sys
from pathlib import Path

from pptx import Presentation
from pptx.oxml.ns import qn

ROOT = Path(__file__).resolve().parent.parent
TEMPLATE = ROOT / "files" / "Session10_Layers_DTOs_Validation_and_Swagger.pptx"

# Template slide (1-based) for each slide type in the Session 10 deck
SOURCE = {"title": 1, "list": 2, "section": 4, "code": 5, "cards3": 18, "materials": 19}
MAX_CODE_LINES = 19
MAX_CODE_COLS = 62


def set_lines(shape, lines):
    """Replace the text of a shape, keeping the formatting of its first paragraph and run."""
    body = shape._element.txBody
    paras = body.findall(qn("a:p"))
    template = copy.deepcopy(paras[0])
    for p in paras:
        body.remove(p)
    runs = template.findall(qn("a:r"))
    for extra in runs[1:]:
        template.remove(extra)
    for line in lines:
        p = copy.deepcopy(template)
        r = p.find(qn("a:r"))
        r.find(qn("a:t")).text = line
        body.append(p)


def clone(prs, src):
    slide = prs.slides.add_slide(src.slide_layout)
    tree = slide.shapes._spTree
    for el in list(tree)[2:]:
        tree.remove(el)
    for el in list(src.shapes._spTree)[2:]:
        tree.append(copy.deepcopy(el))
    bg = src._element.cSld.find(qn("p:bg"))
    if bg is not None:
        slide._element.cSld.insert(0, copy.deepcopy(bg))
    return slide


def shapes(slide):
    return {sh.name: sh for sh in slide.shapes}


def fill(slide, spec):
    s = shapes(slide)
    t = spec["t"]
    if "title" in spec:
        set_lines(s["Title 2" if t == "section" else "Title 1"], [spec["title"]])
    if t == "title":
        set_lines(s["TextBox 2"], [spec["week"]])
    elif t == "list":
        items = spec["items"]
        assert len(items) == 5, f"list slide needs 5 items: {spec['title']}"
        for i, item in enumerate(items):
            set_lines(s[f"TextBox {4 + 2 * i}"], [item])
    elif t == "section":
        set_lines(s["TextBox 3"], [spec["num"]])
        set_lines(s["TextBox 4"], [spec["sub"]])
    elif t == "code":
        code = spec["code"].strip("\n").split("\n")
        assert len(code) <= MAX_CODE_LINES, f"too many code lines ({len(code)}): {spec['title']}"
        for line in code:
            assert len(line) <= MAX_CODE_COLS, f"code line too long ({len(line)}): {line}"
        set_lines(s["Code"], code)
        set_lines(s["TextBox 4"], [spec["label"]])
        set_lines(s["TextBox 6"], [spec["card"]])
        set_lines(s["TextBox 7"], spec["bullets"])
    elif t == "cards3":
        for i, (title, lines) in enumerate(spec["cards"]):
            set_lines(s[f"TextBox {4 + 3 * i}"], [title])
            set_lines(s[f"TextBox {5 + 3 * i}"], lines)
    elif t == "materials":
        for i, (title, lines) in enumerate(spec["cards"]):
            set_lines(s[f"TextBox {4 + 3 * i}"], [title])
            set_lines(s[f"TextBox {5 + 3 * i}"], lines)
    notes = spec.get("notes")
    assert notes, f"every slide needs speaker notes: {spec.get('title')}"
    slide.notes_slide.notes_text_frame.text = notes


def drop_original_slides(prs, count):
    ids = prs.slides._sldIdLst
    for sld_id in list(ids)[:count]:
        prs.part.drop_rel(sld_id.get(qn("r:id")))
        ids.remove(sld_id)


def main(spec_path):
    spec = runpy.run_path(spec_path)
    prs = Presentation(str(TEMPLATE))
    originals = list(prs.slides)
    for item in spec["SLIDES"]:
        fill(clone(prs, originals[SOURCE[item["t"]] - 1]), item)
    drop_original_slides(prs, len(originals))
    out = ROOT / "files" / spec["OUTPUT"]
    prs.save(str(out))
    print(f"{out.name}: {len(spec['SLIDES'])} slides")


if __name__ == "__main__":
    main(sys.argv[1])
