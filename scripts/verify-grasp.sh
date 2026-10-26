#!/usr/bin/env bash
# Non-regression check for the GRASP refactor (Requirement 7, Phase 2).
# Runs the same menu script against the reference build and the candidate
# build, each on its own fresh copy of the data folder, and compares the
# console output and the seven data files byte for byte.
#
# Usage: scripts/verify-grasp.sh <reference-classes> [candidate-classes]
#   reference-classes: target/classes compiled from develop before Phase 2
#   candidate-classes: target/classes of the branch under review (default)
set -euo pipefail

if [ $# -lt 1 ]; then
    echo "Usage: $0 <reference-classes> [candidate-classes]" >&2
    exit 2
fi
REFERENCE="$(cd "$1" && pwd)"
CANDIDATE="$(cd "${2:-$(dirname "$0")/../target/classes}" && pwd)"
EVIDENCE="${EVIDENCE_DIR:-$(mktemp -d)}"
MONTH="$(date +%-m)"
YEAR="$(date +%Y)"
FILES="products persons sales accessories promotions returns warranties"

# Session 1: registers data and exercises every menu, with some error paths.
session1() {
    cat <<IN
1
G1
Game
100
5
PC
RPG
E
1
G1
Repetido
100
5
PC
RPG
E
2
C1
Console
200
3
Sony
PS5
9
11
1
CT1
Pad
50
10
WIRELESS
C1
2
CB1
Cable
10
10
1.5
HDMI
3
M1
Card
30
10
64
SSD
C1
4
5
Controller
6
C1
0
12
1
P1
Promo10
2020-01-01
2099-12-31
10
2
P2
Cat
2020-01-01
2099-12-31
CONSOLE
15
3
P3
Bulk
2020-01-01
2099-12-31
3
20
4
5
0
3
4
Bad
U2
abc
4
Ana
U1
3001234567
a@b.co
5
6
7
1
1
1
2
3
0
s
7
1
1
1
0
3
8
9
1
10
1
13
1
SALE-99
1
SALE-1
G1
Defectuoso
2
3
1
4
SALE-1
5
$MONTH
$YEAR
0
14
1
SALE-1
C1
2
3
4
0
99
0
IN
}

# Session 2: restarts the program and reads everything back from the files.
session2() {
    printf '%s\n' 3 5 6 8 11 4 0 12 4 0 13 2 0 14 2 0 0
}

run() {
    local classes="$1" name="$2" dir="$EVIDENCE/$2"
    mkdir -p "$dir/data"
    for f in $FILES; do : > "$dir/data/$f.txt"; done
    printf 'SELLER|S1|Luis|3001234568|EMP1|MORNING\n' > "$dir/data/persons.txt"
    for s in 1 2; do
        ( cd "$dir" && "session$s" | env -u JAVA_TOOL_OPTIONS java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Duser.language=en -Duser.country=US \
            -cp "$classes" com.mycompany.gamezone.Main > "console$s.txt" 2> "errors$s.txt" )
    done
}

run "$REFERENCE" reference
run "$CANDIDATE" candidate

status=0
for rel in console1.txt console2.txt errors1.txt errors2.txt $(for f in $FILES; do echo "data/$f.txt"; done); do
    if cmp -s "$EVIDENCE/reference/$rel" "$EVIDENCE/candidate/$rel"; then
        echo "SAME  $rel"
    else
        echo "DIFF  $rel"
        diff "$EVIDENCE/reference/$rel" "$EVIDENCE/candidate/$rel" | head -20 || true
        status=1
    fi
done
echo "Evidence: $EVIDENCE"
if [ $status -eq 0 ]; then
    echo "PASS: console output and the seven data files match the reference."
else
    echo "FAIL: behavior changed."
fi
exit $status
