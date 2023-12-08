package aoc.day8;

import aoc.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkNavigator {

    public static void main(String[] args) {
        var maps = FileUtil.readFile("day8");
//        System.out.println("Traveled steps: " + navigateNetwork(maps));
        System.out.println("Ghost steps: " + navigateGhostNetwork(maps));
    }

    private static int navigateGhostNetwork(List<String> maps) {
        var leftRight = maps.get(0).toCharArray();
        var nodes = mapNodes(maps);
        int ghostSteps = 0;
        int i = 0;
        var nextNodes = nodes.keySet().stream()
                .filter(k -> k.endsWith("A"))
                .toList();
        boolean done = false;
        while (!done) {
            var instruction = leftRight[i];
            i = (i == leftRight.length - 1) ? 0 : i + 1;
            List<String> foundNodes = new ArrayList<>();
            for (var nextNode : nextNodes) {
                foundNodes.add(nodes.get(nextNode)[(instruction == 'L') ? 0 : 1]);
            }
            ghostSteps++;
            String log = ghostSteps + ": [" + String.join(", ", nextNodes) + "] -> " + String.join(", ", foundNodes) + "]";
            nextNodes = foundNodes;
            System.out.println(log);
            done = nextNodes.stream().allMatch(s -> s.endsWith("Z"));
        }
        return ghostSteps;
    }

    private static int navigateNetwork(List<String> maps) {
        var leftRight = maps.get(0).toCharArray();
        var nodes = mapNodes(maps);
        int steps = 0;
        int i = 0;
        String nextNode = "AAA";
        while (!nextNode.equals("ZZZ")) {
            var instruction = leftRight[i];
            i = (i == leftRight.length - 1) ? 0 : i + 1;
            steps++;
            nextNode = nodes.get(nextNode)[(instruction == 'L') ? 0 : 1];
        }
        return steps;
    }

    private static Map<String, String[]> mapNodes(List<String> maps) {
        Map<String, String[]> nodes = new HashMap<>();
        for (int i = 2; i < maps.size(); i++) {
            String s = maps.get(i);
            String loc = s.substring(0, s.indexOf("=")).trim();
            String left = s.substring(s.indexOf("(") + 1, s.indexOf(",")).trim();
            String right = s.substring(s.indexOf(",") + 1, s.indexOf(")")).trim();
            nodes.put(loc, new String[]{left, right});
        }
        return nodes;
    }

    record Node(String loc, String left, String right) {}
}
