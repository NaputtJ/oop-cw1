import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

/**
 * Represents a point in space and time, recorded by a GPS sensor.
 *
 * @author Naputt Jarungpolpipat
 */
public class Track {
    private ArrayList<Point> points;

    public Track(){
        this.points = new ArrayList<Point>();
    }

    public Track(String filename) throws IOException{
        this.points = new ArrayList<Point>();
        this.readFile(filename);
    }

    public void readFile(String filename) throws IOException {
        this.points.clear();
        try (Scanner scanner = new Scanner(new File(filename))){
            if (scanner.hasNextLine()){
                scanner.nextLine();
            }

            while(scanner.hasNextLine()){
                String[] data = scanner.nextLine().split(",");
                if (data.length != 4){
                    throw new GPSException("invalid data in file: " + filename);
                }

                this.points.add(new Point(ZonedDateTime.parse(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])));
            }
        } catch (FileNotFoundException e){
            throw new FileNotFoundException("failed to open file: " + filename);
        }
    }

    public int size(){
        return this.points.size();
    }

    public Point get(int i){
        if (i >= size() || i < 0){
            throw  new GPSException("invalid point index");
        }
        return this.points.get(i);
    }

    public void add(Point point){
        this.points.add(point);
    }

    public Point lowestPoint(){
        if (this.points.size() == 0){
            throw new GPSException("track is empty");
        }

        Point minPoint = null;
        for (Point p : this.points){
            if (minPoint == null){
                minPoint = p;
                continue;
            }
            if (p.getElevation() < minPoint.getElevation()){
                minPoint = p;
            }
        }
        return minPoint;
    }

    public Point highestPoint(){
        if (this.points.size() == 0){
            throw new GPSException("track is empty");
        }

        Point maxPoint = null;
        for (Point p : this.points){
            if (maxPoint == null){
                maxPoint = p;
                continue;
            }
            if (p.getElevation() > maxPoint.getElevation()){
                maxPoint = p;
            }
        }
        return maxPoint;
    }

    public double totalDistance(){
        if (this.points.size() < 2){
            throw new GPSException("track have fewer 2 point");
        }

        Point prevPoint = null;
        double distance = 0;
        for (Point p: this.points){
            if (prevPoint == null){
                prevPoint = p;
                continue;
            }

            distance += Point.greatCircleDistance(prevPoint, p);
            prevPoint = p;
        }

        return distance;
    }

    public double averageSpeed(){
        if (this.points.size() < 2){
            throw new GPSException("track have fewer 2 point");
        }

        double time = ChronoUnit.SECONDS.between(this.points.get(0).getTime(), this.points.get(this.points.size() - 1).getTime());
        return this.totalDistance() / time;
    }
}
