package aoc.day20;

import aoc.ChristmasAssert;
import aoc.FileUtil;

import java.util.*;

public class ButtonModule {

    public static final int LOGLEVEL = 1;
    public static final char FLIPFLOP = '%';
    public static final String BROADCASTER = "broadcaster";


    void main() {
        ButtonModule buttonModule = new ButtonModule();
        int pressCount = 1000;
        long result = 0L;
        for (int i = 0; i < pressCount; i++) {
            result = buttonModule.press(false);
            System.out.println(STR."Result: \{result}\n---");
        }
        ChristmasAssert.test(result != 694094280, result);
        ChristmasAssert.test(result > 694094280, result);
    }

    final Map<String, Module> allModules;
    final Map<String, Module> untypedModules;
    public int noActionCounter = 0;
    long lowPulses = 0L;
    long highPulses = 0L;


    public ButtonModule() {
        untypedModules = new HashMap<>();
        allModules = mapAllModules(FileUtil.readFile("day20"));
        for (var m : allModules.values()) {
            m.initializeConnectedModules();
        }
        allModules.putAll(untypedModules);
        for (var m : allModules.values()) {
            if (m instanceof Conjunction) {
                ((Conjunction) m).mapWatchedModules();
            }
        }
    }

    private long press(boolean isHighPulse) {
        long prevLowPulses = lowPulses;
        long prevHighPulses = highPulses;

        Deque<Module> queue = new ArrayDeque<>();
        Module broadcaster = allModules.get(BROADCASTER);
        broadcaster.receivedPulses.add(isHighPulse);
        queue.offer(broadcaster);
        if (isHighPulse) highPulses++;
        else lowPulses++;
        print("button", BROADCASTER, isHighPulse);

        while (!queue.isEmpty() && noActionCounter < 5000) {
            Module current = queue.poll();
            current.handlePulse();
            List<Module> modules = current.modules;
            queue.addAll(modules);
        }
        long lowDiff = lowPulses - prevLowPulses;
        long highDiff = highPulses - prevHighPulses;
        System.out.println(STR."---\nLow pulses: \{ lowPulses } (\{ lowDiff })\nHigh pulses: \{ highPulses } (\{ highDiff })");
        return lowPulses * highPulses;
    }

    private Map<String, Module> mapAllModules(List<String> input) {
        Map<String, Module> modules = new HashMap<>();
        for (var line : input) {
            var split = line.split(" -> ");
            var connectedModules = split[1].split(", ");
            String moduleName;
            Module module;
            if (split[0].equals(BROADCASTER)) {
                moduleName = BROADCASTER;
                module = new BroadCaster(moduleName, connectedModules);
            } else {
                moduleName = split[0].substring(1);
                if (split[0].charAt(0) == FLIPFLOP) {
                    module = new FlipFlop(moduleName, connectedModules);
                } else {
                    module = new Conjunction(moduleName, connectedModules);
                }
            }

            modules.put(moduleName, module);
        }
        return modules;
    }

    void print(String from, String to, boolean isHighPulse) {
        if (LOGLEVEL > 0) System.out.printf("%s -%s-> %s%n", from, isHighPulse ? "high" : "low", to);
        noActionCounter = 0;
    }

    void debugLog(String name) {
        if (LOGLEVEL > 1) System.out.println(name + " did nothing");
        noActionCounter++;
    }

    public class FlipFlop extends Module {
        boolean isOn;

        public FlipFlop(String name, String[] connectedModules) {
            super(name, connectedModules);
            isOn = false;
        }

        @Override
        void handlePulse() {
            Boolean receivedPulse = receivedPulses.poll();
            if (receivedPulse == null) {
                debugLog(name);
                return;
            }
            lastPulse = receivedPulse;
            if (!lastPulse) {
                isOn = !isOn;
                for (var module : modules) {
                    module.receivedPulses.add(isOn); // on = send high pulse | off = send low pulse
                    print(this.toString(), module.toString(), isOn);
                    if (isOn) highPulses++;
                    else lowPulses++;
                }
            } else {
                debugLog(name);
            }
        }

        @Override
        public String toString() {
            return "%" + name;
        }
    }

    public class Conjunction extends Module {
        List<FlipFlop> flipFlops;
        List<Module> otherModules;
        public Conjunction(String name, String[] connectedModules) {
            super(name, connectedModules);
            flipFlops = new ArrayList<>();
            otherModules = new ArrayList<>();
        }

        @Override
        void handlePulse() {
            Boolean lastReceivedPulse = receivedPulses.peek();
            if (lastReceivedPulse == null) {
                debugLog(name);
                return;
            }
            receivedPulses.clear();
            lastPulse = lastReceivedPulse;
            boolean areAllWatchedModulesHighPulses = flipFlops.stream().allMatch(ff -> ff.isOn) && otherModules.stream().allMatch(m -> m.lastPulse);
            for (var module : modules) {
                module.receivedPulses.add(!areAllWatchedModulesHighPulses);
                print(this.toString(), module.toString(), !areAllWatchedModulesHighPulses);
                if (areAllWatchedModulesHighPulses) lowPulses++;
                else highPulses++;
            }
        }

        void mapWatchedModules() {
            flipFlops = allModules.values().stream().filter(s -> s.modules.contains(this) && s instanceof FlipFlop).map(s -> (FlipFlop) s).toList();
            otherModules = allModules.values().stream().filter(s -> s.modules.contains(this) && !(s instanceof FlipFlop)).toList();
        }

        @Override
        public String toString() {
            return "&" + name;
        }
    }

    class BroadCaster extends Module {
        public BroadCaster(String name, String[] connectedModules) {
            super(name, connectedModules);
        }

        @Override
        void handlePulse() {
            Boolean receivedPulse = receivedPulses.poll();
            lastPulse = receivedPulse != null && receivedPulse;
            for (var module : modules) {
                module.receivedPulses.add(receivedPulse);
                print(this.toString(), module.toString(), receivedPulse);
                if (receivedPulse) highPulses++;
                else lowPulses++;
            }
        }

        @Override
        public String toString() {
            return BROADCASTER;
        }
    }

    abstract class Module {
        String name;
        String[] connectedModules;
        List<Module> modules;
        Queue<Boolean> receivedPulses;
        boolean lastPulse;


        public Module(String name, String[] connectedModules) {
            this.name = name;
            this.connectedModules = connectedModules;
            modules = new ArrayList<>();
            receivedPulses = new ArrayDeque<>();
            lastPulse = false;
        }

        public void initializeConnectedModules() {
            for (int i = 0; i < connectedModules.length; i++) {
                Module module = allModules.get(connectedModules[i]);
                if (module == null) { // create an untyped module
                    module = new Conjunction(connectedModules[i], new String[0]);
                    untypedModules.put(module.name, module);
                }
                modules.add(module);
            }
        }

        abstract void handlePulse();
    }
}
