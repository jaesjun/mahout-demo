package org.mahoutdemo.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.clustering.UncommonDistributions;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;

public class VectorGenerator {
	public static List<Vector> generateRandomVector(int count, double mx, double my, double sd) {
	    RandomUtils.useTestSeed();

		List<Vector> vectors = new ArrayList<Vector>();
		
		for (int i = 0; i < count; i++) {
			vectors.add(new DenseVector(
				new double[] {UncommonDistributions.rNorm(mx, sd),UncommonDistributions.rNorm(my, sd), }));
		}
		
		return vectors;
	}
}
