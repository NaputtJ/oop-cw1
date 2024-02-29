import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * JavaFX application to plot elevations of a GPS track, for the
 * Advanced task of COMP1721 Coursework 1.
 *
 * @author YOUR NAME HERE
 */
public class PlotApplication extends Application {

  // If attempting the Advanced task, implement your plotting code here.
  // You will need to modify this class definition so that it extends
  // javafx.application.Application

  @Override
  public void start(Stage primaryStage) throws IOException {
    if (getParameters().getRaw().isEmpty()) {
      throw new GPSException("no filename entered");
    }

    XYChart.Series<Number, Number> series = this.readFile(getParameters().getRaw().get(0));

    NumberAxis x = new NumberAxis();
    NumberAxis y = new NumberAxis();

    x.setLabel("Distance (m)");
    y.setLabel("Elevation (m)");

    LineChart<Number, Number> plot = new LineChart<>(x, y);
    plot.setTitle("Elevation Plot");
    plot.getData().add(series);
    plot.setCreateSymbols(false);

    Scene scene = new Scene(plot, 600, 400);

    primaryStage.setScene(scene);
    primaryStage.setTitle("JavaFX Graph Plotter");

    primaryStage.show();
  }

  private XYChart.Series<Number, Number> readFile(String filename) throws IOException {
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName(filename);

    Track track = new Track(filename);
    Point prevPoint = null;
    double distance = 0.f;
    for (int i = 0; i < track.size(); i++){
      Point point = track.get(i);
      if (i == 0) {
        series.getData().add(new XYChart.Data<>(distance, point.getElevation()));
        prevPoint = point;
        continue;
      }

      distance += Point.greatCircleDistance(prevPoint, point);
      series.getData().add(new XYChart.Data<>(distance, point.getElevation()));
      prevPoint = point;
    }
    return series;
  }

  public static void main(String[] args) {
    // If attempting the Advanced task, uncomment the line below
    launch(args);
  }
}
