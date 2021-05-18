import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Description:
 * Date Last Modified: 03/15/2020
 *
 * @author Will Holland
 * CS1131, Fall 2019
 * Section 1
 */

public class AStar implements PathFind {

    @Override
    public void findPath(Display.Node[][] grid, Display.Node start, Display.Node end) {
        ArrayList<Display.Node> closeList = new ArrayList<>();
        ArrayList<Display.Node> openList = new ArrayList<>();
        ArrayList<Display.Node> path = new ArrayList<>();

        Display.Node current = start;
        openList.add(current);
        Arrays.stream(grid).forEach(e -> Arrays.stream(e).forEach(e1 -> {
            e1.g = Double.MAX_VALUE - 100;
            e1.f = Double.MAX_VALUE - 100;
            e1.findNeighbors(grid);
        }));

        start.g = 0;
        start.f = start.h;
        Display.Node lastNode = null;
        while (openList.size() != 0) {

            current = openList.stream().min(Comparator.comparingDouble(o -> o.f)).get();

            openList.remove(current);
            closeList.add(current);

            if (Display.areDiagonalsEnabled) {
                if (current.h <= Math.sqrt(2)) {
                    draw(openList, closeList, path, current);
                    lastNode = current;
                    break;
                }
            } else {
                if (current.h <= 1) {
                    draw(openList, closeList, path, current);
                    lastNode = current;
                    break;
                }
            }

            for (Display.Node neighbor : current.neighbors) {
                if (!closeList.contains(neighbor)) {
                    double tentativeG = current.g + 1;
                    if (tentativeG < neighbor.g) {
                        neighbor.cameFrom = current;
                        neighbor.g = tentativeG;
                        neighbor.f = neighbor.g + neighbor.h;
                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }
                }
            }
            if (Display.areDiagonalsEnabled) {
                for (Display.Node diagonalNeighbor : current.diagonalNeighbors) {
                    if (!closeList.contains(diagonalNeighbor)) {
                        double tentativeG = current.g + Math.sqrt(2);
                        if (tentativeG < diagonalNeighbor.g) {
                            diagonalNeighbor.cameFrom = current;
                            diagonalNeighbor.g = tentativeG;
                            diagonalNeighbor.f = diagonalNeighbor.g + diagonalNeighbor.h;
                            if (!openList.contains(diagonalNeighbor)) {
                                openList.add(diagonalNeighbor);
                            }
                        }
                    }
                }
            }
        }
        if (lastNode == null) {
            System.out.println("NO SOLUTION");
            drawVisited(openList, closeList);
        }
        disableEditable(grid);
        Display.hasAlgorithmRun = true;
    }

    public void draw(ArrayList<Display.Node> openList, ArrayList<Display.Node> closeList, ArrayList<Display.Node> path, Display.Node current) {
        path.forEach(node -> {
            if (!node.isStartNode) node.setFill(Color.WHITE);
        });
        path = new ArrayList<>();
        drawVisited(openList, closeList);
        makePath(current, path);
        System.out.println(path.size());
    }

    private void disableEditable(Display.Node[][] grid) {
        Arrays.stream(grid).forEach(e -> Arrays.stream(e).forEach(e1 -> e1.setDisable(true)));
    }

    private void makePath(Display.Node current, ArrayList<Display.Node> path) {
        path.add(current);
        current.isPath = true;
        if (!current.isEndNode && !current.isStartNode) current.setFill(Color.YELLOW);
        if (current.cameFrom != null) makePath(current.cameFrom, path);
    }

    private void drawVisited(ArrayList<Display.Node> openList, ArrayList<Display.Node> closeList) {
        openList.forEach(node -> {
            if (!node.isStartNode && !node.isEndNode && !node.isBlockade) node.setFill(Color.TOMATO);
        });
        closeList.forEach(node -> {
            if (!node.isStartNode && !node.isEndNode && !node.isBlockade) node.setFill(Color.GREENYELLOW);
        });
    }
}