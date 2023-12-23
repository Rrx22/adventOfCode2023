package aoc.day19;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GearSorter {

    void main() {
        var sorter = new GearSorter();
        System.out.println(sorter.sortGears());
    }

    private final Map<String, List<Rule>> workFlows;
    private final List<Gear> gears;

    GearSorter() {
        workFlows = new HashMap<>();
        gears = new ArrayList<>();
        collectWorkflowsAndGears(FileUtil.readFile("day19"));
    }

    long sortGears() {
        return gears.stream().filter(this::validate).mapToLong(Gear::value).sum();
    }

    private boolean validate(Gear gear) {
        var workFlow = workFlows.get("in");
        int counter = 0;
        while (true) {
            var rule = workFlow.get(counter++);
            if (rule.check(gear)) {
                if (rule.outcome.equals("A")) {
                    return true;
                } else if (rule.outcome.equals("R")) {
                    return false;
                } else {
                    workFlow = workFlows.get(rule.outcome);
                    counter = 0;
                }
            }
        }
    }

    private void collectWorkflowsAndGears(List<String> instructions) {
        boolean timeToMapGears = false;
        for (var line : instructions) {
            if (line.isEmpty()) {
                timeToMapGears = true;
                continue;
            }
            if (timeToMapGears) {
                gears.add(mapGear(line));
            } else {
                String[] split = line.split("\\{");
                workFlows.put(split[0], mapRules(split[1]));
            }

        }
    }

    private Gear mapGear(String line) {
        Pattern pattern = Pattern.compile("\\d+");
        int[] values = new int[4];
        Matcher matcher = pattern.matcher(line);
        int counter = 0;
        while (matcher.find()) {
            String numberStr = matcher.group();
            int number = Integer.parseInt(numberStr);
            values[counter] = number;
            counter++;
        }
        return new Gear(values[0], values[1], values[2], values[3]);
    }

    private List<Rule> mapRules(String line) {
        var rules = new ArrayList<Rule>();
        var rulesSplit = line.replace("}", "").split(",");
        for (int i = 0; i < rulesSplit.length; i++) {
            if (i == rulesSplit.length - 1) {
                rules.add(new Rule(null, null, null, rulesSplit[i]));
            } else {
                var r = rulesSplit[i].split(":");
                rules.add(new Rule(r[0].charAt(0), r[0].charAt(1), Integer.parseInt(r[0].substring(2)), r[1]));
            }
        }
        return rules;
    }

    record Rule(Character category, Character condition, Integer value, String outcome) {
        boolean check(Gear gear) {
            if (category == null) {
                return true;
            }
            int val = gear.get(category);
            return switch (condition) {
                case '<' -> val < this.value;
                case '>' -> val > this.value;
                default -> throw new ChristmasException("Unexpected value: " + condition);
            };
        }
    }

    record Gear(int x, int m, int a, int s) {
        public long value() {
            return x + m + a + s;
        }

        public int get(char category) {
            return switch (category) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new ChristmasException("Unexpected value: " + category);
            };
        }
    }

}
