package aoc.day10;

import aoc.ChristmasException;
import aoc.FileUtil;

import java.util.*;

public class PipeJumper {

    private static final boolean log = false;
    private static final char STARTFINISH = 'S';
    public static final char VISITED = '█';
    private static final Map<String, int[]> DIRECTIONS = Map.of(
            "DOWN", new int[]{1, 0},
            "RIGHT", new int[]{0, 1},
            "LEFT", new int[]{0, -1},
            "UP", new int[]{-1, 0}
    );
    private static int[] OUTER_EDGES;

    public static void main(String[] args) {
        var matrix = createTheMatrix();
        var matrixJumps = enterTheMatrix(matrix);

        var narrowedDownMatrix = shrinkAndMapTheMatrix(matrix);
        var possibleNestLocations = scanMatrix(narrowedDownMatrix);

        printTheMatrix(narrowedDownMatrix);
        System.out.println(STR. "\nJumped the matrix \{ matrixJumps } times. Result: \{ matrixJumps / 2 }." );
        System.out.println(STR. "Matrix scan identified \{ possibleNestLocations } possible locations for nesting." );
    }

    private static int scanMatrix(Node[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                scanForNests(matrix, new int[]{i, j});
            }
        }
        return (int) Arrays.stream(matrix).flatMap(Arrays::stream).filter(n -> n.type == '+').count();
    }

    private static void scanForNests(Node[][] matrix, int[] start) {
        Stack<int[]> pathStack = new Stack<>();
        pathStack.push(start);
        Set<Node> seen = new HashSet<>();

        while (!pathStack.isEmpty()) {
            int[] current = pathStack.peek();
            int row = current[0];
            int col = current[1];

            if (row == 0 || row == matrix.length-1 || col == 0 || col == matrix[0].length-1) {
                for(var n : seen) {
                    n.type = '.';
                    n.isEnclosed = false;
                }
                return; // path to exit was found, so quit method
            }

            Node node = matrix[row][col];
            if (node.type != VISITED && node.type != '+') {
                seen.add(node);
                int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};  // Up, Right, Down, Left

                boolean foundNextStep = false;

                for (int[] direction : directions) {
                    int newRow = row + direction[0];
                    int newCol = col + direction[1];
                    Node newNode = matrix[newRow][newCol];

                    if (newNode.type != VISITED && newNode.type != '+' && !seen.contains(newNode)) {
                        pathStack.push(new int[]{newRow, newCol});
                        seen.add(newNode);
                        foundNextStep = true;
                        break;
                    }
                }

                if (!foundNextStep) {
                    pathStack.pop();  // Backtrack
                }
            } else {
                pathStack.pop();  // Backtrack
            }
        }
        for(var n : seen) {
            n.type = '+';
            n.isEnclosed = true;
        }
    }

    private static int enterTheMatrix(char[][] matrix) {
        int[] current = findStart(matrix);
        if (log) System.out.println(new Move("START", "N.A.", 'S', current));
        Move currentMove = findFirstJump(matrix, current[0], current[1]);
        int jumps = 1;
        if (log) System.out.println(currentMove);

        int lowestX = current[0];
        int highestX = current[0];
        int lowestY = current[1];
        int highestY = current[1];

        while ('S' != currentMove.type) {
            currentMove = makeNextMove(currentMove, matrix);
            jumps++;
            if (log) System.out.println(currentMove);

            if (currentMove.coords[0] < lowestX) lowestX = currentMove.coords[0];
            if (currentMove.coords[0] > highestX) highestX = currentMove.coords[0];
            if (currentMove.coords[1] < lowestY) lowestY = currentMove.coords[1];
            if (currentMove.coords[1] > highestY) highestY = currentMove.coords[1];
        }

        matrix[currentMove.coords[0]][currentMove.coords[1]] = VISITED;
        OUTER_EDGES = new int[]{lowestX, highestX, lowestY, highestY};
        return jumps;
    }

    private static Move makeNextMove(Move currentMove, char[][] matrix) {
        int[] direction = DIRECTIONS.get(currentMove.newDirection);
        int x = currentMove.coords[0];
        int y = currentMove.coords[1];
        int newX = x + direction[0];
        int newY = y + direction[1];
        char newType = matrix[newX][newY];
        String newDirection = navigateNextDirection(newType, currentMove.newDirection);
        matrix[x][y] = VISITED;
        return new Move(newDirection, currentMove.newDirection, newType, new int[]{newX, newY});
    }

    private static String navigateNextDirection(char type, String previousDirection) {
        if (type == '|') return previousDirection.equals("UP") ? "UP" : "DOWN";
        if (type == 'L') return previousDirection.equals("DOWN") ? "RIGHT" : "UP";
        if (type == 'J') return previousDirection.equals("DOWN") ? "LEFT" : "UP";
        if (type == '-') return previousDirection.equals("LEFT") ? "LEFT" : "RIGHT";
        if (type == 'F') return previousDirection.equals("LEFT") ? "DOWN" : "RIGHT";
        if (type == '7') return previousDirection.equals("RIGHT") ? "DOWN" : "LEFT";
        if (type == 'S') return "Finished";
        throw new ChristmasException("No possible path found for " + type + " and " + previousDirection);
    }

    private static Move findFirstJump(char[][] matrix, int x, int y) {
        int newX = x + DIRECTIONS.get("DOWN")[0];
        int newY = y + DIRECTIONS.get("DOWN")[1];
        return new Move("DOWN", "START", matrix[newX][newY], new int[]{newX, newY});
    }

    private static int[] findStart(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == STARTFINISH) {
                    return new int[]{i, j};
                }
            }
        }
        throw new ChristmasException("The Matrix has no start position.");
    }

    private static char[][] createTheMatrix() {
        var pipeNetwork = FileUtil.readFile("day10");
        char[][] matrix = new char[pipeNetwork.size()][];
        for (int i = 0; i < pipeNetwork.size(); i++) {
            matrix[i] = pipeNetwork.get(i).toCharArray();
        }
        if (log) printTheMatrix(matrix);
        return matrix;
    }

    private static Node[][] shrinkAndMapTheMatrix(char[][] completeMatrix) {
        int minX = OUTER_EDGES[0];
        int maxX = OUTER_EDGES[1];
        int minY = OUTER_EDGES[2];
        int maxY = OUTER_EDGES[3];
        int xRange = maxX - minX + 1;
        int yRange = maxY - minY + 1;
        Node[][] narrowedDownMatrix = new Node[xRange][yRange];

        for (int i = 0; i < xRange; i++) {
            char[] row = completeMatrix[minX + i];
            for (int j = 0; j < yRange; j++) {
                char node = row[minY + j];
                narrowedDownMatrix[i][j] = new Node(node);
            }
        }
        if (log) printTheMatrix(narrowedDownMatrix);
        return narrowedDownMatrix;
    }

    private static void printTheMatrix(char[][] matrix) {
        for (char[] row : matrix) {
            for (char value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private static void printTheMatrix(Node[][] narrowedDownMatrix) {
        int i = 0;
        for (Node[] row : narrowedDownMatrix) {
            System.out.printf("%3d:", i);
            i++;
            for (Node node : row) {
                System.out.print(node.type);
            }
            System.out.println();
        }
    }

    record Move(String newDirection, String previousDirection, char type, int[] coords) {

        @Override
        public String toString() {
            return STR. "[\{ coords[0] }, \{ coords[1] }]  > from \{ previousDirection } to \{ newDirection }< bring us to '\{ type }' " ;
        }

    }

    public static class Node {
        public char type;
        public Boolean isEnclosed;

        public Node(char type) {
            this.type = type;
            this.isEnclosed = null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "type=" + type +
                    ", isEnclosed=" + isEnclosed +
                    '}';
        }
    }
}





















