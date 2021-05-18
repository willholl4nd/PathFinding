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

public class GreedyBestFirst implements PathFind {

    @Override
    public void findPath(Display.Node[][] grid, Display.Node start, Display.Node end) {
        ArrayList<Display.Node> openList = new ArrayList<>();
        ArrayList<Display.Node> closeList = new ArrayList<>();
        ArrayList<Display.Node> path = new ArrayList<>();

        Arrays.stream(grid).forEach(e -> Arrays.stream(e).forEach(e1 -> {
            e1.findNeighbors(grid);
        }));

            Display.Node current = start;
        openList.add(current);
        Display.Node lastNode = null;

        while (openList.size() != 0) {
            current = openList.stream().min(Comparator.comparingDouble(o -> o.h)).get();
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

            for (Display.Node node : current.neighbors){
                if(node.h < current.h && !closeList.contains(node)) {
                    node.cameFrom = current;
                    if(!openList.contains(node)) openList.add(node);
                }
            }

            if (Display.areDiagonalsEnabled) {
                for (Display.Node node : current.diagonalNeighbors){
                    if(node.h < current.h && !closeList.contains(node)) {
                        node.cameFrom = current;
                        if (!openList.contains(node)) openList.add(node);
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
