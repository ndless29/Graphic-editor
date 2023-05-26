package by.bsuir.giis.algoritms.circle;

import by.bsuir.giis.dialog.Dialogs;
import by.bsuir.giis.functions.Functions;
import by.bsuir.giis.model.Pixel;
import javafx.scene.control.TextArea;

import java.util.ResourceBundle;

/**
 * @author Andrey
 */
public class AlgorithmParabola {
    private ResourceBundle resourceBundle;

    private Pixel[][] grid;
    private int X_TILES;
    private int Y_TILES;
    private TextArea textAreaDebug;
    private Functions functions = Functions.getInstance();

    // Конструктор для инициализации алгоритма Построения параболы
    // grid - поле пикселей, x_Tiles и y_Tiles - размер пикселя по x и y.
    public AlgorithmParabola(Pixel[][] grid, int x_TILES, int y_TILES, TextArea textAreaDebug, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.grid = grid;
        X_TILES = x_TILES;
        Y_TILES = y_TILES;
        this.textAreaDebug = textAreaDebug;

        textAreaDebug.setText(textAreaDebug.getText() + resourceBundle.getString("parabola") + "\n");
        begin();
    }

    private void begin() {
        int x = 0;
        int y = 0;
        int tempX = functions.findEndTile(grid, X_TILES, Y_TILES).getX();
        int tempY = functions.findEndTile(grid, X_TILES, Y_TILES).getY();
        int limit = grid[0].length;

        int p = Math.abs(Dialogs.getInstance().showDialog(resourceBundle.getString("hyperbolaTitleP"), limit));
        int error = 1 - 2 * p;
        int sign = 0;
        int i = 0;

        while (x < limit) {
            functions.plot(grid, x, y, i, error, textAreaDebug, resourceBundle);
            i++;
            if (error > 0) {
                sign = Math.abs(error) - Math.abs(y * y - 2 * p * (x + 1));
                if (sign <= 0) {
                    x++;
                    y++;
                    error = error - 2 * p + 2 * y + 1;
                } else {
                    x++;
                    error = error - 2 * p;
                }
            } else {
                sign = Math.abs((int) Math.pow(y + 1, 2) - 2 * p * x) - Math.abs(error);
                if (sign <= 0) {
                    y++;
                    error = error + 2 * y + 1;
                } else {
                    x++;
                    y++;
                    error = error - 2 * p + 2 * y + 1;
                }
            }
        }
        functions.plot(grid, x, y, i + 1, error, textAreaDebug, resourceBundle);


    }
}

