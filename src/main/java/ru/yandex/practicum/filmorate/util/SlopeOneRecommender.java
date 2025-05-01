package ru.yandex.practicum.filmorate.util;

import java.util.HashMap;
import java.util.Map;

public class SlopeOneRecommender {
    private final Map<Long, Map<Long, Double>> diff = new HashMap<>();
    private final Map<Long, Map<Long, Integer>> freq = new HashMap<>();

    // diff и freq
    public void buildDiffMatrix(Map<Long, Map<Long, Double>> data) {
        for (Map<Long, Double> user : data.values()) {
            for (Map.Entry<Long, Double> entry : user.entrySet()) {
                long item1 = entry.getKey();
                diff.putIfAbsent(item1, new HashMap<>());
                freq.putIfAbsent(item1, new HashMap<>());
                for (Map.Entry<Long, Double> entry2 : user.entrySet()) {
                    long item2 = entry2.getKey();
                    int oldCount = freq.get(item1).getOrDefault(item2, 0);
                    double oldDiff = diff.get(item1).getOrDefault(item2, 0.0);
                    double observedDiff = entry.getValue() - entry2.getValue();
                    freq.get(item1).put(item2, oldCount + 1);
                    diff.get(item1).put(item2, oldDiff + observedDiff);
                }
            }
        }
        // нормируем
        for (Long item : diff.keySet()) {
            for (Long item2 : diff.get(item).keySet()) {
                double oldValue = diff.get(item).get(item2);
                int count = freq.get(item).get(item2);
                diff.get(item).put(item2, oldValue / count);
            }
        }
    }

    // Предсказания для одного пользователя
    public Map<Long, Double> predict(Map<Long, Map<Long, Double>> data,
                                     Map<Long, Double> userRatings) {

        Map<Long, Double> predictions = new HashMap<>();
        Map<Long, Integer> frequencies = new HashMap<>();

        for (Long item : diff.keySet()) {
            if (userRatings.containsKey(item))
                continue;  // фильм уже оценён

            double numerator = 0.0;
            int denominator = 0;

            for (Long item2 : userRatings.keySet()) {
                Integer freqItem = freq.get(item).get(item2);
                if (freqItem == null) continue;

                double predicted = diff.get(item).get(item2) + userRatings.get(item2);
                int count = freqItem;

                numerator += predicted * count;
                denominator += count;
            }
            if (denominator > 0) {
                predictions.put(item, numerator / denominator);
                frequencies.put(item, denominator);
            }
        }
        return predictions;
    }
}
