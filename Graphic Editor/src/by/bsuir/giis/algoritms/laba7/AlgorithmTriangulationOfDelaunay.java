package by.bsuir.giis.algoritms.laba7;

import by.bsuir.giis.algoritms.polygon.AlgorithmJarvis;
import by.bsuir.giis.algoritms.segment.AlgorithmDDA;
import by.bsuir.giis.functions.Functions;
import by.bsuir.giis.model.Edge;
import by.bsuir.giis.model.Pixel;
import by.bsuir.giis.model.Triangle;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * @author Andrey
 */
public class AlgorithmTriangulationOfDelaunay {
    private Pixel[][] grid;
    private int X_TILES;
    private int Y_TILES;
    private TextArea textAreaDebug;
    private ResourceBundle resourceBundle;
    private Functions functions = Functions.getInstance();

    public AlgorithmTriangulationOfDelaunay(Pixel[][] grid, int x_TILES, int y_TILES, TextArea textAreaDebug, ResourceBundle resourceBundle) {
        this.grid = grid;
        X_TILES = x_TILES;
        Y_TILES = y_TILES;
        this.textAreaDebug = textAreaDebug;
        this.resourceBundle = resourceBundle;
    }

    public void algorithm(List<Pixel> pixelList) {
        List<Triangle> triangleList = triangleListOfDelone(pixelList);
        AlgorithmJarvis algorithmJarvis = new AlgorithmJarvis(pixelList, grid, textAreaDebug, resourceBundle);
        paintLineByList(algorithmJarvis.jarvis());
        paintTriangle(triangleList, pixelList);
    }

    private List<Triangle> triangleListOfDelone(List<Pixel> pixelList) {
        List<Triangle> triangleList = new ArrayList<>();
        List<Pixel> tempPixelList = new ArrayList<>(pixelList);
        Pixel startPixel = functions.findLeftmostTile(tempPixelList);
        tempPixelList.remove(startPixel);
        Pixel endPixel = functions.findLeftmostTile(tempPixelList);
        tempPixelList.remove(endPixel);
        tempPixelList = new ArrayList<>(pixelList);

        Edge startEdge = new Edge(startPixel, endPixel);
        Stack<Edge> edgeStack = new Stack<>();
        edgeStack.add(startEdge);
        while (!edgeStack.isEmpty()) {
            List <Pixel> pixelWithoutEdge = new ArrayList<>(tempPixelList);
            Edge edge = edgeStack.pop();
            Pixel bestPixel = findBestPixel(edge, pixelWithoutEdge);
            if (bestPixel != null) {
                Edge firstEdge = new Edge(edge.getStarPixel(), bestPixel);
                Edge secondEdge = new Edge(edge.getEndPixel(), bestPixel);

                updateEdgeStack(edgeStack, firstEdge);
                updateEdgeStack(edgeStack, secondEdge);
                triangleList.add(new Triangle(edge.getStarPixel(), edge.getEndPixel(), bestPixel, initTriangleEdgeList(edge,firstEdge, secondEdge)));
                tempPixelList.remove(bestPixel);
            }
        }
        return triangleList;
    }

    private void updateEdgeStack(Stack<Edge> edgeStack, Edge edge) {
        if (edgeStack.contains(edge)) {
            edgeStack.remove(edge);
        } else {
            edgeStack.add(edge);
        }
    }

    private Pixel findBestPixel(Edge edge, List<Pixel> pixelList) {
        Pixel bestPixel = null;
        List<Pixel> rightList = findAllRightPixels(edge, pixelList);
        bestPixel = findConjugatePixel(edge, rightList);
        return bestPixel;
    }

    private List<Pixel> findAllRightPixels(Edge edge, List<Pixel> pixelList) {
        List<Pixel> resultList = new ArrayList<>(pixelList);
        if (resultList.contains(edge.getStarPixel())) {
            resultList.remove(edge.getStarPixel());
        }
        if (resultList.contains(edge.getEndPixel())) {
            resultList.remove(edge.getEndPixel());
        }
        Pixel minPixel = findMinPixel(edge);
        resultList.removeIf(pixel -> pixel.getX() < minPixel.getX());
        return resultList;
    }

    private Pixel findMinPixel(Edge edge) {
        Pixel starPixel = edge.getStarPixel();
        Pixel endPixel = edge.getEndPixel();
        if (starPixel.getX() < endPixel.getX()) {
            return starPixel;
        } else {
            return endPixel;
        }
    }

    private Pixel findConjugatePixel(Edge edge, List<Pixel> pixelList) {
        Pixel result = null;
        int minDistance = 99999999;
        for (Pixel pixel : pixelList) {
            int distance1 = findDistance(edge.getStarPixel(), pixel);
            int distance2 = findDistance(edge.getEndPixel(), pixel);
            int resultDistance = distance1 + distance2;
            if (minDistance > resultDistance) {
                minDistance = resultDistance;
                result = pixel;
            }
        }

        return result;
    }

    private int findDistance(Pixel starPixel, Pixel pixel) {
        return (int) Math.sqrt(Math.pow(starPixel.getX() - pixel.getX(), 2) + Math.pow(starPixel.getY() - pixel.getY(), 2));
    }

    private void paintTriangle(List<Triangle> triangleList, List<Pixel> pixelList) {
        for (Triangle triangle : triangleList) {
            for (Edge edge : triangle.getEdgeList()) {
                new AlgorithmDDA(grid, edge.getStarPixel(), edge.getEndPixel(), X_TILES, Y_TILES, textAreaDebug, resourceBundle);
            }
        }
        for (Pixel pixel : pixelList) {
            pixel.getRectangle().setFill(Color.BLUE);
        }
    }

    private List<Edge> initTriangleEdgeList(Edge liveEdge, Edge firstEdge, Edge secondEdge) {
        List<Edge> triangleEdge = new ArrayList<>();
        triangleEdge.add(liveEdge);
        triangleEdge.add(firstEdge);
        triangleEdge.add(secondEdge);
        return triangleEdge;
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

}