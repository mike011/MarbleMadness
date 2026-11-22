"""Render the Marble Madness before/after diagrams as PNGs."""
import sys
from math import comb, factorial

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch

OUT = sys.argv[1]

SURFACE = "#fcfcfb"
INK = "#0b0b0b"
INK2 = "#52514e"
MUTED = "#8a8984"
GRID = "#e4e3df"
BLUE = "#2a78d6"     # correct / new
RED = "#e34948"      # wrong / missing
ORANGE = "#eb6834"
GREEN = "#008300"
BOX = "#f3f2ef"

plt.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "text.color": INK,
    "figure.facecolor": SURFACE,
    "axes.facecolor": SURFACE,
    "savefig.facecolor": SURFACE,
})


def box(ax, x, y, text, w, h, edge=GRID, fill=BOX, color=INK, size=11,
        weight="normal", dashed=False, lw=1.5, family=None):
    ax.add_patch(FancyBboxPatch(
        (x - w / 2, y - h / 2), w, h,
        boxstyle="round,pad=0.02,rounding_size=0.12",
        linewidth=lw, edgecolor=edge, facecolor=fill,
        linestyle=(0, (4, 3)) if dashed else "solid"))
    ax.text(x, y, text, ha="center", va="center", color=color, fontsize=size,
            weight=weight, family=family)


def arrow(ax, x1, y1, x2, y2, color=INK2, dashed=False, lw=1.4):
    ax.add_patch(FancyArrowPatch(
        (x1, y1), (x2, y2), arrowstyle="-|>", mutation_scale=12,
        color=color, lw=lw, linestyle=(0, (4, 3)) if dashed else "solid",
        shrinkA=2, shrinkB=2))


def canvas(w, h):
    fig, ax = plt.subplots(figsize=(w, h), dpi=150)
    ax.set_xlim(0, w)
    ax.set_ylim(0, h)
    ax.axis("off")
    return fig, ax


# ---------------------------------------------------------------- counts
def correct_count(n):
    f = [1] + [0] * n
    for k in range(1, n + 1):
        f[k] = k * f[k - 1] + (comb(k, 2) * f[k - 2] if k >= 2 else 0)
    return sum(comb(n, k) * f[k] for k in range(1, n + 1))


def old_count(n):
    """Old algorithm: every ordering of every subset, with at most ONE pair."""
    g = lambda k: factorial(k) + comb(k, 2) * factorial(k - 1) if k >= 2 else 1
    return sum(comb(n, k) * g(k) for k in range(1, n + 1))


# The old Java tests asserted exactly these numbers for 4, 5 and 6 colours.
assert [old_count(n) for n in (4, 5, 6)] == [130, 815, 5871]
assert [correct_count(n) for n in range(1, 9)] == \
    [1, 5, 24, 136, 935, 7671, 73360, 801704]

# ------------------------------------------------- 1. old algorithm & bug
fig, ax = canvas(14, 9.4)
ax.text(0.5, 9.0, "Before: the Java \"Recursion #3\" algorithm",
        fontsize=19, weight="bold", va="center")
ax.text(0.5, 8.55, "It builds colour orderings first, then tries to merge "
        "neighbours into two-colour marbles afterwards.",
        fontsize=12, color=INK2, va="center")

steps = [
    ("1  Indexes", "\"0123\""),
    ("2  Every permutation\nof every suffix", "n! strings each,\nmany repeated"),
    ("3  Dedupe in a\nTreeSet, map to colours", "\"blue green red yellow\""),
    ("4  addAllCombos()\nmerge ONE adjacent pair", "one replace per\nsequence, never two"),
]
xs = [1.9, 5.2, 8.5, 11.8]
for (title, sub), x in zip(steps, xs):
    edge = RED if title.startswith("4") else GRID
    box(ax, x, 7.35, title, 2.9, 1.0, edge=edge, weight="bold", size=11,
        lw=2.2 if edge == RED else 1.5)
    ax.text(x, 6.55, sub, ha="center", va="top", fontsize=10, color=INK2)
for a, b in zip(xs, xs[1:]):
    arrow(ax, a + 1.47, 7.35, b - 1.47, 7.35)

# worked example with 4 colours
ax.text(0.5, 5.45, "Worked example: 4 colours, starting from "
        "\"blue green red yellow\"", fontsize=13, weight="bold", va="center")
box(ax, 2.6, 4.2, "blue green red yellow", 3.6, 0.7, family="monospace",
    size=11)
