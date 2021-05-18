import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Description: This class handles all of the displaying of the grid
 * and holds the node class that is used by the path finding algorithms
 * Date Last Modified: 03/15/2020
 *
 * @author Will Holland
 * CS1131, Fall 2019
 * Section 1
 */

public class Display extends Application {
    int height;
    int width;
    private int rows, columns, divisor;
    private String searchAlgorithm = "A*";
    Tooltip startToolTip;
    PathFind algorithm;
    Node startNode, endNode;
    Node[][] grid;
    private boolean isBlockadeEnabled;
    PenSizes penSize;
    Pane rectangles;
    public static boolean hasAlgorithmRun, areDiagonalsEnabled;

    @Override
    public void start(Stage primaryStage) {
        width = 1000;
        height = 760;
        divisor = 10;
        rows = (height - 60) / divisor;
        columns = width / divisor;
        startToolTip = new Tooltip("Currently using " + searchAlgorithm + " path finding algorithm");
        algorithm = new AStar();
        penSize = new SmallPen();
        areDiagonalsEnabled = true;

        //TODO Clean up all of this initialization code
        rectangles = new Pane();
        BorderPane everything = new BorderPane();
        HBox buttons = new HBox();
        Button start = new Button("Start Visualization");
        buttons.getChildren().addAll(start);
        buttons.setAlignment(Pos.CENTER);
        start.setTooltip(startToolTip);
        everything.setCenter(rectangles);
        everything.setBottom(buttons);
        grid = generateGrid(rectangles);
        everything.setTop(createMenuBar());
        start.setOnAction(event -> {
            if (!hasAlgorithmRun) {
                if (startNode == null) {
                    startNode = grid[0][0];
                    startNode.isStartNode = true;
                    startNode.setFill(Color.GREEN);
                }
                if (endNode == null) {
                    endNode = grid[rows - 1][columns - 1];
                    endNode.isEndNode = true;
                    endNode.setFill(Color.DARKRED);
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        grid[i][j].calculateH();
                    }
                }
                //TODO Implement the algorithms now
                algorithm.findPath(grid, startNode, endNode);
            }
        });

        Scene sc = new Scene(everything, width, height);
        primaryStage = new Stage();
        primaryStage.setScene(sc);
        primaryStage.setTitle("Visualize Path Finding Algorithms");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu pathFindingAlgs = new Menu("Algorithms");
        Menu tools = new Menu("Tools");
        Menu penSizeChooser = new Menu("Pen Size");
        Menu board = new Menu("Board");
        Menu algorithmOptions = new Menu("Algorithm options");

        MenuItem diagonalsEnabled = new MenuItem("Diagonals Enabled");
        diagonalsEnabled.setOnAction(event -> areDiagonalsEnabled = true);
        MenuItem diagonalsDisabled = new MenuItem("Diagonals Disabled");
        diagonalsDisabled.setOnAction(event -> areDiagonalsEnabled = false);

        MenuItem randomFill = new MenuItem("Randomize Blockades");
        randomFill.setOnAction(event -> randomizeBoard());
        MenuItem resetPath = new MenuItem("Reset Path");
        resetPath.setOnAction(event -> resetPath());
        MenuItem reset = new MenuItem("Reset Board");
        reset.setOnAction(event -> resetBoard());
        MenuItem benchmark = new MenuItem("Benchmark/dev");
        benchmark.setOnAction(event -> performBenchmark());

        //1x1 node size
        MenuItem small = new MenuItem("Small");
        small.setOnAction(event -> penSize = new SmallPen());
        //3x3 node size
        MenuItem medium = new MenuItem("Medium");
        medium.setOnAction(event -> penSize = new MediumPen());
        //5x5 node size
        MenuItem large = new MenuItem("Large");
        large.setOnAction(event -> penSize = new LargePen());

        MenuItem blockade = new MenuItem("Blockade");
        blockade.setOnAction(event -> isBlockadeEnabled = false);
        MenuItem eraser = new MenuItem("Eraser");
        eraser.setOnAction(event -> isBlockadeEnabled = true);

