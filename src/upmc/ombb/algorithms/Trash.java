//package upmc.ombb.algorithms;
//
//import java.util.List;
//
//import javafx.geometry.Point2D;
//
//public class Trash {
//	/**
//	 * 
//	 * @param convexHull
//	 *            a sorted convex hull whith out repetition of points
//	 * @return list contains 4 corners of the oriented minimum bounding box
//	 *         (Points)
//	 */
//	public static List<Point2D> computeOMBB(List<Point2D> convexHull) {
//		List<Point2D> rect = null, temp;
//
//		for (int i = 0; i < convexHull.size(); i++) {
//			Point2D A = convexHull.get(i);
//			Point2D B = convexHull.get((i + 1) % convexHull.size());
//
//			double teta = -Math.atan((B.getY() - A.getY())
//					/ (B.getX() - A.getX()));
//			double tempRotatedX = rotateX(convexHull.get(0), teta);
//			double tempRotatedY = rotateY(convexHull.get(0), teta);
//			double rotatedLeftX = tempRotatedX, rotatedRightX = tempRotatedX, rotatedTopY = tempRotatedY, rotatedBottomY = tempRotatedY;
//			Point2D tempPoint, C = convexHull.get(0), D = convexHull.get(0), E = convexHull
//					.get(0), E_bis = convexHull.get(0);
//
//			for (int j = 0; j < convexHull.size(); j++) {
//				tempPoint = convexHull.get(j);
//				tempRotatedX = rotateX(tempPoint, teta);
//				tempRotatedY = rotateY(tempPoint, teta);
//				if (rotatedRightX < tempRotatedX) {
//					C = tempPoint;
//					rotatedRightX = tempRotatedX;
//				} else if (rotatedLeftX > tempRotatedX) {
//					D = tempPoint;
//					rotatedLeftX = tempRotatedX;
//				}
//				if (rotatedTopY < tempRotatedY) {
//					E = tempPoint;
//					rotatedTopY = tempRotatedY;
//				} else if (rotatedBottomY > tempRotatedY) {
//					E_bis = tempPoint;
//					rotatedBottomY = tempRotatedY;
//				}
//			}
//
//			if (Math.abs(rotatedBottomY - rotateY(A, teta)) < 0.01)
//				temp = GeometryComputer.getOBBCorners(A, B, C, D, E);
//			else
//				temp = GeometryComputer.getOBBCorners(A, B, C, D, E_bis);
//
//			if (/* temp.size() == 4 && */GeometryComputer.rectangleArea(rect) > GeometryComputer.rectangleArea(temp)) {
//				rect = temp;
//			}
//		}
//		// System.out.println("Mine = "+ rectangleArea(rect));
//		return rect;
//	}
//
//	/**
//	 * Applique the rotation matrix on the given point p
//	 * 
//	 * @param p
//	 *            point
//	 * @param teta
//	 *            angle in radians
//	 * @return x coordinate of the rotated point
//	 */
//	private static double rotateX(Point2D p, double teta) {
//		return p.getX() * Math.cos(teta) - p.getY() * Math.sin(teta);
//	}
//
//	/**
//	 * Applique the rotation matrix on the given point p
//	 * 
//	 * @param p
//	 *            point
//	 * @param teta
//	 *            angle in radians
//	 * @return y coordinate of the rotated point
//	 */
//	private static double rotateY(Point2D p, double teta) {
//		return p.getX() * Math.sin(teta) + p.getY() * Math.cos(teta);
//	}
//
//}
