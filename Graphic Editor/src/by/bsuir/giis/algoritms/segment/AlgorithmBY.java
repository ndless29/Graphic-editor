package by.bsuir.giis.algoritms.segment;

import by.bsuir.giis.functions.Functions;
import by.bsuir.giis.model.Pixel;
import javafx.scene.control.TextArea;

import java.util.ResourceBundle;

/**
 * @author Andrey
 */
public class AlgorithmBY {
    private ResourceBundle resourceBundle;

    private Pixel[][] grid;
    private int X_TILES;
    private int Y_TILES;
    private TextArea textAreaDebug;
    private Functions functions = Functions.getInstance();

    // Конструктор для инициализации алгоритма Ву
    // grid - поле пикселей, x_Tiles и y_Tiles - размер пикселя по x и y.
    public AlgorithmBY(Pixel[][] grid, int x_TILES, int y_TILES, TextArea textAreaDebug, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.grid = grid;
        X_TILES = x_TILES;
        Y_TILES = y_TILES;
        this.textAreaDebug = textAreaDebug;

        begin();
    }

    //Начало алгоритма
    private void begin() {
        textAreaDebug.setText(textAreaDebug.getText() + resourceBundle.getString("algoritm_BY") + "\n");

        //Начальный пиксель
        Pixel startPixel = functions.findLeftmostTilePolygon(grid, X_TILES, Y_TILES);
        //Конечный пиксель
        Pixel endPixel = functions.findEndTile(grid, X_TILES, Y_TILES);

        int x1 = startPixel.getX();
        int y1 = startPixel.getY();

        int x2 = endPixel.getX();
        int y2 = endPixel.getY();

        //Проверка расположения пикселей
        boolean checkX = startPixel.getX() > endPixel.getX();
        boolean checkY = startPixel.getY() > endPixel.getY();

        //Вычисление разности между конечным пикселем и начальным
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        // Изменение пикселя по оси х и у
        int changeX;
        int changeY;
        if (x1 < x2) {
            changeX = 1;
        } else {
            changeX = -1;
        }

        if (y1 < y2) {
            changeY = 1;
        } else {
            changeY = -1;
        }

        int x = x1;
        int y = y1;

        double e = 0;

        if (dx == 0 || dy == 0) {
            drawVerticalOrHorizontalLine(x1, y1, endPixel.getX(), endPixel.getY(), (int) dx, (int) dy, checkX, checkY);
            return;
        }
        int i = 1;
        if (dx >= dy) {
            e = (dy / dx - 0.5);
            while (i <= dx) {
                if (e >= 0) {
                    y += changeY;
                    e -= 1;
                }
                x += changeX;
                e += dy / dx;
                functions.plot(grid, x, y, 0, changeY, e, i, textAreaDebug, resourceBundle);
                i++;
            }
        } else {
            e = (dy / dx - 0.5);
            while (i <= dy) {
                if (e >= 0) {
                    x += changeX;
                    e -= 1;
                }
                y += changeY;
                e += dx / dy;
                functions.plot(grid, x, y, changeX, 0, e, i, textAreaDebug, resourceBundle);
                i++;

            }
        }
    }

    private void drawVerticalOrHorizontalLine(int x1, int y1, int x2, int y2, int dx, int dy, boolean checkX, boolean checkY) {
        int length = Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
        for (int i = 0; i < length + 1; i++) {
            if (dx == 0) {
                if (checkY) {
                    functions.plot(grid, x1, y1--, i + 1, textAreaDebug, resourceBundle);
                } else {
                    functions.plot(grid, x1, y1++, i + 1, textAreaDebug, resourceBundle);
                }
            } else if (dy == 0) {
                if (checkX) {
                    functions.plot(grid, x1--, y1, i + 1, textAreaDebug, resourceBundle);
                } else {
                    functions.plot(grid, x1++, y1, i + 1, textAreaDebug, resourceBundle);
                }
            }

        }
    }

}
