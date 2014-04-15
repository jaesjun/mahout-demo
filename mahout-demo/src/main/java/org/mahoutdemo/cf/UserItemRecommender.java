package org.mahoutdemo.cf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.mahoutdemo.model.Preference;

public class UserItemRecommender {
	private DataModel dataModel;
	
	public UserItemRecommender(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	
	public List<Preference> recommendBasedUser(Class<? extends UserSimilarity> similarityClass, long userId, int howMany) throws TasteException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends UserSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		UserSimilarity similarity = constructor.newInstance(dataModel);
		
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
		List<RecommendedItem> recommendations = recommender.recommend(userId, howMany);
		
		List<Preference> preferences = new ArrayList<Preference>();
		for (RecommendedItem item : recommendations) {
			Preference pref = new Preference();
			pref.setUserId(userId);
			pref.setItemId(item.getItemID());
			pref.setPreference(item.getValue());
			preferences.add(pref);
		}
		
		return preferences;
	}

	public long[] getSimilarUsers(Class<? extends UserSimilarity> similarityClass, long userId, int howMany) throws TasteException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends UserSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		UserSimilarity similarity = constructor.newInstance(dataModel);
		
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(100, similarity, dataModel);
		GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
		return recommender.mostSimilarUserIDs(userId, howMany);
	}
	
	public double getSimilarScoreForUser(Class<? extends UserSimilarity> similarityClass, long userId1, long userId2) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TasteException {
		Constructor<? extends UserSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		UserSimilarity similarity = constructor.newInstance(dataModel);
		return similarity.userSimilarity(userId1, userId2);
	}

	public List<RecommendedItem> getSimilarItems(Class<? extends ItemSimilarity> similarityClass, long itemId, int howMany) throws TasteException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends ItemSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		ItemSimilarity similarity = constructor.newInstance(dataModel);
		
		GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		return recommender.mostSimilarItems(itemId, howMany);
	}

	public List<RecommendedItem> recommendBasedItem(Class<? extends ItemSimilarity> similarityClass, long userId, int howMany) throws TasteException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends ItemSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		ItemSimilarity similarity = constructor.newInstance(dataModel);
		
		GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		return recommender.recommend(userId, howMany);
	}

	public List<RecommendedItem> recommendBasedItemBecause(Class<? extends ItemSimilarity> similarityClass, long userId, long itemId, int howMany) throws TasteException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<? extends ItemSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		ItemSimilarity similarity = constructor.newInstance(dataModel);
		
		GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);
		return recommender.recommendedBecause(userId, itemId, howMany);
	}

	public double getSimilarScoreForItem(Class<? extends ItemSimilarity> similarityClass, long itemId1, long itemId2) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TasteException {
		Constructor<? extends ItemSimilarity> constructor = similarityClass.getConstructor(DataModel.class);
		ItemSimilarity similarity = constructor.newInstance(dataModel);
		return similarity.itemSimilarity(itemId1, itemId2);
	}

}
