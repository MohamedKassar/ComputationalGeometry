package upmc.ombb.algorithms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * 
 * @author Mohamed Tarek KASSAR
 * @author mohamed.kassar@etu.upmc.fr
 *
 */
public class Frame extends Application {
	private static final int MILLION = 1_000_000;

	private final String style = "-fx-background-color:  rgba(176,196,222, 0.8); -fx-background-radius : 5;";
	private final String styleHover = "-fx-background-color: rgba(176,196,222, 0.5); -fx-background-radius : 5;";

	private final AnchorPane root = new AnchorPane();
	private final Scene scene = new Scene(root);

	private Canvas canvas = new Canvas(5000, 5000);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
	private final ScrollPane pane = new ScrollPane(canvas);

	private final VBox zoomBox = new VBox();
	private final HBox buttonBox = new HBox();

	private final Button nextSampleButton = new Button("Next sample");
	private final Button previousSampleButton = new Button("Previous sample");

	private final Label fileNameLabel = new Label();

	private final Label report = new Label("Report :");
	private final Label qualityOMBBLabel = new Label();
	private final Label qualityCircleoLabel = new Label();
	private final Label eTOMBB = new Label();
	private final Label eTCH = new Label();
	private final Label eTMC = new Label();

	private final VBox textFlow = new VBox(report, qualityOMBBLabel,
			qualityCircleoLabel, eTOMBB, eTMC, eTCH);
	private final Label infoLabel = new Label(
			"R Clear\nB OMBB\nH Convex hull\nC Minimum circle (Ritter)\n"
					+ "N next sample\nP previous sample ");

	private Slider zoom = new Slider(50, 250, 100);
	private final Label zoomLabel = new Label(" Zoom");

	private final Scale scale = new Scale(1, 1, 0, 0);

	private final Path[] paths = Files.list(Paths.get("Samples")).toArray(
			Path[]::new);
	private int indexFileRectangle = -1;

	private String currentFileName;
	private List<Point2D> currentPointCloud;
	private List<Point2D> currentConvexHull = null;
	private List<Point2D> currentRectangle = null;
	private Circle currentCircle = null;

	private final EventHandler<ActionEvent> clear = e -> {
		clear();
		fileNameLabel.setText(currentFileName);
		paintPoints(currentPointCloud);
		currentConvexHull = null;
		currentRectangle = null;
		currentCircle = null;
	};

	private final EventHandler<ActionEvent> drawRectangle = e -> {
		if (currentRectangle != null || currentConvexHull != null) {
			clear.handle(null);
		}
		double endCH;
		double begin = System.nanoTime();
		currentConvexHull = GeometryComputer.convexHull(currentPointCloud);
		endCH = System.nanoTime();
		currentRectangle = GeometryComputer.computeOMBB(currentConvexHull);
		double end = System.nanoTime();
		eTOMBB.setText("Elapsed time (OMBB) : " + (end - begin) / MILLION
				+ " millis");
		eTCH.setText("Elapsed time (convex hull) : " + (endCH - begin)
				/ MILLION + " millis");
		// paintPolyG(convexHull, Color.BLUE);
		// paintPolyGFillStroke(Trash.computeOMBB(currentConvexHull),
		// Color.BISQUE);
		paintPolyGFillStroke(currentRectangle, Color.BLUE);
		paintPolyGStroke(currentConvexHull);
	};

	private final EventHandler<ActionEvent> drawConvexHull = e -> {
		if (currentRectangle != null || currentConvexHull != null) {
			clear.handle(null);
		}
		double start = System.nanoTime();
		currentConvexHull = GeometryComputer.convexHull(currentPointCloud);
		double end = System.nanoTime();
		eTCH.setText("Elapsed time (convex hull) : " + (end - start) / MILLION
				+ " millis");
		paintPolyGFillStroke(currentConvexHull, Color.BLUE);
	};

	private final EventHandler<ActionEvent> drawCircle = e -> {
		double begin = System.nanoTime();
		currentCircle = GeometryComputer.ritter(currentPointCloud);
		double end = System.nanoTime();
		eTMC.setText("Elapsed time (Ritter) : " + (end - begin) / MILLION
				+ " millis");
		paintCircleStroke(currentCircle, Color.GREEN);
		if (currentConvexHull == null)
			currentConvexHull = GeometryComputer.convexHull(currentPointCloud);
	};

	private final EventHandler<ActionEvent> computQuality = e -> {
		if (currentRectangle == null)
			drawRectangle.handle(null);
		if (currentCircle == null)
			drawCircle.handle(null);
		double circleQual = GeometryComputer.circleQuality(currentConvexHull,
				currentCircle);
		double rectQual = GeometryComputer.rectagleQuality(currentConvexHull,
				currentRectangle);
		qualityOMBBLabel.setText("OMBB quality : " + rectQual);
		qualityCircleoLabel.setText("Circle quality : " + circleQual);
	};

	private final EventHandler<ActionEvent> nextSample = e -> {
		indexFileRectangle++;
		if (indexFileRectangle == paths.length) {
			indexFileRectangle = 0;
		}
		drawNewSample();
	};

	private final EventHandler<ActionEvent> previousSample = e -> {
		clear();
		indexFileRectangle--;
		if (indexFileRectangle < 0) {
			indexFileRectangle = paths.length - 1;
		}
		drawNewSample();
	};

	private void drawNewSample() {
		Path temp = /* Paths.get("Samples/test-265.points"); */paths[indexFileRectangle];/* 394 */
		currentPointCloud = getCloud(temp);
		currentFileName = "File name : " + temp.getFileName().toString()
				+ " - Points number : " + currentPointCloud.size();
		// currentPointCloud = new ArrayList<Point>(Arrays.asList(new
		// Point(100,200),new Point(200,300),new Point(300,200),new
		// Point(200,100), new Point(125,275)));

		clear.handle(null);
		drawRectangle.handle(null);
		drawCircle.handle(null);
		computQuality.handle(null);
	}

