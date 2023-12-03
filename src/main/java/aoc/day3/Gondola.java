package aoc.day3;

import aoc.FileUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Gondola {

    public static void main(String[] args) throws URISyntaxException, IOException {
        var engineSchematic = FileUtil.readFile("day3");

        int missingPart = searchSchematicForPartNumbers(engineSchematic);
        System.out.println(missingPart);

        int missingGear = searchSchematicForGearNumbers(engineSchematic);
        System.out.println(missingGear);
    }

    private static int searchSchematicForGearNumbers(List<String> engineSchematic) {
        int sumOfGearNumbers = 0;

        SchematicLine mainLine = null;
        SchematicLine lineAbove = null;
        SchematicLine lineBelow;

        for (int i = 0; i <= engineSchematic.size(); i++) {
            lineBelow = i < engineSchematic.size() ? parseSchematicLine(engineSchematic.get(i)) : null;

            if (mainLine != null) {
                for (var symbol : mainLine.symbols) {
                    if (symbol.value.equals("*")) {
                        sumOfGearNumbers += symbol.getGearNumber(mainLine, lineAbove, lineBelow);
                    }
                }
            }
            lineAbove = mainLine;
            mainLine = lineBelow;
        }
        return sumOfGearNumbers;
    }

    private static int searchSchematicForPartNumbers(List<String> engineSchematic) {
        int sumOfPartNumbers = 0;

        SchematicLine mainLine = null;
        SchematicLine lineAbove = null;
        SchematicLine lineBelow;

        for (int i = 0; i <= engineSchematic.size(); i++) {
            lineBelow = i < engineSchematic.size() ? parseSchematicLine(engineSchematic.get(i)) : null;

            if (mainLine != null) {
                for (var number : mainLine.numbers) {
                    if (number.isAPartNumber(mainLine, lineBelow, lineAbove)) {
                        sumOfPartNumbers += number.value;
                    }
                }
            }
            lineAbove = mainLine;
            mainLine = lineBelow;
        }
        return sumOfPartNumbers;
    }

    private static SchematicLine parseSchematicLine(String line) {
        SchematicLine schematicLine = new SchematicLine(new ArrayList<>(), new ArrayList<>());
        StringBuilder sb = new StringBuilder();

        boolean digitFound = false;
        int startIndex = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (Character.isDigit(c)) {
                sb.append(c);
                if (!digitFound) {
                    digitFound = true;
                    startIndex = i;
                }
            } else {
                if (digitFound) {
                    schematicLine.addNumber(sb.toString(), startIndex, i - 1); // handles end of number
                    digitFound = false;
                    sb.setLength(0); // clears StringBuilder
                }
                if (c != '.') {
                    schematicLine.addSymbol(String.valueOf(c), i);
                }
            }

            if (digitFound && i == line.length() - 1) { //handles end of line
                schematicLine.addNumber(sb.toString(), startIndex, i);
            }
        }

        return schematicLine;
    }


    record SchematicLine(List<Number> numbers, List<Symbol> symbols) {
        void addNumber(String valueString, int startIndex, int lastIndex){
            numbers.add(new Number(Integer.parseInt(valueString), startIndex, lastIndex));
        }

        public void addSymbol(String value, int index) {
            symbols.add(new Symbol(String.valueOf(value), index));
        }
    }

    record Number(int value, int startIndex, int lastIndex) {
        boolean isAPartNumber(SchematicLine... lines) {
            return Arrays.stream(lines)
                    .filter(Objects::nonNull) //filters out any null values (for example when there is no aboveLine for first row)
                    .flatMap(l -> l.symbols.stream()) // bundles all SYMBOL lists into one
                    .anyMatch(this::isAdjacentTo);
        }

        boolean isAdjacentTo(Symbol symbol) {
            return symbol.index >= startIndex - 1 && symbol.index <= lastIndex + 1;
        }
    }

    record Symbol(String value, int index) {
        public int getGearNumber(SchematicLine ... lines) {
            List<Integer> numbersAdjacentToSymbol = Arrays.stream(lines)
                    .filter(Objects::nonNull) //filters out any null values
                    .flatMap(l -> l.numbers.stream()) // bundles all NUMBER lists into one
                    .filter(n -> n.isAdjacentTo(this))
                    .map(n -> n.value)
                    .toList();
            if (numbersAdjacentToSymbol.size() != 2) {
                return 0;
            }
            return numbersAdjacentToSymbol.get(0) * numbersAdjacentToSymbol.get(1);
        }
    }

}
