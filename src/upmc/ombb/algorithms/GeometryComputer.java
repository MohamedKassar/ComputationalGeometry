package upmc.ombb.algorithms;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

/**
 * 
 * @author Mohamed Tarek KASSAR
 * @author mohamed.kassar@etu.upmc.fr
 * 
 */
public final class GeometryComputer {

	/**
	 * Oriented minimum bounding box Godfried Toussaint Algorithm, complexity
	 * O(n)
	 * 
	 * @param convexHull
	 *            list of convex hull points sorted in clockwise order without
	 *            repetition
	 * @return list contains 4 corners of the oriented minimum bounding box
	 *         (Points)
	 */
	public static List<Point2D> computeOMBB(List<Point2D> convexHull) {
		/*
		 * 0 : top, 1 : right, 2 : bottom, 3 : left
		 */
		int[] p = { 0, 0, 0, 0 };

		/*
		 * initial box vectors
		 */
		Point2D[] boxVecs = { new Point2D(1, 0), new Point2D(0, -1),
				new Point2D(-1, 0), new Point2D(0, 1) };

		List<Point2D> rect, rectMin = null;
		Point2D[] convexHullUnitVecs = new Point2D[convexHull.size()];
		/*
		 * calculating the vector for each convex hull side and searching
		 * min/max x/y points
		 */
		for (int i = 0; i < convexHull.size(); i++) {
			convexHullUnitVecs[i] = new Point2D(convexHull.get(
					(i + 1) % convexHull.size()).getX()
					- convexHull.get(i).getX(), convexHull.get(
					(i + 1) % convexHull.size()).getY()
					- convexHull.get(i).getY());

			if (convexHull.get(p[0]).getY() < convexHull.get(i).getY()) {
				p[0] = i;
			} else if (convexHull.get(p[2]).getY() > convexHull.get(i).getY()) {
				p[2] = i;
			}
			if (convexHull.get(p[1]).getX() < convexHull.get(i).getX()) {
				p[1] = i;
			} else if (convexHull.get(p[3]).getX() > convexHull.get(i).getX()) {
				p[3] = i;
			}
		}

		/*
		 * computing the minimum box
		 */
		for (int i = 0; i < convexHull.size(); i++) {

			/*
			 * computing the minimum angle .....................................
			 * Note: angle method of the Point2D class compute the angle between
			 * the vector represented by the current point and the vector
			 * represented by the given point.
			 */
			double delta = convexHullUnitVecs[p[0]].angle(boxVecs[0]), temp;
			int cible = 0;
			for (int j = 1; j < 4; j++) {
				if (delta > (temp = convexHullUnitVecs[p[j]].angle(boxVecs[j]))) {
					delta = temp;
					cible = j;
				}
			}

			/*
			 * computing the box corners
			 */
			rect = getOBBCorners(convexHull.get(p[cible]),
					convexHull.get((p[cible] + 1) % convexHull.size()),
					convexHull.get(p[(cible + 1) % 4]),
					convexHull.get(p[(cible + 3) % 4]),
					convexHull.get(p[(cible + 2) % 4]));

			if (rectangleArea(rect) < rectangleArea(rectMin)) {
				rectMin = rect;
			}

			/*
			 * update the box vectors
			 */
			boxVecs[cible] = convexHullUnitVecs[p[cible]];
			boxVecs[(cible + 1) % 4] = new Point2D(boxVecs[cible].getY(),
					-boxVecs[cible].getX());
			boxVecs[(cible + 2) % 4] = new Point2D(-boxVecs[cible].getX(),
					-boxVecs[cible].getY());
			boxVecs[(cible + 3) % 4] = new Point2D(-boxVecs[cible].getY(),
					boxVecs[cible].getX());
			/*
			 * move to the next point
			 */
			p[cible] = (p[cible] + 1) % convexHull.size();
		}
		return rectMin;
	}

	private static double rectangleArea(List<Point2D> rect) {
		if (rect == null) {
			return Double.MAX_VALUE;
		}
		return rect.get(1).distance(rect.get(0))
				* rect.get(1).distance(rect.get(2));
	}

