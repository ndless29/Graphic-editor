package by.bsuir.giis.controllers;

import by.bsuir.giis.algoritms.circle.AlgorithmCircle;
import by.bsuir.giis.algoritms.circle.AlgorithmHyperbola;
import by.bsuir.giis.algoritms.circle.AlgorithmParabola;
import by.bsuir.giis.algoritms.clipping.CohenSutherlandCutoff;
import by.bsuir.giis.algoritms.clipping.CyrusBeck;
import by.bsuir.giis.algoritms.clipping.RobertsAlgorithm;
import by.bsuir.giis.algoritms.laba7.AlgorithmTriangulationOfDelaunay;
import by.bsuir.giis.algoritms.polygon.AlgorithmGraham;
import by.bsuir.giis.algoritms.polygon.AlgorithmJarvis;
import by.bsuir.giis.algoritms.interpolation.B_Splain;
import by.bsuir.giis.algoritms.interpolation.Interpolation;
import by.bsuir.giis.algoritms.loaders.LoaderFigureFromFile;
import by.bsuir.giis.algoritms.segment.AlgorithmBR;
import by.bsuir.giis.algoritms.segment.AlgorithmBY;
import by.bsuir.giis.algoritms.circle.AlgorithmEllipse;
import by.bsuir.giis.algoritms.segment.AlgorithmDDA;
import by.bsuir.giis.dialog.Dialogs;
import by.bsuir.giis.filling.AlgorithmFilling;
import by.bsuir.giis.filling.AlgorithmFillingOfRaster;
import by.bsuir.giis.functions.TransformFigure;
import by.bsuir.giis.model.Edge;
import by.bsuir.giis.model.Figure;
import by.bsuir.giis.model.Pixel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private Stage mainStage;
    private CohenSutherlandCutoff cohenSutherlandCutoff;
    private CyrusBeck cyrusBeck;
    private RobertsAlgorithm robertsAlgorithm;

    private int tileSize = 20;
    private int numberOfCircles = 4;
    private int X_TILES;
    private int Y_TILES;
    private boolean check = false;
    private Pixel firstPixel;
    private Pixel endPixel;
    private Circle firstCircle;
    private Circle firstTempCircle;
    private Circle endTempCircle;
    private Circle endCircle;
    private Color fillingColor = Color.GREEN;
    private List<Circle> circleList = new ArrayList<>();
    private final static int RADIUS = 10;
    private Line line;
    private List<Edge> lineList;
    private List<Pixel> pixelList = new ArrayList<>();
    @FXML
    private Label labelCoordinate;
    @FXML
    private Pane paneTile;
    @FXML
    private Label nameOfAlgorithm;
    @FXML
    private TextArea textAreaDebug;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Pane pane2;

    private Pixel[][] grid;

    private ResourceBundle resourceBundle;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        lineList = new ArrayList<>();
        initContent();
        initDDA(null);
        initTab1(null);
    }

    @FXML
    private void clear() {
        if (labelCoordinate.isVisible()) {
            initContent();
        } else {
            pane2.getChildren().clear();
            textAreaDebug.setText("");
        }
        if (circleList.size() != 0) {
            circleList.clear();
        }
        if (pixelList.size() != 0) {
            pixelList.clear();
        }
        check = false;
    }

    private void initContent() {
        paneTile.getChildren().clear();
        textAreaDebug.setText("");
        lineList.clear();
        X_TILES = 895 / tileSize;
        Y_TILES = 500 / tileSize;

        if (paneTile.getScene() != null) {
            paneTile.getScene().setOnKeyPressed(null);
        }
        paneTile.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                labelCoordinate.setText(" x=" + (int) event.getX() / tileSize + " y=" + (int) event.getY() / tileSize);
            }
        });
        grid = new Pixel[X_TILES][Y_TILES];
        createContent();
    }

    private void createContent() {
        for (int y = 0; y < Y_TILES; y++) {
            for (int x = 0; x < X_TILES; x++) {
                Pixel pixel = new Pixel(x, y, tileSize);

                grid[x][y] = pixel;

                paneTile.getChildren().add(pixel);
            }
        }
    }

    public void cageSetting(javafx.event.ActionEvent actionEvent) {
        List<Integer> choices = new ArrayList<>();
        initChoices(choices);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<Integer>(tileSize, choices);
        dialog.setTitle(resourceBundle.getString("settings"));
        dialog.setHeaderText("");
        dialog.setContentText(resourceBundle.getString("size_of_cage"));
        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == 1) {
                tileSetting(null);
                tileSize = 2;
            } else {
                tileSize = result.get();
            }
            initContent();
        }
    }

    public void tileSetting(ActionEvent actionEvent) {
        Pixel.setVisibleStoke(!Pixel.isVisibleStoke());
        initContent();
    }

    private void initChoices(List<Integer> choices) {
        choices.add(1);
        choices.add(2);
        choices.add(3);
        choices.add(4);
        choices.add(5);
        choices.add(10);
        choices.add(15);
        choices.add(20);
    }

    private void paintPixelsByColor(List<Pixel> pixels, Color color) {
        for (Pixel pixel : pixels) {
            pixel.getRectangle().setFill(color);
        }
    }

    private void paintPixelByColor(Pixel pixel, Color color) {
        pixel.getRectangle().setFill(color);
    }

    public void initDDA(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("algoritm_CDA"));
        paneTile.setOnMouseClicked(mouseEventEventHandlerDDA());
    }

    public void initAlgorithmBR(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("algoritm_BR"));
        paneTile.setOnMouseClicked(mouseEventEventHandlerBR());

    }

    public void initAlgorithmBY(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("algoritm_BY"));
        paneTile.setOnMouseClicked(mouseEventEventHandlerBY());
    }

    public void initAlgorithmCircle(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("circle"));
        paneTile.setOnMouseClicked(mouseEventEventHandlerCircle());
    }

    public void initAlgorithmHyperbola(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("hyperbola"));
        new AlgorithmHyperbola(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
    }

    public void initAlgorithmEllips(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("ellips"));
        paneTile.setOnMouseClicked(mouseEventEventHandlerElips());
    }

    public void initAlgorithmParabola(ActionEvent actionEvent) {
        nameOfAlgorithm.setText(resourceBundle.getString("parabola"));
        firstPixel = grid[0][0];
        endPixel = grid[grid[1].length - 1][grid[1].length - 1];
        endPixel.setEnd(true);
        new AlgorithmParabola(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
        endPixel.setEnd(false);
    }

    public void initAlgorithmErmit(ActionEvent actionEvent) {
        clear();
        pane2.setOnMouseReleased(mouseEventEventHandlerForInterpolationErmit());
        pane2.setOnMousePressed(mouseEventEventHandlerForInterpolationErmit());
        pane2.setOnMouseDragged(mouseEventEventHandlerForInterpolationErmit());
        nameOfAlgorithm.setText(resourceBundle.getString("ErmitForm"));
    }

    public void initAlgorithmBrezie(ActionEvent actionEvent) {
        clear();
        pane2.setOnMouseReleased(mouseEventEventHandlerForInterpolationBezie());
        pane2.setOnMousePressed(mouseEventEventHandlerForInterpolationBezie());
        pane2.setOnMouseDragged(mouseEventEventHandlerForInterpolationBezie());
        nameOfAlgorithm.setText(resourceBundle.getString("BrezieForm"));
    }

    public void initB_Splain(ActionEvent actionEvent) {
        clear();
        initChoseForBSplain();

        pane2.setOnMouseReleased(mouseEventEventHandlerForInterpolationB_Splain());
        pane2.setOnMousePressed(mouseEventEventHandlerForInterpolationB_Splain());
        pane2.setOnMouseDragged(mouseEventEventHandlerForInterpolationB_Splain());
        nameOfAlgorithm.setText(resourceBundle.getString("B_Splain"));
    }

    private void initChoseForBSplain() {
        List<Integer> choices = new ArrayList<>();
        for (int i = 4; i < 11; i++) {
            choices.add(i);
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<Integer>(numberOfCircles, choices);
        dialog.setTitle(resourceBundle.getString("settings"));
        dialog.setHeaderText("");
        dialog.setContentText(resourceBundle.getString("number_of_circles"));
        Optional<Integer> result = dialog.showAndWait();
        if (result.isPresent()) {
            numberOfCircles = result.get();
        }
    }

    public void initAlgorithmJarvis(ActionEvent actionEvent) {
        clear();
        paneTile.setOnMouseClicked(mouseEventEventHandlerForJarvis());
        nameOfAlgorithm.setText(resourceBundle.getString("jarvis"));
    }

    public void initAlgorithmGraham(ActionEvent actionEvent) {
        clear();
        paneTile.setOnMouseClicked(mouseEventEventHandlerForGraham());
        nameOfAlgorithm.setText(resourceBundle.getString("graham"));
    }

    public void initAlgorithmSimplePrimer(ActionEvent actionEvent) {
        paneTile.setOnMouseClicked(mouseEventEventHandlerSimplePrimer());
        nameOfAlgorithm.setText(resourceBundle.getString("simple_primer"));
    }

    public void initAlgorithmLineSeedFilling(ActionEvent actionEvent) {
        paneTile.setOnMouseClicked(mouseEventEventHandlerLineSeedFilling());
        nameOfAlgorithm.setText(resourceBundle.getString("line_seed_filling"));
    }

    public void initAlgorithmAlgorithmOrderedListOfEdges(ActionEvent actionEvent) {
        paneTile.setOnMouseClicked(mouseEventEventHandlerForOrderedListOfEdges());
        nameOfAlgorithm.setText(resourceBundle.getString("ordered_list_of_edges"));
    }

    public void initAlgorithmDelaunayTriangulation(ActionEvent actionEvent) {
        paneTile.setOnMouseClicked(mouseEventEventHandlerForDelaunayTriangulation());
        nameOfAlgorithm.setText(resourceBundle.getString("delaunay_triangulation"));
        pixelList.clear();
    }

    public void initAlgorithmCohenSutherland(ActionEvent actionEvent) {
        clear();
        cohenSutherlandCutoff = new CohenSutherlandCutoff(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
        lineList.clear();
        paneTile.setOnMouseClicked(mouseEventEventHandlerDDA());
        nameOfAlgorithm.setText(resourceBundle.getString("Cohen_Sutherland_algorithm"));
    }

    public void initAlgorithmCyrusBeck(ActionEvent actionEvent) {
        clear();
        cyrusBeck = new CyrusBeck(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
        lineList.clear();
        paneTile.setOnMouseClicked(mouseEventEventHandlerDDA());
        nameOfAlgorithm.setText(resourceBundle.getString("Cyrus_Beck"));
    }

    public void initAlgorithmRoberts(ActionEvent actionEvent) {

        clear();
        nameOfAlgorithm.setText(resourceBundle.getString("roberts_algorithm"));
        Figure figure = null;
        File selectedFile = getFile();
        if (selectedFile != null) {
            clear();
            LoaderFigureFromFile loaderFigureFromFile = new LoaderFigureFromFile();
            figure = loaderFigureFromFile.loadFigure(selectedFile, true, tileSize);
            drawFigureByDDA(figure);
        }
        robertsAlgorithm = new RobertsAlgorithm(grid, lineList, X_TILES, Y_TILES, textAreaDebug, resourceBundle);

    }

    private EventHandler<MouseEvent> mouseEventEventHandlerCircle() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                findFirstTile(event);
                new AlgorithmCircle(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                firstPixel.setStart(false);
                check = false;
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerElips() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                findFirstTile(event);
                new AlgorithmEllipse(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                firstPixel.setStart(false);
                check = false;
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerDDA() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (check) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (findEndTile(event) == 1) {
                        AlgorithmDDA algorithmDDA = new AlgorithmDDA(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                        lineList.add(new Edge(algorithmDDA.getStartPixel(), algorithmDDA.getEndPixel(), algorithmDDA.getLine()));
                        firstPixel.setStart(false);
                        endPixel.setEnd(false);
                    }
                }
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    findFirstTile(event);
                }
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerBR() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (check) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (findEndTile(event) == 1) {
                        new AlgorithmBR(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                        firstPixel.setStart(false);
                        endPixel.setEnd(false);
                    } else {
                    }
                }
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    findFirstTile(event);
                }
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerBY() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (check) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (findEndTile(event) == 1) {
                        new AlgorithmBY(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                        firstPixel.setStart(false);
                        endPixel.setEnd(false);
                    } else {
                    }
                }
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    findFirstTile(event);
                }
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerForInterpolationErmit() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (check) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    endCircle = findCircle(event);
                    newLine(event);
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    endTempCircle = findCircle(event);

                    drawMainLine(endCircle, endTempCircle);
                    Interpolation interpolation = new Interpolation(firstCircle, endCircle, firstTempCircle, endTempCircle, pane2, textAreaDebug, resourceBundle);
                    interpolation.setErmit(true);
                    interpolation.init();
                    check = !check;
                }
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    firstCircle = findCircle(event);
                    newLine(event);
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    firstTempCircle = findCircle(event);
                    drawMainLine(firstCircle, firstTempCircle);
                    check = !check;
                }

            }
            if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                drawTempLine(event.getX(), event.getY(), line);
            }

        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerForInterpolationBezie() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (check) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    endCircle = findCircle(event);
                    newLine(event);
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    endTempCircle = findCircle(event);

                    drawMainLine(endCircle, endTempCircle);
                    Interpolation interpolation = new Interpolation(firstCircle, endCircle, firstTempCircle, endTempCircle, pane2, textAreaDebug, resourceBundle);
                    interpolation.setErmit(false);
                    interpolation.init();
                    check = !check;
                }
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    firstCircle = findCircle(event);
                    newLine(event);
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    firstTempCircle = findCircle(event);
                    drawMainLine(firstCircle, firstTempCircle);
                    check = !check;
                }

            }
            if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                drawTempLine(event.getX(), event.getY(), line);
            }

        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerForInterpolationB_Splain() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (circleList.size() == numberOfCircles) {
                new B_Splain(circleList, pane2, textAreaDebug, resourceBundle);
            } else {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    Circle circle = new Circle(event.getSceneX(), event.getY(), RADIUS, Color.GREEN);
                    pane2.getChildren().add(circle);
                    circleList.add(circle);
                }
            }
        };
        return mouseEventEventHandler;
    }

    private EventHandler<? super MouseEvent> mouseEventEventHandlerForJarvis() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                findTile(event);
            }
            paintPixelsByColor(pixelList, Color.GREEN);
        };
        return mouseEventEventHandler;
    }

    private EventHandler<? super MouseEvent> mouseEventEventHandlerForGraham() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                findTile(event);
            }
            paintPixelsByColor(pixelList, Color.GREEN);
        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerSimplePrimer() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Pixel pixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
                AlgorithmFilling algorithmFilling = new AlgorithmFilling(grid, pixel, fillingColor, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                algorithmFilling.simplePrimer(pixel);
            }

        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerLineSeedFilling() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Pixel pixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
                AlgorithmFilling algorithmFilling = new AlgorithmFilling(grid, pixel, fillingColor, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                algorithmFilling.lineSeedFilling(pixel);
            }

        };
        return mouseEventEventHandler;
    }

    private EventHandler<MouseEvent> mouseEventEventHandlerForOrderedListOfEdges() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                Pixel pixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
                AlgorithmFillingOfRaster algorithmFillingOfRaster = new AlgorithmFillingOfRaster(grid, fillingColor, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
                algorithmFillingOfRaster.rideOderOfListOfReb(lineList);
                lineList.clear();
            }

        };
        return mouseEventEventHandler;
    }

    private EventHandler<? super MouseEvent> mouseEventEventHandlerForDelaunayTriangulation() {
        EventHandler<MouseEvent> mouseEventEventHandler = event -> {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                findTile(event);
            }
            paintPixelsByColor(pixelList, Color.BLUE);
        };
        return mouseEventEventHandler;
    }

    private void newLine(MouseEvent event) {
        line = new Line();
        line.setStartX(event.getX());
        line.setEndX(event.getX());
        line.setStartY(event.getY());
        line.setEndY(event.getY());
        pane2.getChildren().add(line);
    }

    private void drawMainLine(Circle start, Circle end) {
        line.setStartX(start.getCenterX());
        line.setStartY(start.getCenterY());
        line.setEndX(end.getCenterX());
        line.setEndY(end.getCenterY());
        line.setStroke(Color.BLACK);
    }

    private void drawTempLine(double centerX, double centerY, Line line) {
        line.setEndX(centerX);
        line.setEndY(centerY);
        line.setStroke(Color.RED);
    }

    private void findFirstTile(MouseEvent event) {
        firstPixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
        firstPixel.setStart(true);
        check = true;
    }

    private int findEndTile(MouseEvent event) {
        endPixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
        endPixel.setEnd(true);
        check = false;
        if (firstPixel.equals(endPixel)) {
            Dialogs.getInstance().errorDialog(resourceBundle.getString("error"), resourceBundle.getString("text_error"));
            return 0;
        }
        return 1;
    }

    private int findTile(MouseEvent event) {
        Pixel pixel = grid[(int) event.getX() / tileSize][(int) event.getY() / tileSize];
        for (Pixel pixel1 : pixelList) {
            if (pixel.equals(pixel1)) {
                Dialogs.getInstance().errorDialog(resourceBundle.getString("error"), resourceBundle.getString("text_error"));
                return 0;
            }
        }
        pixelList.add(pixel);

        return 1;
    }

    private Circle findCircle(MouseEvent event) {
        Circle circle = new Circle(event.getX(), event.getY(), RADIUS, Color.GREEN);
        pane2.getChildren().add(circle);
        return circle;
    }

    public void initTab1(Event event) {
        if (menuBar != null) {
            ObservableList<Menu> menus = menuBar.getMenus();
            menus.get(0).setDisable(false);
            menus.get(1).setDisable(false);
            menus.get(2).setDisable(true);
            menus.get(3).setDisable(false);
            menus.get(4).setDisable(false);
            menus.get(5).setDisable(false);
            menus.get(6).setDisable(false);
            menus.get(7).setDisable(false);
            menus.get(8).getItems().get(0).setDisable(false);
            menus.get(8).getItems().get(1).setDisable(false);
            labelCoordinate.setVisible(true);
        }
    }

    public void initTab2(Event event) {
        if (menuBar != null) {
            ObservableList<Menu> menus = menuBar.getMenus();
            menus.get(0).setDisable(true);
            menus.get(1).setDisable(true);
            menus.get(2).setDisable(false);
            menus.get(3).setDisable(true);
            menus.get(4).setDisable(true);
            menus.get(5).setDisable(true);
            menus.get(6).setDisable(true);
            menus.get(7).setDisable(true);
            menus.get(8).getItems().get(0).setDisable(true);
            menus.get(8).getItems().get(1).setDisable(true);
            labelCoordinate.setVisible(false);
        }
    }

    public void initLoadFigureFromFile(ActionEvent actionEvent) {
        File selectedFile = getFile();
        if (selectedFile != null) {
            clear();
            LoaderFigureFromFile loaderFigureFromFile = new LoaderFigureFromFile();
            Figure figure = loaderFigureFromFile.loadFigure(selectedFile, true, tileSize); // потом передать в другой класс
            drawFigureByDDA(figure);
            paneTile.getScene().setOnKeyPressed(moveFigureKeyEvent(figure));
        }
    }

    private File getFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString("choseFile"));
        fileChooser.setInitialDirectory(new File(resourceBundle.getString("initDirectoryPath")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(resourceBundle.getString("descriptionFileType"), "*.properties"));
        return fileChooser.showOpenDialog(mainStage);
    }

    private EventHandler<KeyEvent> moveFigureKeyEvent(Figure figure) {
        EventHandler<KeyEvent> eventEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.D)) {
                    moveAction(figure, 1, 0);
                } else if (event.getCode().equals(KeyCode.A)) {
                    moveAction(figure, -1, 0);
                } else if (event.getCode().equals(KeyCode.W)) {
                    moveAction(figure, 0, -1);
                } else if (event.getCode().equals(KeyCode.S)) {
                    moveAction(figure, 0, 1);
                } else if (event.getCode().equals(KeyCode.P)) {
                    changeAction(figure, -0.5);
                } else if (event.getCode().equals(KeyCode.M)) {
                    changeAction(figure, 0.5);
                } else if (event.getCode().equals(KeyCode.L)) {
                    rotateAction(figure);
                }
            }
        };
        return eventEventHandler;
    }

    private void rotateAction(Figure figure) {
        double alpha = Dialogs.getInstance().showDialog(resourceBundle.getString("alpha"));
        int m = 0;
        int n = 0;
        do {
            m = Dialogs.getInstance().showDialog(resourceBundle.getString("m"));
            n = Dialogs.getInstance().showDialog(resourceBundle.getString("n"));

        } while (m > grid.length || n > grid[0].length || m < 0 || n < 0);

        Figure newFigure = TransformFigure.getInstance().rotationFigure(figure, Math.toRadians(alpha), m, n, grid.length, grid[1].length);
        clear();
        drawFigureByDDA(newFigure);
        paneTile.getScene().setOnKeyPressed(moveFigureKeyEvent(newFigure));
    }

    private void changeAction(Figure figure, double changeSize) {
        if (figure.getSize() + changeSize > 0) {
            Figure newFigure = TransformFigure.getInstance().changeSizeOfFigure(figure, changeSize, grid.length, grid[1].length);
            clear();
            drawFigureByDDA(newFigure);
            paneTile.getScene().setOnKeyPressed(moveFigureKeyEvent(newFigure));
        }
    }

    private void moveAction(Figure figure, final int MIGRATION_CONSTANT_X, final int MIGRATION_CONSTANT_Y) {
        Figure newFigure = TransformFigure.getInstance().moveFigure(figure, MIGRATION_CONSTANT_X, MIGRATION_CONSTANT_Y, grid.length, grid[1].length);
        clear();
        drawFigureByDDA(newFigure);
        paneTile.getScene().setOnKeyPressed(moveFigureKeyEvent(newFigure));
    }

    private void drawFigureByDDA(Figure figure) {
        for (Edge edge : figure.getEdgeList()) {

            AlgorithmDDA algorithmDDA = new AlgorithmDDA(grid, edge.getStarPixel(), edge.getEndPixel(), X_TILES, Y_TILES, textAreaDebug, resourceBundle);
            if (edge.getStarPixel().getX() == 9 && edge.getStarPixel().getY() == 13 || edge.getEndPixel().getX() == 9 && edge.getEndPixel().getY() == 13) {
                lineList.add(new Edge(edge.getStarPixel(), edge.getEndPixel(), algorithmDDA.getLine(), false));
            } else {
                lineList.add(new Edge(edge.getStarPixel(), edge.getEndPixel(), algorithmDDA.getLine(), true));
            }
        }
    }

    public void paintTheFigure(ActionEvent actionEvent) {
        String listenerJarvis = mouseEventEventHandlerForJarvis().getClass().getTypeName();
        String listenerGraham = mouseEventEventHandlerForGraham().getClass().getTypeName();
        if (listenerJarvis != null) {
            if (paneTile.getOnMouseClicked().getClass().getTypeName().equals(listenerJarvis)) {
                AlgorithmJarvis algorithmJarvis = new AlgorithmJarvis(pixelList, grid, textAreaDebug, resourceBundle);
                List<Pixel> jarvis = algorithmJarvis.jarvis();
                paintLineByList(jarvis);
                paintPixelsByColor(jarvis, Color.RED);
                paneTile.setOnMouseClicked(null);
            } else {
                if (listenerGraham != null) {
                    if (paneTile.getOnMouseClicked().getClass().getTypeName().equals(listenerGraham)) {
                        AlgorithmGraham algorithmGraham = new AlgorithmGraham(pixelList, grid, textAreaDebug, resourceBundle);
                        Stack<Pixel> pixelStack = algorithmGraham.graham();
                        textAreaDebug.setText(textAreaDebug.getText() + "\n" + resourceBundle.getString("stackOfVertex") + pixelStack + "\n");
                        List<Pixel> graham = new ArrayList<>(pixelStack);
                        paintLineByList(graham);
                        paintPixelsByColor(graham, Color.RED);
                        paneTile.setOnMouseClicked(null);
                    }
                } else {
                    // тип обработать
                }

            }
        } else {
            //тип обработать нужно
        }
    }

    public void doDelaunayTriangulation(ActionEvent actionEvent) {
        AlgorithmTriangulationOfDelaunay algorithmTriangulationOfDelaunay = new AlgorithmTriangulationOfDelaunay(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
        algorithmTriangulationOfDelaunay.algorithm(pixelList);
    }

    private void paintLineByList(List<Pixel> pixelList) {
        for (int i = 0; i < pixelList.size() - 1; i++) {
            pixelList.get(i).setStart(true);
            pixelList.get(i + 1).setEnd(true);
            new AlgorithmDDA(grid, X_TILES, Y_TILES, textAreaDebug, resourceBundle);
            pixelList.get(i).setStart(false);
            pixelList.get(i + 1).setEnd(false);
        }
    }

    public void aboutProgram(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resourceBundle.getString("about_program"));
        alert.setHeaderText("");
        alert.setContentText(resourceBundle.getString("author"));

        alert.showAndWait();
    }

    public void docCippingCohen(ActionEvent actionEvent) {
        cohenSutherlandCutoff.startAlgorithm(lineList);
    }

    public void doСlippingCyrusBeck(ActionEvent actionEvent) {
        cyrusBeck.startAlgorithm(lineList);
    }

    public void doСlippingRoberts(ActionEvent actionEvent) {
        TransformFigure.getInstance().temp(robertsAlgorithm.getFigure(), grid);
    }
}
