import javafx.scene.paint.Color;

/**
 * Description: This class is responsible for drawing blockades with a 5x5 size
 * Date Last Modified: 03/17/2020
 *
 * @author Will Holland
 * CS1131, Fall 2019
 * Section 1
 */

public class LargePen implements PenSizes {

    @Override
    public void draw(Display.Node currentNode, Display.Node[][] grid, boolean isBlockadeEnabled) {
        if (isBlockadeEnabled) {
            currentNode.setFill(Color.WHITE);
            currentNode.isBlockade = false;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    int row = i + currentNode.row;
                    int col = j + currentNode.col;
                    if(!(row < 0 || row >= grid.length || col < 0 || col >= grid[0].length)){
                        grid[row][col].setFill(Color.WHITE);
                        grid[row][col].isBlockade = false;
                    }
                }
            }
        } else {
            currentNode.setFill(Color.DARKGREY);
            currentNode.isBlockade = true;
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    int row = i + currentNode.row;
                    int col = j + currentNode.col;
                    if(!(row < 0 || row >= grid.length || col < 0 || col >= grid[0].length)){
                        grid[row][col].setFill(Color.DARKGREY);
                        grid[row][col].isBlockade = true;
                    }
                }
            }
        }
    }
}