	public Frame() throws IOException {
		Arrays.parallelSort(
				paths,
				Comparator.comparing(Path::getFileName,
						Comparator.naturalOrder()));

		// for (int i = 0; i < 1663; i++)
		nextSample.handle(null);

	}

	private static List<Point2D> getCloud(Path path) {
		List<Point2D> conv = new ArrayList<Point2D>();
		try {/* 110 */
			Files.readAllLines(path)
			/* .stream() */
			.forEach(
					line -> conv.add(new Point2D(Integer.parseInt(line
							.split(" ")[0]),
							Integer.parseInt(line.split(" ")[1]))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conv;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		root.getChildren().add(pane);

		pane.setPannable(true);

		AnchorPane.setBottomAnchor(zoomBox, 50.0);
		AnchorPane.setRightAnchor(zoomBox, 50.0);
		root.getChildren().add(zoomBox);

		Insets insets = new Insets(5, 0, 0, 10);
		HBox.setMargin(fileNameLabel, insets);
		buttonBox.getChildren().add(previousSampleButton);
		buttonBox.getChildren().add(nextSampleButton);
		buttonBox.getChildren().add(fileNameLabel);

		previousSampleButton.setStyle("-fx-background-radius : 5;");
		nextSampleButton.setStyle("-fx-background-radius : 5;");

		buttonBox.setStyle(style);
		buttonBox.setOnMouseEntered((e) -> {
			buttonBox.setStyle(styleHover);
		});
		buttonBox.setOnMouseExited((e) -> {
			buttonBox.setStyle(style);
		});

		previousSampleButton.setOnAction(previousSample);
		nextSampleButton.setOnAction(nextSample);

		root.setOnKeyTyped(keyE -> {
			switch (keyE.getCharacter()) {
			case "R":
			case "r":
				clear.handle(null);
				break;
			case "B":
			case "b":
				drawRectangle.handle(null);
				break;
			case "H":
			case "h":
				drawConvexHull.handle(null);
				break;
			case "C":
			case "c":
				drawCircle.handle(null);
				break;
			// case "Q":
			// case "q":
			// computQuality.handle(null);
			// break;
			case "P":
			case "p":
				previousSample.handle(null);
				break;
			case "N":
			case "n":
				nextSample.handle(null);
				break;
			}
		});

		AnchorPane.setTopAnchor(buttonBox, 20.0);
		AnchorPane.setLeftAnchor(buttonBox, 50.0);
		root.getChildren().add(buttonBox);

		// HBox.setMargin(textFlow, insets);
		textFlow.setStyle(style);
		AnchorPane.setTopAnchor(textFlow, 40.0);
		AnchorPane.setRightAnchor(textFlow, 50.0);
		root.getChildren().add(textFlow);

		infoLabel.setStyle(style);
		AnchorPane.setTopAnchor(infoLabel, 65.0);
		AnchorPane.setLeftAnchor(infoLabel, 50.0);
		root.getChildren().add(infoLabel);

		zoomBox.getChildren().add(zoom);
		zoom.setOrientation(Orientation.VERTICAL);
		zoom.setShowTickLabels(true);
		zoom.setShowTickMarks(true);
		zoom.setMajorTickUnit(10);
		zoom.setMajorTickUnit(50);

		scale.xProperty().bind(zoom.valueProperty().divide(100));
		scale.yProperty().bind(zoom.valueProperty().divide(100));

		zoomBox.getChildren().add(zoomLabel);
		zoomBox.setStyle(style);
		zoomBox.setOnMouseEntered((e) -> {
			zoomBox.setStyle(styleHover);
		});

		zoomBox.setOnMouseExited((e) -> {
			zoomBox.setStyle(style);
		});

		// zoomLabel.setStyle("-fx-font-weight: bold;");

		canvas.getTransforms().add(scale);

		primaryStage.setScene(scene);
		primaryStage.setMinHeight(480);
		primaryStage.setMinWidth(640);
		primaryStage.setTitle("Minimum Bounding Box - MT KASSAR");
		primaryStage.show();
	}

	public void paintPoints(List<Point2D> points) {
		gc.setFill(Color.BLACK);
		points/* .stream() */.forEach(p -> {
			gc.fillOval(p.getX() - 2, p.getY() - 2, 4, 4);
		});
	}

	/*
	  
	 * */

	public void paintCircleStroke(Circle circle, Color c) {
		gc.setGlobalAlpha(1);
		gc.setStroke(c);
		gc.setLineWidth(2);
		gc.strokeOval(circle.getCenterX() - circle.getRadius(),
				circle.getCenterY() - circle.getRadius(),
				circle.getRadius() * 2, circle.getRadius() * 2);
	}

	public void paintPolyGStroke(List<Point2D> points) {
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		gc.strokePolygon(points.stream().mapToDouble(Point2D::getX).toArray(),
				points.stream().mapToDouble(Point2D::getY).toArray(),
				points.size());
	}

	public void paintPolyGFillStroke(List<Point2D> points, Color c) {
		paintPolyGStroke(points);
		gc.setFill(c);
		gc.setGlobalAlpha(0.5);
		gc.fillPolygon(points.stream().mapToDouble(Point2D::getX).toArray(),
				points.stream().mapToDouble(Point2D::getY).toArray(),
				points.size());
	}

	public void clear() {
		canvas = new Canvas(5000, 5000);
		gc = canvas.getGraphicsContext2D();
		canvas.getTransforms().add(scale);
		zoom.setValue(100);
		pane.setContent(canvas);
	}

}