	/**
	 * 
	 * Return the oriented box corners that passes by the given points,
	 * complexity O(1)
	 * 
	 * @param fstTopPoint
	 *            first point belongs to the top side of the rectangle
	 * @param sndTopPoint
	 *            second point belongs to the top side of the rectangle
	 * @param rLPoint
	 *            point belongs to the right/left side of the rectangle
	 * @param lRPoint
	 *            point belongs to the right/left side of the rectangle
	 * @param bottomPoint
	 *            point belongs to the bottom side of the rectangle
	 * @return list of Points contains corners of the rectangle
	 */
	private static List<Point2D> getOBBCorners(Point2D fstTopPoint,
			Point2D sndTopPoint, Point2D rLPoint, Point2D lRPoint,
			Point2D bottomPoint) {

		double a = (sndTopPoint.getY() - fstTopPoint.getY())
				/ (sndTopPoint.getX() - fstTopPoint.getX()), firstA = a;
		double b = fstTopPoint.getY() - a * fstTopPoint.getX();
		List<Point2D> tempList = new ArrayList<Point2D>();

		/*
		 * check if a is infinite or zero (i.e : the box is not oriented)
		 */
		if (Double.isInfinite(a) || a == 0) {
			tempList.add(fstTopPoint);
			tempList.add(sndTopPoint);
			tempList.add(rLPoint);
			tempList.add(lRPoint);
			tempList.add(bottomPoint);
			double xMin = fstTopPoint.getX(), xMax = fstTopPoint.getX(), yMin = fstTopPoint
					.getY(), yMax = fstTopPoint.getY();
			for (Point2D point : tempList) {
				if (xMin > point.getX())
					xMin = point.getX();
				else if (xMax < point.getX())
					xMax = point.getX();
				if (yMin > point.getY())
					yMin = point.getY();
				else if (yMax < point.getY())
					yMax = point.getY();
			}
			tempList.clear();

			tempList.add(new Point2D(xMax, yMax));
			tempList.add(new Point2D(xMin, yMax));
			tempList.add(new Point2D(xMin, yMin));
			tempList.add(new Point2D(xMax, yMin));
			return tempList;
		}
		double x1, x2, x3, x4, y1, y2, y3, y4;
		/*
		 * point 1 (first corner)
		 */
		x1 = (a * rLPoint.getY() + rLPoint.getX() - a * b) / (a * a + 1);
		y1 = a * x1 + b;

		/*
		 * point 2 (second corner)
		 */
		x2 = (a * lRPoint.getY() + lRPoint.getX() - a * b) / (a * a + 1);
		y2 = a * x2 + b;

		/*
		 * point 3 (third)
		 */
		if (Math.abs(lRPoint.getY() - y2) < 0.01
				&& Math.abs(lRPoint.getX() - x2) < 0.01) {

			a = -1 / firstA;
		} else {
			a = (lRPoint.getY() - y2) / (lRPoint.getX() - x2);
		}
		b = y2 - a * x2;
		// if (Double.isInfinite(a)) { x3 = x2;y3 = bottomPoint.getY(); } else {
		x3 = (a * bottomPoint.getY() + bottomPoint.getX() - a * b)
				/ (a * a + 1);
		y3 = a * x3 + b;// }

		/*
		 * point 4 (fourth corner)
		 */
		if (Math.abs(rLPoint.getY() - y1) < 0.01
				&& Math.abs(rLPoint.getX() - x1) < 0.01) {
			a = -1 / firstA;
		} else {
			a = (rLPoint.getY() - y1) / (rLPoint.getX() - x1);
		}
		b = y1 - a * x1;
		// if (Double.isInfinite(a)) {x4 = x1;y4 = y3;} else {
		x4 = (a * bottomPoint.getY() + bottomPoint.getX() - a * b)
				/ (a * a + 1);
		y4 = a * x4 + b;

		tempList.add(new Point2D(x1, y1));
		tempList.add(new Point2D(x2, y2));
		tempList.add(new Point2D(x3, y3));
		tempList.add(new Point2D(x4, y4));

		return tempList;
	}

	/**
	 * Remove points having x abscissa, complexity O(n)
	 * 
	 * @param pointsCloud
	 *            a points cloud
	 * @param x
	 *            abscissa
	 */
	private static void filterPointsHaveX(List<Point2D> pointsCloud, int x) {
		List<Point2D> temp = new ArrayList<Point2D>();
		pointsCloud.forEach(p -> {
			if (p.getX() == x) {
				temp.add(p);
			}
		});

		if (temp.size() > 2) {
			Point2D p1 = temp.get(0), p2 = temp.get(0);
			for (Point2D p : temp) {
				if (p1.getY() < p.getY()) {
					p1 = p;
				} else if (p2.getY() > p.getY()) {
					p2 = p;
				}
			}
			pointsCloud.removeAll(temp);
			pointsCloud.add(p1);
			pointsCloud.add(p2);

		}
	}

	/**
	 * Compute the area of a convex hull, complexity O(n)
	 * 
	 * @param convexHull
	 *            list of points of convex hull sorted without repetition
	 * @return area of the polygon
	 * @see http://www.mathwords.com/a/area_convex_polygon.htm
	 */
	private static double polygonArea(List<Point2D> convexHull) {
		int i = convexHull.size();
		double first = 0, second = 0;
		Point2D currentPoint, nextPoint;
		while (i > 0) {
			nextPoint = convexHull.get(i % convexHull.size());
			currentPoint = convexHull.get(i - 1);
			first += currentPoint.getX() * nextPoint.getY();
			second += currentPoint.getY() * nextPoint.getX();
			i--;
		}
		return Math.abs(0.5 * (first - second));
	}