        MenuItem aStar = new MenuItem("A*");
        aStar.setOnAction(event -> {
            searchAlgorithm = aStar.getText();
            updateToolTip();
            algorithm = new AStar();
        });
        MenuItem dijkstras = new MenuItem("Dijkstra’s");
        dijkstras.setOnAction(event -> {
            searchAlgorithm = dijkstras.getText();
            updateToolTip();
            algorithm = new Dijkstras();
        });
        MenuItem greedyBestFirst = new MenuItem("Greedy Best-First-Search");
        greedyBestFirst.setOnAction(event -> {
            searchAlgorithm = greedyBestFirst.getText();
            updateToolTip();
            algorithm = new GreedyBestFirst();
        });
        MenuItem swarm = new MenuItem("Swarm Search");
        swarm.setOnAction(event -> {
            searchAlgorithm = swarm.getText();
            updateToolTip();
            algorithm = new Swarm();
        });
        MenuItem convergentSwarm = new MenuItem("Convergent Swarm Search");
        convergentSwarm.setOnAction(event -> {
            searchAlgorithm = convergentSwarm.getText();
            updateToolTip();
            algorithm = new ConvergentSwarm();
        });
        MenuItem bidirectionalSwarm = new MenuItem("Bidirectional Swarm Search");
        bidirectionalSwarm.setOnAction(event -> {
            searchAlgorithm = bidirectionalSwarm.getText();
            updateToolTip();
            algorithm = new BidirectionalSwarm();
        });
        MenuItem breadth = new MenuItem("Breadth-First Search");
        breadth.setOnAction(event -> {
            searchAlgorithm = breadth.getText();
            updateToolTip();
            algorithm = new BreadthFirst();
        });
        MenuItem depth = new MenuItem("Depth-First Search");
        depth.setOnAction(event -> {
            searchAlgorithm = depth.getText();
            updateToolTip();
            algorithm = new DepthFirst();
        });

