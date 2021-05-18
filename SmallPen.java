import javafx.scene.paint.Color;

/**
 * Description: This class is responsible for drawing blockades with a 1x1 size
 * Date Last Modified: 03/17/2020
 *
 * @author Will Holland
 * CS1131, Fall 2019
 * Section 1
 */

public class SmallPen implements PenSizes {

    @Override
    public void draw(Display.Node currentNode, Display.Node[][] grid, boolean isBlockadeEnabled) {
        if (isBlockadeEnabled) {
            currentNode.setFill(Color.WHITE);
            currentNode.isBlockade = false;
        } else {
            currentNode.setFill(Color.DARKGREY);
            currentNode.isBlockade = true;
        }
    }
}
