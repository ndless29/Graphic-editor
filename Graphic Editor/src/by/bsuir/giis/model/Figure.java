package by.bsuir.giis.model;

import java.util.List;
import java.util.Map;

/**
 * @author Andrey
 */
public class Figure {
    private List<Pixel> pixelList;
    private List<Edge> edgeList;
    private double size;

    public Figure() {
    }

    public Figure(List<Pixel> pixelList, List<Edge> edgeList, double size) {
        this.pixelList = pixelList;
        this.edgeList = edgeList;
        this.size = size;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }

    public List<Pixel> getPixelList() {
        return pixelList;
    }

    public void setPixelList(List<Pixel> pixelList) {
        this.pixelList = pixelList;
    }

    public double getSize() {
        return size;
    }
}
