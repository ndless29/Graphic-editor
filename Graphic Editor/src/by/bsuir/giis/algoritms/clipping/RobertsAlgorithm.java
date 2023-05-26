package by.bsuir.giis.algoritms.clipping;

import by.bsuir.giis.functions.Functions;
import by.bsuir.giis.model.Edge;
import by.bsuir.giis.model.Figure;
import by.bsuir.giis.model.Pixel;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Andrey
 */
public class RobertsAlgorithm {
    private Pixel[][] grid;
    private TextArea textAreaDebug;
    private ResourceBundle resourceBundle;
    private Functions functions = Functions.getInstance();
    private List<Edge> edgeList = new ArrayList<>();
    private List<Edge> figure;

    private int X_TILES;
    private int Y_TILES;

    public RobertsAlgorithm(Pixel[][] grid, List<Edge> figure, int X_TILES, int Y_TILES, TextArea textAreaDebug, ResourceBundle resourceBundle) {
        this.grid = grid;
        this.textAreaDebug = textAreaDebug;
        this.resourceBundle = resourceBundle;
        this.figure = figure;
        this.X_TILES = X_TILES;
        this.Y_TILES = Y_TILES;
    }

    private void start(List<Edge> edgeList) {
        edgeList.forEach(edge -> visibleverge(edge));
    }

    private void visibleverge(Edge edge) {
        if (!Visible_Side(edge)) {
            edgeList.remove(edge);
        }
    }

    private boolean Visible_Side(Edge edge) {
        double[] d1 = null;// Make_Vector (edge.getStarPixel().getX(), edge.getEndPixel().getY());
        double[] d2 = null; //Make_Vector(edge.getEndPixel().getZ(), edge.getEndPixel().getX());
        double[] n2 = CrossProduct(d1, d2);
        double[] n = Normalize(n2);


        double[] camera = {0, 0, 100};

        double sum = 0;
        for (int i = 0; i < 3; i++)
            sum += camera[i] * n[i];

        if (sum < 0) return true;
        else return false;
    }

    private double[] Make_Vector(double p1[], double p2[]) {
        double[] p = new double[3];
        for (int i = 0; i < 3; i++)
            p[i] = p1[i] - p2[i];
        return p;
    }

    private double[] CrossProduct(double d1[], double d2[]) {
        double[] p = new double[3];

        p[0] = d1[1] * d2[2] - d1[2] * d2[1];
        p[1] = d1[2] * d2[0] - d1[0] * d2[2];
        p[2] = d1[0] * d2[1] - d1[1] * d2[0];

        return p;
    }

    private double[] Normalize(double p[]) {
        double[] p2 = new double[3];
        double l = Math.sqrt(p[0] * p[0] + p[1] * p[1] + p[2] * p[2]);
        for (int i = 0; i < 3; i++)
            p2[i] = p[i] / l;
        return p2;
    }

    public List<Edge> getFigure() {
        return figure;
    }

    public void setFigure(List<Edge> figure) {
        this.figure = figure;
    }
}
