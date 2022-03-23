package com.mach.core.model.repository;

import com.mach.core.db.AutomationResultsMongoDao;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TextRepository extends AutomationResultsMongoDao {

    private static TextRepository instance;

    private MongoCollection<Document> collection;

    private TextRepository() {
        super("automation-results");
        collection = database.getCollection("texts");
    }

    private static TextRepository getInstance() {
        if (instance == null) {
            instance = new TextRepository();
        }
        return instance;
    }

    public static String getText(final String... textKey) {
        return getTexts(textKey).isEmpty() ? null : getTexts(textKey).get(0);
    }

    public static List<String> getTexts(final String... textKey) {
        List<String> result = new ArrayList<>();
        FindIterable<Document> dbResults = getInstance().collection.find(Filters.in("key", textKey));
        dbResults.forEach(dbResult -> result.addAll(dbResult.getList("values", String.class)));
        return result;
    }

}
