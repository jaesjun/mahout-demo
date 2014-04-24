package org.mahoutdemo.clustering;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.iterator.ClusterWritable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.common.iterator.sequencefile.PathFilters;
import org.apache.mahout.common.iterator.sequencefile.PathType;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileDirValueIterable;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

public class ClusterGenerator {
	public static List<List<Cluster>> runKMeansClusterer(List<Vector> vectors, DistanceMeasure measure, 
		int numClusters, int maxIterations, double convergenceDelta) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
	    Path samples = new Path("samples");
	    Path output = new Path("output");
	    HadoopUtil.delete(conf, samples);
	    HadoopUtil.delete(conf, output);

	    writeSampleData(samples, vectors);
	    Path clustersIn = new Path(output, "random-seeds");
	    RandomSeedGenerator.buildRandom(conf, samples, clustersIn, numClusters, measure);
	    KMeansDriver.run(samples, clustersIn, output, convergenceDelta, maxIterations, true, 0.0, true);
	    return loadClustersWritable(output);
	}

	public static List<List<Cluster>> runFuzzyKMeansClusterer(List<Vector> vectors, DistanceMeasure measure, 
		int numClusters, int maxIterations, float m, double threshold) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Path samples = new Path("samples");
	    Path output = new Path("output");
	    HadoopUtil.delete(conf, samples);
	    HadoopUtil.delete(conf, output);

	    writeSampleData(samples, vectors);
	    Path clustersIn = new Path(output, "random-seeds");
	    RandomSeedGenerator.buildRandom(conf, samples, clustersIn, numClusters, measure);
	    FuzzyKMeansDriver.run(samples, clustersIn, output, threshold, maxIterations, m, true, true, threshold, true);
    
	    return loadClustersWritable(output);
	}
	
	public static List<List<Cluster>> runCanopyClusterer(List<Vector> vectors, DistanceMeasure measure, 
		double t1, double t2) throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = new Configuration();
		Path samples = new Path("samples");
	    Path output = new Path("output");
	    HadoopUtil.delete(conf, samples);
	    HadoopUtil.delete(conf, output);

	    writeSampleData(samples, vectors);
	    CanopyDriver.buildClusters(conf, samples, output, new ManhattanDistanceMeasure(), t1, t2, 0, true);
	    return loadClustersWritable(output);
	}

	protected static void writeSampleData(Path output, List<Vector> vectors) throws IOException {
	    Configuration conf = new Configuration();
	    FileSystem fs = FileSystem.get(output.toUri(), conf);
	    SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, output, Text.class, VectorWritable.class);
	    try {
	      int i = 0;
	      for (Vector vector : vectors) {
	    	  VectorWritable vw = new VectorWritable(vector);
	    	  writer.append(new Text("sample_" + i++), vw);
	      }
	    } finally {
	    	Closeables.close(writer, false);
	    }
	}

	protected static List<List<Cluster>> loadClustersWritable(Path output) throws IOException {
		List<List<Cluster>> clusters = Lists.newArrayList();
		
	    Configuration conf = new Configuration();
	    FileSystem fs = FileSystem.get(output.toUri(), conf);
	    for (FileStatus s : fs.listStatus(output, new ClustersFilter())) {
			List<Cluster> cluster = readClustersWritable(s.getPath());
			clusters.add(cluster);
	    }
	    
	    return clusters;
	}

	protected static List<Cluster> readClustersWritable(Path clustersIn) {
	    List<Cluster> clusters = Lists.newArrayList();
	    Configuration conf = new Configuration();
	    for (ClusterWritable value : new SequenceFileDirValueIterable<ClusterWritable>(clustersIn, PathType.LIST,
	        PathFilters.logsCRCFilter(), conf)) {
			Cluster cluster = value.getValue();
			clusters.add(cluster);
	    }
	    return clusters;
	}

	static class ClustersFilter implements PathFilter {
		@Override
		public boolean accept(Path path) {
			String pathString = path.toString();
			return pathString.contains("/clusters-");
		}
	}

}
