package aoc.day12;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.*;
import java.util.stream.Collectors;

public class HotSpring {
    public static final char OPERATIONAL_SPRING = '.';
    public static final char UNKNOWN_SPRING = '?';
    public static final char DAMAGED_SPRING = '#';

    private final Map<ConditionRecord, Long> map;
    private List<String> damagedConditionRecordsOfWhichSpringsAreDamaged;


    public static void main(String[] args) {
        var hotSpring = new HotSpring(FileUtil.readFile("day12"));

        ChristmasAssert.test(hotSpring.repairDamagedConditionRecordsOfWhichSpringsAreDamaged(), 7716L);

        hotSpring.unfoldDocument();
        ChristmasAssert.test(hotSpring.repairDamagedConditionRecordsOfWhichSpringsAreDamaged(), 18716325559999L);
    }

    public HotSpring(List<String> damagedConditionRecordsOfWhichSpringsAreDamaged) {
        this.damagedConditionRecordsOfWhichSpringsAreDamaged = damagedConditionRecordsOfWhichSpringsAreDamaged;
        map = new HashMap<>();
    }

    public long repairDamagedConditionRecordsOfWhichSpringsAreDamaged() {
        var t0 = System.currentTimeMillis();
        long sumOfHowBrokenTheseConditionRecordsReallyAre = 0L;
        for (var damagedConditionRecordOfWhichSpringsAreDamaged : damagedConditionRecordsOfWhichSpringsAreDamaged) {
            var splitRecord = damagedConditionRecordOfWhichSpringsAreDamaged.split("\\s+");
            var springs = splitRecord[0].trim();
            var groups = Arrays.stream(splitRecord[1].split(",")).map(Long::parseLong).toList();
            sumOfHowBrokenTheseConditionRecordsReallyAre += countAllPossibleArrangements(springs, groups);
        }
        System.out.println(STR. "Repair of damaged springs records of which springs are damaged result: \{ sumOfHowBrokenTheseConditionRecordsReallyAre } (\{ System.currentTimeMillis() - t0 }ms)" );
        return sumOfHowBrokenTheseConditionRecordsReallyAre;
    }

    /**
     * Utilizing Dynamic Programming / Recursive Memoization to make the enormous computation possible.
     * A recursive algorithm that maps conclusions so if it crosses it again, it can simply collect the result from the HashMap
     * @param springRecord The damaged condition record of damaged springs
     * @param groups The contiguous groups
     * @return Number of different possible arrangements of OPERATIONAL and DAMAGED springs
     */
    public long countAllPossibleArrangements(String springRecord, List<Long> groups) {
        ConditionRecord conditionRecord = new ConditionRecord(springRecord, groups);
        if (map.containsKey(conditionRecord)) {
            // = recursive memoization i.e. already mapped, so dont compute again!
            return map.get(conditionRecord);
        }

        if (springRecord.isBlank()) {
            // End of spring record. If group is not empty, we can assume another group is being filled
            return groups.isEmpty() ? 1 : 0;
        }

        char firstChar = springRecord.charAt(0);
        long arrangements = 0L;
        if (firstChar == OPERATIONAL_SPRING) {
            // Skipping OPERATION('.') springs
            arrangements = countAllPossibleArrangements(springRecord.substring(1), groups);
        } else if (firstChar == UNKNOWN_SPRING) {
            // For UNKNOWN('?') springs, recursively compute all options
            arrangements += countAllPossibleArrangements(OPERATIONAL_SPRING + springRecord.substring(1), groups);
            arrangements += countAllPossibleArrangements(DAMAGED_SPRING + springRecord.substring(1), groups);
        } else if (!groups.isEmpty()) {
            // For DAMAGED('#') springs, compute the correct signs to match the next GROUP
            long nrDamaged = groups.get(0);
            if (nrDamaged <= springRecord.length() && springRecord.chars().limit(nrDamaged).allMatch(c -> c == DAMAGED_SPRING || c == UNKNOWN_SPRING)) {
                List<Long> newGroups = groups.subList(1, groups.size());
                if (nrDamaged == springRecord.length()) {
                    arrangements = newGroups.isEmpty() ? 1 : 0;
                } else if (springRecord.charAt((int) nrDamaged) == OPERATIONAL_SPRING) {
                    arrangements = countAllPossibleArrangements(springRecord.substring((int) nrDamaged + 1), newGroups);
                } else if (springRecord.charAt((int) nrDamaged) == UNKNOWN_SPRING) {
                    arrangements = countAllPossibleArrangements(OPERATIONAL_SPRING + springRecord.substring((int) nrDamaged + 1), newGroups);
                }
            }
        }
        map.put(conditionRecord, arrangements);
        return arrangements;
    }

    public void unfoldDocument() {
        System.out.println("Unfolding the document....");
        damagedConditionRecordsOfWhichSpringsAreDamaged = damagedConditionRecordsOfWhichSpringsAreDamaged.stream()
                .map(s -> {
                    var split = s.split(" ");
                    return (split[0] + UNKNOWN_SPRING).repeat(4) + split[0]
                            + " "
                            + (split[1] + ",").repeat(4) + split[1];
                })
                .collect(Collectors.toList());
    }

    record ConditionRecord(String springConditions, List<Long> groups) { }
}