	public static double rectagleQuality(List<Point2D> convexHull,
			List<Point2D> rectangle) {
		return (rectangleArea(rectangle) / polygonArea(convexHull)) - 1;
	}

	public static double circleQuality(List<Point2D> convexHull, Circle circle) {
		return (Math.PI * circle.getRadius() * circle.getRadius() / polygonArea(convexHull)) - 1;
	}

	/**
	 * Convex hull Graham algorithm, complexity ~ O(n*log(n))
	 * 
	 * @param pointsCloud
	 *            a points cloud
	 * @return list of points of convex hull sorted on clockwise order without
	 *         repetition
	 */
	public static List<Point2D> convexHull(List<Point2D> pointsCloud) {

		List<Point2D> points1 = new ArrayList<Point2D>(pointsCloud);
		double xMin = points1.get(0).getX(), xMax = points1.get(0).getX();
		for (Point2D p : points1) {
			if (p.getX() > xMax) {
				xMax = p.getX();
			} else if (p.getX() < xMin) {
				xMin = p.getX();
			}
		}

		for (int i = (int) xMin; i <= xMax; i++) {
			filterPointsHaveX(points1, i);
		}

		Point2D temp1, temp2, temp3;
		List<Point2D> top = new ArrayList<Point2D>();
		List<Point2D> bottom = new ArrayList<Point2D>();

		for (int i = (int) xMin; i <= xMax; i++) {
			temp1 = null;
			temp2 = null;
			for (Point2D p : points1) {
				if (p.getX() == i) {
					if (temp1 == null) {
						temp1 = p;
					} else /* if (!temp1.equals(p)) */{
						temp2 = p;
						break;
					}
				}
			}

			if (temp2 != null) {
				points1.remove(temp1);
				points1.remove(temp2);
				if (temp1.getY() >= temp2.getY()) {
					top.add(temp1);
					bottom.add(temp2);
				} else/* if (temp1.getY() < temp2.getY()) */{
					top.add(temp2);
					bottom.add(temp1);
				}
			} else if (temp1 != null) {
				points1.remove(temp1);
				top.add(temp1);
				bottom.add(temp1);
			}
		}

		for (int i = bottom.size() - 1; i >= 0; i--) {
			top.add(bottom.get(i));
		}

		int i = 2;
		while (i < top.size()) {
			temp1 = top.get(i - 2);
			temp2 = top.get(i - 1);
			temp3 = top.get(i);

			if (temp1.equals(temp2)) {
				top.remove(temp1);
				i--;
				continue;
			}

			double sign = (((temp2.getX() - temp1.getX()) * (temp3.getY() - temp2
					.getY())) - ((temp2.getY() - temp1.getY()) * (temp3.getX() - temp2
					.getX())));

			if (sign > 0) {
				top.remove(i - 1);
				i = i - 1 < 2 ? 2 : i - 1;
			} else {
				i++;
			}
		}

		if (top.get(0).equals(top.get(top.size() - 1)))
			top.remove(0);

		return top;
	}

	/**
	 * Minimum circle Ritter approximation algorithm, complexity O(n)
	 * 
	 * @param pointsCloud
	 *            a points cloud
	 * @return circle contains points
	 */

	public static Circle ritter(List<Point2D> pointsCloud) {
		//
		List<Point2D> pointsBis = new ArrayList<Point2D>(pointsCloud);

		Point2D dummy = pointsBis.remove(0), x1 = dummy, x2 = dummy;

		for (Point2D p : pointsBis) {
			if (dummy.distance(p) > x1.distance(dummy)) {
				x1 = p;
			}

		}
		pointsBis.add(0, dummy);
		for (Point2D p : pointsBis) {
			if (x1.distance(p) > x1.distance(x2))
				x2 = p;
		}

		pointsBis.remove(x1);
		pointsBis.remove(x2);
		Point2D center = new Point2D((x1.getX() + x2.getX()) / 2,
				(x1.getY() + x2.getY()) / 2);
		double radius = x1.distance(x2) / 2;

		while (!pointsBis.isEmpty()) {
			double temp = center.distance(pointsBis.get(0));
			if (temp <= radius) {
				pointsBis.remove(0);
			} else {
				if (!pointsBis.isEmpty()) {
					double beta = (temp - (temp + radius) / 2) / temp;
					double alpha = ((temp + (radius)) / 2) / temp;

					center = new Point2D((alpha * center.getX() + beta
							* pointsBis.get(0).getX()),
							(alpha * center.getY() + beta
									* pointsBis.get(0).getY()));

					radius = center.distance(pointsBis.remove(0));
				}
			}
		}

		return new Circle(center.getX(), center.getY(), (int) radius);
	}
}