merges = [
    ("blue+green red yellow", 5.0),
    ("blue green+red yellow", 4.2),
    ("blue green red+yellow", 3.4),
]
for text, y in merges:
    arrow(ax, 4.45, 4.2, 6.2, y)
    box(ax, 8.15, y, text, 3.8, 0.62, edge=BLUE, family="monospace", size=11)
    ax.text(10.2, y, "generated", color=BLUE, fontsize=10, va="center")

box(ax, 8.15, 2.1, "blue+green red+yellow", 3.8, 0.62, edge=RED, fill=SURFACE,
    family="monospace", size=11, dashed=True, lw=2)
arrow(ax, 8.15, 3.05, 8.15, 2.45, color=RED, dashed=True)
ax.text(8.45, 2.75, "needs a second merge", color=RED, fontsize=10,
        va="center")
ax.text(10.2, 2.1, "NEVER generated", color=RED, fontsize=11, weight="bold",
        va="center")

ax.text(0.5, 1.05,
        "Bug: any sequence with two or more two-colour marbles is missing. "
        "4 colours gives 130 instead of 136;\n"
        "6 colours gives 5,871 instead of 7,671. The old tests asserted these "
        "wrong counts, so they passed.",
        fontsize=11.5, color=INK, va="center")
ax.text(0.5, 0.3,
        "Slow too: generating and deduping permutations of every suffix took "
        "minutes for 7 colours and hours for 8.",
        fontsize=11.5, color=INK2, va="center")
fig.savefig(f"{OUT}/1-before-old-algorithm.png", bbox_inches="tight")
plt.close(fig)

# ---------------------------------------------- 2. new algorithm (tree)
fig, ax = canvas(19.6, 10.2)
ax.text(0.5, 9.8, "After: build each sequence one marble at a time",
        fontsize=19, weight="bold", va="center")
ax.text(0.5, 9.35,
        "At each position, pick any unused colour (single marble) or any unused "
        "pair i < j (two-colour marble). A bitmask tracks used colours.",
        fontsize=12, color=INK2, va="center")
ax.text(0.5, 8.95,
        "Shown: the pass that builds 2-marble sequences from 3 colours. "
        "Every leaf is a unique, valid sequence, so nothing is filtered.",
        fontsize=12, color=INK2, va="center")

root_y, mid_y, leaf_y = 7.9, 5.6, 2.9
box(ax, 9.8, root_y, "start\nused = 000", 1.9, 0.85, weight="bold")

children = [
    ("blue", "001", ["green", "red", "green+red"]),
    ("green", "010", ["blue", "red", "blue+red"]),
    ("red", "100", ["blue", "green", "blue+green"]),
    ("blue+green", "011", ["red"]),
    ("blue+red", "101", ["green"]),
    ("green+red", "110", ["blue"]),
]
leaf_gap = 1.26
group_gap = 0.3
widths = [max(len(l) * leaf_gap, 1.9) for _, _, l in children]
total = sum(widths) + group_gap * (len(children) - 1)
left = 0.6 + (19.0 - total) / 2
for (name, mask, leaves), width in zip(children, widths):
    pair = "+" in name
    cx = left + width / 2
    xs_leaves = [cx + (i - (len(leaves) - 1) / 2) * leaf_gap
                 for i in range(len(leaves))]
    left += width + group_gap
    col = ORANGE if pair else BLUE
    arrow(ax, 9.8, root_y - 0.45, cx, mid_y + 0.42, color=MUTED, lw=1.1)
    box(ax, cx, mid_y, f"{name}\nused={mask}", 1.62 if pair else 1.2, 0.8,
        edge=col, lw=2, size=10, family="monospace")
    for lx, leaf in zip(xs_leaves, leaves):
        lcol = ORANGE if "+" in leaf else BLUE
        arrow(ax, cx, mid_y - 0.42, lx, leaf_y + 0.75, color=MUTED, lw=1.0)
        ax.add_patch(FancyBboxPatch(
            (lx - 0.55, leaf_y - 0.7), 1.1, 1.4,
            boxstyle="round,pad=0.02,rounding_size=0.1",
            linewidth=1.2, edgecolor=lcol, facecolor=BOX))
        ax.text(lx, leaf_y + 0.3, leaf, ha="center", va="center",
                fontsize=8.5 if "+" in leaf else 9.5, family="monospace")
        ax.text(lx, leaf_y - 0.35, f"{name}\n{leaf}", ha="center",
                va="center", fontsize=6.8, color=INK2, family="monospace")

