package aoc.day08;

import aoc.FileUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkNavigator {

    private static char[] leftRight;
    private static Map<String, String[]> allNodes;

    public static void main(String[] args) {
        var maps = FileUtil.readFile("day8");
        analyzeMaps(maps);
        System.out.printf("Traveled steps: %,d%n", navigateNetwork());
        System.out.printf("Ghost steps: %,d%n", navigateGhostNetwork());
    }

    private static int navigateNetwork() {
        return countStepsForNode("AAA", "ZZZ");
    }

    private static BigInteger navigateGhostNetwork() {
        return allNodes.keySet().stream()
                .filter(k -> k.endsWith("A"))
                .map(n -> BigInteger.valueOf(countStepsForNode(n, "Z")))
                .reduce(BigInteger.ONE, (lcm, steps) -> lcm
                        .multiply(steps)  // calculate the product (lcm * number)
                        .divide(lcm.gcd(steps))); //divide by greatest common divider of lcm & number
    }

    private static int countStepsForNode(String node, String endsWith) {
        int steps = 0;
        int i = 0;
        String nextNode = node;
        while (!nextNode.endsWith(endsWith)) {
            var instruction = leftRight[i];
            nextNode = allNodes.get(nextNode)[(instruction == 'L') ? 0 : 1];
            steps++;
            i = (i == leftRight.length - 1) ? 0 : i + 1;
        }
        System.out.println(STR. "\{ steps } = (\{ node }, \{ nextNode })" );
        return steps;
    }

    private static void analyzeMaps(List<String> maps) {
        allNodes = new HashMap<>();
        leftRight = maps.remove(0).toCharArray();
        maps.remove(0);
        for (var node : maps) {
            var location = node.substring(0, 3);
            var left = node.substring(7, 10);
            var right = node.substring(12, 15);
            allNodes.put(location, new String[]{left, right});
        }
    }
}