        algorithmOptions.getItems().addAll(diagonalsEnabled, diagonalsDisabled);
        board.getItems().addAll(randomFill, resetPath, reset, benchmark);
        penSizeChooser.getItems().addAll(small, medium, large);
        tools.getItems().addAll(blockade, eraser);
        pathFindingAlgs.getItems().addAll(aStar, dijkstras, greedyBestFirst, swarm,
                convergentSwarm, bidirectionalSwarm, breadth, depth);
        menuBar.getMenus().addAll(pathFindingAlgs, tools, penSizeChooser, board, algorithmOptions);
        return menuBar;
    }

    private void performBenchmark() {
        for (int i = 0; i < 10; i++) {
            randomizeBoard();
            long time1 = System.currentTimeMillis();
            if (!hasAlgorithmRun) {
                if (startNode == null) {
                    startNode = grid[0][0];
                    startNode.isStartNode = true;
                    startNode.setFill(Color.GREEN);
                }
                if (endNode == null) {
                    endNode = grid[rows - 1][columns - 1];
                    endNode.isEndNode = true;
                    endNode.setFill(Color.DARKRED);
                }
                for (int k = 0; k < rows; k++) {
                    for (int j = 0; j < columns; j++) {
                        grid[k][j].calculateH();
                    }
                }
                algorithm.findPath(grid, startNode, endNode);
            }
            System.out.println("A* took " + (System.currentTimeMillis() - time1));
            resetPath();
            algorithm = new Dijkstras();
            time1 = System.currentTimeMillis();
            if (!hasAlgorithmRun) {
                if (startNode == null) {
                    startNode = grid[0][0];
                    startNode.isStartNode = true;
                    startNode.setFill(Color.GREEN);
                }
                if (endNode == null) {
                    endNode = grid[rows - 1][columns - 1];
                    endNode.isEndNode = true;
                    endNode.setFill(Color.DARKRED);
                }
                for (int k = 0; k < rows; k++) {
                    for (int j = 0; j < columns; j++) {
                        grid[k][j].calculateH();
                    }
                }
                algorithm.findPath(grid, startNode, endNode);
            }
            System.out.println("Dijkstras took " + (System.currentTimeMillis() - time1) + "milliseconds to complete");
            resetBoard();
            algorithm = new AStar();
            System.out.println();
        }
    }

    private void resetPath() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Node n = grid[i][j];
                if (n.getFill() == Color.GREENYELLOW ||
                        n.getFill() == Color.YELLOW || n.getFill() == Color.TOMATO) {
                    n.setFill(Color.WHITE);
                }
            }
        }
        hasAlgorithmRun = false;
    }

    private void resetBoard() {
        grid = generateGrid(rectangles);
        startNode = null;
        endNode = null;
        isBlockadeEnabled = false;
        penSize = new SmallPen();
        hasAlgorithmRun = false;
    }

    private void randomizeBoard() {
        if (!hasAlgorithmRun) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (Math.random() > .7) {
                        grid[i][j].setFill(Color.DARKGREY);
                        grid[i][j].isBlockade = true;
                    }
                }
            }
        }
    }

    private void updateToolTip() {
        startToolTip.setText("Currently using " + searchAlgorithm + " path finding algorithm");
    }

    private Node[][] generateGrid(Pane p) {
        Node[][] output = new Node[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                output[i][j] = new Node(j * divisor, i * divisor, divisor, divisor);
                p.getChildren().addAll(output[i][j]);
            }
        }
        return output;
    }

    /**
     * Used to hold all of the information for the nodes of the grid.
     */
    public class Node extends Rectangle {
        public int row, col;
        public double f, g, h;
        public boolean isBorder, isCorner, isStartNode, isEndNode, isBlockade, isPath;
        public ArrayList<Node> neighbors, diagonalNeighbors;
        public Node cameFrom;
        //TODO Determine what other variables need to be stored for all of the algorithms

        public Node(double x, double y, double width, double height) {
            super(x, y, width, height);
            neighbors = new ArrayList<>();
            diagonalNeighbors = new ArrayList<>();
            this.col = (int) (x / divisor);
            this.row = (int) (y / divisor);
            this.setFill(Color.WHITE);
            this.setStroke(Color.BLACK);
            this.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && !this.isEndNode && !this.isStartNode) {
                    penSize.draw(this, grid, isBlockadeEnabled);
                } else if (event.getButton().equals(MouseButton.MIDDLE)) {
                    if (this.isStartNode) {
                        this.isStartNode = false;
                        this.setFill(Color.WHITE);
                        startNode = null;
                    } else if (startNode == null && !this.isEndNode) {
                        this.setFill(Color.GREEN);
                        this.isStartNode = true;
                        startNode = this;
                    }
                } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                    if (this.isEndNode) {
                        this.isEndNode = false;
                        this.setFill(Color.WHITE);
                        endNode = null;
                    } else if (endNode == null && !this.isStartNode) {
                        this.setFill(Color.DARKRED);
                        this.isEndNode = true;
                        endNode = this;
                    }
                }
            });
            this.setOnMouseDragged(event -> handleMouseDragged((int) event.getSceneX(), (int) event.getSceneY(), event.getButton()));
            setupIsBorder();
            setupIsCorner();
        }

        private void handleMouseDragged(int x, int y, MouseButton button) {
            y = y - 30;
            int row = y / divisor;
            int col = x / divisor;
            if (row >= rows || row < 0 || col >= columns || col < 0) return;
            if (button.equals(MouseButton.PRIMARY) && !grid[row][col].isEndNode && !grid[row][col].isStartNode) {
                penSize.draw(grid[row][col], grid, isBlockadeEnabled);
            }
        }

        private void setupIsCorner() {
            this.isCorner = (this.row == 0 && this.col == 0) || (this.row == 0 && this.col == columns - 1) ||
                    (this.row == rows - 1 && this.col == 0) || (this.row == rows - 1 && this.col == columns - 1);
        }

        private void setupIsBorder() {
            this.isBorder = this.row == 0 || this.col == 0 || this.row == rows - 1 || this.col == columns - 1;
        }

        private void calculateH() {
            this.h = Math.sqrt(Math.pow(this.row - endNode.row, 2) + Math.pow(this.col - endNode.col, 2));
        }

        public void findNeighbors(Node[][] grid) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int currentRow = i + this.row;
                    int currentCol = j + this.col;
                    if (currentRow < 0 || currentRow >= rows || currentCol < 0 || currentCol >= columns) continue;
                    if (grid[currentRow][currentCol] != this) {
                        if (!grid[currentRow][currentCol].isBlockade) {
                            if (Math.abs(i) != Math.abs(j)) {
                                this.neighbors.add(grid[currentRow][currentCol]);
                            } else {
                                this.diagonalNeighbors.add(grid[currentRow][currentCol]);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "row=" + row +
                    ", col=" + col +
                    ", f=" + f +
                    ", g=" + g +
                    ", h=" + h +
                    ", isBorder=" + isBorder +
                    ", isCorner=" + isCorner +
                    ", isStartNode=" + isStartNode +
                    ", isEndNode=" + isEndNode +
                    ", isBlockade=" + isBlockade +
                    '}';
        }

    }
}