ax.text(0.2, 6.85, "marble 1", fontsize=10, color=MUTED, rotation=90,
        va="center")
ax.text(0.2, 4.3, "marble 2", fontsize=10, color=MUTED, rotation=90,
        va="center")

# legend (two series of boxes)
ax.add_patch(FancyBboxPatch((12.9, 7.75), 0.35, 0.3,
                            boxstyle="round,pad=0.01,rounding_size=0.05",
                            edgecolor=BLUE, facecolor=BOX, lw=2))
ax.text(13.4, 7.9, "single-colour marble", fontsize=10.5, va="center")
ax.add_patch(FancyBboxPatch((12.9, 7.25), 0.35, 0.3,
                            boxstyle="round,pad=0.01,rounding_size=0.05",
                            edgecolor=ORANGE, facecolor=BOX, lw=2))
ax.text(13.4, 7.4, "two-colour marble (i < j only)", fontsize=10.5,
        va="center")

ax.text(0.5, 1.55, "Why it is correct and fast", fontsize=13, weight="bold",
        va="center")
notes = [
    "• Pairs are only made with i < j, so \"green+blue\" is never produced "
    "alongside \"blue+green\": no duplicates, no reverse check.",
    "• Two-colour marbles can be picked at every position, so "
    "\"blue+green red+yellow\" is reached naturally (the old bug).",
    "• One pass per marble count (1, 2, ... n) gives the README's output "
    "order. 8 colours: 801,704 sequences in about 0.2 s.",
]
for i, note in enumerate(notes):
    ax.text(0.5, 1.05 - i * 0.42, note, fontsize=11, va="center")
fig.savefig(f"{OUT}/2-after-new-algorithm.png", bbox_inches="tight")
plt.close(fig)

# ------------------------------------------- 3. what the old code missed
ns = list(range(1, 9))
correct = [correct_count(n) for n in ns]
old = [old_count(n) for n in ns]
missed_pct = [100 * (c - o) / c for c, o in zip(correct, old)]

fig, ax = plt.subplots(figsize=(10, 5.6), dpi=150)
bars = ax.bar(ns, missed_pct, width=0.62, color=RED, zorder=3)
for b in bars:  # surface gap / rounded top feel kept simple: thin edge
    b.set_edgecolor(SURFACE)
    b.set_linewidth(2)
for n, pct, c, o in zip(ns, missed_pct, correct, old):
    if pct > 0:
        ax.text(n, pct + 1.2, f"{pct:.1f}%\n{c - o:,} missing", ha="center",
                va="bottom", fontsize=9.5, color=INK)
    else:
        ax.text(n, 1.2, "none", ha="center", va="bottom", fontsize=9.5,
                color=INK2)
ax.set_xticks(ns)
ax.set_xticklabels([f"{n}\n({c:,})" for n, c in zip(ns, correct)],
                   fontsize=9.5, color=INK2)
ax.set_xlabel("Input (number of colours), with the correct sequence count",
              color=INK2, fontsize=10.5, labelpad=8)
ax.set_ylabel("Share of valid sequences missed", color=INK2, fontsize=10.5)
ax.set_ylim(0, max(missed_pct) * 1.25)
ax.yaxis.set_major_formatter(matplotlib.ticker.PercentFormatter(decimals=0))
ax.tick_params(axis="y", colors=INK2, labelsize=9.5, length=0)
ax.tick_params(axis="x", length=0)
ax.grid(axis="y", color=GRID, lw=0.8, zorder=0)
for side in ("top", "right", "left"):
    ax.spines[side].set_visible(False)
ax.spines["bottom"].set_color(GRID)
fig.subplots_adjust(top=0.82)
fig.text(0.02, 0.965, "The old solution missed more as colours were added",
         ha="left", fontsize=15, weight="bold")
fig.text(0.02, 0.905,
         "It never produced sequences with two or more two-colour marbles. "
         "4 to 6 colours match the old tests' counts (130, 815, 5,871);\n"
         "7 and 8 are computed from the same logic, since the old code took "
         "minutes to hours to run.", ha="left", fontsize=9.5, color=INK2,
         va="center")
fig.savefig(f"{OUT}/3-missed-sequences.png", bbox_inches="tight")
plt.close(fig)
print("ok")
