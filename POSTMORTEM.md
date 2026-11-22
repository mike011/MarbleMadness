# Postmortem: Marble Machine

The problem (see `README`): given 1 to 8 colours, print every sequence of marbles the machine can make. A marble is one colour or two colours. Each colour is used at most once per sequence, and every sequence has at least one marble.

The Java solution, written in April 2012, gave wrong answers and took hours to finish for 8 colours. This document records what went wrong with the approach. It covers the reasoning, not only the bug, so the same mistakes are easier to spot next time.

## Summary

The Java program never produced a sequence containing two or more two-colour marbles, such as `blue+green red+yellow`. For 4 colours it printed 130 sequences instead of 136. The tests asserted 130, so they passed.

The underlying cause was the model: the program treated a sequence as an **ordering of colours** and added two-colour marbles afterwards. It should have treated a sequence as an **ordering of marbles**. The rewrite in Swift builds sequences one marble at a time, gets every count right, and handles 8 colours in about 0.2 seconds.

## Impact

| Colours | Java output | Correct | Missing |
|---:|---:|---:|---:|
| 1 | 1 | 1 | 0 |
| 2 | 5 | 5 | 0 |
| 3 | 24 | 24 | 0 |
| 4 | 130 | 136 | 6 (4.4%) |
| 5 | 815 | 935 | 120 (12.8%) |
| 6 | 5,871 | 7,671 | 1,800 (23.5%) |
| 7 | 47,950 | 73,360 | 25,410 (34.6%) |
| 8 | 438,404 | 801,704 | 363,300 (45.3%) |

The figures for 7 and 8 are computed from the Java algorithm's logic, because the program itself took minutes (7) and hours (8) to run.

Runtime: by the `algorithms` notes, minutes for 7 colours and hours for 8. The Swift version takes about 0.2 s for 8.

## Timeline

| Date | Commit | Event |
|---|---|---|
| 2012-04-10 | `c7f81e7` | Early experiments: permutation and combination helpers, some copied from online examples. |
| 2012-04-11 | `f9dd201` | Problem statement added to `README`. |
| 2012-04-11 | `73f7883` | `MarbleMachine` and its tests committed together. The tests assert 130 / 815 / 5,871 for 4 / 5 / 6 colours. |
| 2012-04-13 | `03382bd` | JUnitPerf added to measure speed. |
| 2012-04-14 | `7e6d1b7` | `algorithms` notes record four attempts. Each problem is described as speed ("ULTRA SLOW", "8 marbles is hours"). |
| 2014-10-24/25 | `11b220b`..`401ec68` | Comment cleanup. The algorithm is unchanged. |
| 2026-11-21 | PR #1 | Bug diagnosed, solution rewritten in Swift, Java removed, diagrams added. |
| 2026-11-21 | PR #2 | Animated comparison published with GitHub Pages. |

## Root causes

### 1. The model was built on colours, not marbles

The answer is a list of marbles, and a marble is one or two colours. Every attempt treated a sequence as an ordering of colours and then patched pairs in:

- **Recursion #3** (the final Java version) generated every ordering of the colours, then glued one neighbouring pair into a two-colour marble. Each glue started again from the plain ordering, so two glues never happened in the same sequence.
- The Recursion #4 notes ask "Create Marble objects instead of a string combo?" That was the right idea, and `Marble.java` already existed with one-colour and two-colour constructors. It was never used.

Stated in terms of marbles, the problem defines its own recursion:

```
a sequence = one marble + (nothing, or a sequence of the remaining colours)
a marble   = one unused colour, or two unused colours
```

### 2. The tests checked the code against itself

The expected counts for 4, 5 and 6 colours went in with the code in the same commit (`73f7883`). They were not worked out from the problem. The tests checked by hand stopped at 3 colours, but two two-colour marbles need at least 4 colours. So every hand-checked test passed, and the larger tests locked the bug in.

4 colours is small enough to count by hand. Using all four colours:

| Grouping | Sequences |
|---|---:|
| 4 single marbles | 24 |
| 1 two-colour marble + 2 singles | 36 |
| 2 two-colour marbles | 6 |
| **Total** | **66** |

Adding the 4 + 18 + 48 sequences that use 1, 2 or 3 colours gives 136, not 130.

### 3. Generate everything, then filter

The pipeline built permutations, removed duplicates with a `TreeSet`, removed reversed pairs (`green+blue` when `blue+green` existed), then glued. Each step cleaned up after the previous one. Most of the work went into making results that were thrown away, which is why 8 colours took hours. The layers also made it hard to see what the code could never produce.

### 4. Speed was diagnosed instead of correctness

The `algorithms` notes record every attempt's problem as speed, and the plan for Recursion #4 asks "Where is the current version the slowest?" The real problem was wrong output. Effort went into making a wrong answer faster.

### 5. The tools came first, not the problem

Recursion #1 was "copy and pasted examples from online", and `src/` collected permutation and combination helpers (`Permuter`, `Combinations`, `ArrayPermutation`, `StringPermutation`). Starting from those made permutations the core of the solution. Permutations are the wrong tool here, because the items being ordered are marbles, and they have to be chosen before they can be ordered.

## What went well

- **Recursion #2 had the right building blocks.** It treated `blue`, `green` and `blue+green` as items. It failed only because it built every combination first and removed reused colours afterwards.
- **Recursion #3 had the right mechanism.** Its permutation code removes a colour once it is used, so it never builds an invalid ordering. Combining that with #2's building blocks gives the correct algorithm.
- **The `Marble` class and the Recursion #4 note** show that the model was already being questioned.
- **The `algorithms` file** recorded each attempt and its failure, which made this postmortem possible years later.

## The fix

The Swift version (`Sources/MarbleMachine/MarbleMachine.swift`):

- Builds each sequence one marble at a time, choosing an unused single colour or an unused pair at every position.
- Tracks used colours in a bitmask, so it never builds an invalid sequence.
- Writes pairs only one way (`blue+green`, never `green+blue`), so it never builds a duplicate. Nothing needs filtering.
- Is tested against a formula worked out independently: for k colours, f(k) = k·f(k−1) + C(k,2)·f(k−2), summed over every non-empty set of colours. The tests also check the README sample exactly, and check every sequence for 1 to 8 colours for validity and uniqueness.

Visual explanations are in `diagrams/`, including the animated comparison: https://mike011.github.io/MarbleMadness/diagrams/marble-machine-animated.html

## Lessons

1. **Describe the shape of the answer first.** Ask what one item of the output is made of, in the problem's own terms (sequence → marbles → colours), and write the recursion from that description.
2. **Work out the smallest case where every rule applies at once**, by hand, before writing code. Here that is 4 colours, the first size where two pairs fit.
3. **Get expected test values from outside the program**: by hand, from a formula, or from a deliberately slow brute-force version.
4. **Never build invalid or duplicate results.** Enforce the rules while building, instead of filtering afterwards.
5. **Ask whether it is correct before asking whether it is fast.**
6. **Start from the problem, not from code you already have.** Reach for a known algorithm only after the problem's structure shows it fits.
