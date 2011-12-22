/*
 * Copyright 2011 University of Minnesota
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.grouplens.lenskit.hello;

import org.grouplens.lenskit.ItemRecommender;
import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.RecommenderEngine;
import org.grouplens.lenskit.baseline.BaselinePredictor;
import org.grouplens.lenskit.baseline.ItemMeanPredictor;
import org.grouplens.lenskit.collections.ScoredLongList;
import org.grouplens.lenskit.core.LenskitRecommenderEngineFactory;
import org.grouplens.lenskit.data.dao.DAOFactory;
import org.grouplens.lenskit.data.dao.EventCollectionDAO;
import org.grouplens.lenskit.data.dao.SimpleFileRatingDAO;
import org.grouplens.lenskit.knn.item.ItemItemRatingPredictor;
import org.grouplens.lenskit.knn.item.ItemItemRecommender;
import org.grouplens.lenskit.norm.BaselineSubtractingNormalizer;
import org.grouplens.lenskit.norm.VectorNormalizer;
import org.grouplens.lenskit.params.UserVectorNormalizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration app for LensKit. This application builds an item-item CF model
 * from a CSV file, then generates recommendations for a user.
 *
 * Usage: java org.grouplens.lenskit.hello.HelloLenskit ratings.csv user
 */
public class HelloLenskit implements Runnable {
    public static void main(String[] args) {
        HelloLenskit hello = new HelloLenskit(args);
        try {
            hello.run();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private String delimiter = "\t";
    private File inputFile = new File("ratings.dat");
    private List<Long> users;

    public HelloLenskit(String[] args) {
        int nextArg = 0;
        boolean done = false;
        while (!done && nextArg < args.length) {
            String arg = args[nextArg];
            if (arg.equals("-d")) {
                delimiter = args[nextArg + 1];
                nextArg += 2;
            } else if (arg.startsWith("-")) {
                throw new RuntimeException("unknown option: " + arg);
            } else {
                inputFile = new File(arg);
                nextArg += 1;
                done = true;
            }
        }
        users = new ArrayList<Long>(args.length - nextArg);
        for (; nextArg < args.length; nextArg++) {
            users.add(Long.parseLong(args[nextArg]));
        }
    }

    public void run() {
        // We first need to configure the data access.
        // We will use a simple delimited file; you can use something else like
        // a database (see JDBCRatingDAO).
        DAOFactory daoFactory;
        try {
            DAOFactory base = new SimpleFileRatingDAO.Factory(inputFile, delimiter);
            // Reading directly from CSV files is slow, so we'll cache it in memory.
            // You can use SoftFactory here to allow ratings to be expunged and re-read
            // as memory limits demand. If you're using a database, just use it directly.
            daoFactory = EventCollectionDAO.Factory.wrap(base);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("%s: not found", inputFile), e);
        }

        // Second step is to create the LensKit factory...
        LenskitRecommenderEngineFactory factory = new LenskitRecommenderEngineFactory(daoFactory);
        // ... and configure the recommender.  The set and setComponent methods
        // are what you use to do that. Here, we want an item-item recommender and
        // rating predictor.
        factory.setComponent(ItemRecommender.class, ItemItemRecommender.class);
        factory.setComponent(RatingPredictor.class, ItemItemRatingPredictor.class);
        // let's use item mean rating as the baseline predictor
        factory.setComponent(BaselinePredictor.class, ItemMeanPredictor.class);
        // and normalize ratings by baseline prior to computing similarities
        // This one has 3 parameters because it uses a role - UserVectorNormalizer -
        // to restrict what kind of vector normalizer we're talking about.
        factory.setComponent(UserVectorNormalizer.class, VectorNormalizer.class,
                             BaselineSubtractingNormalizer.class);
        // There are more parameters, roles, and components that can be set. See the
        // JavaDoc for each recommender algorithm for more information.

        // Now that we have a factory, build a recommender engine from the configuration
        // and data source. This will compute the similarity matrix and return a recommender
        // engine that uses it.
        RecommenderEngine engine = factory.create();

        // To do recommendation, we first open the recommender
        Recommender rec = engine.open();
        // then use it!
        try {
            // we want to recommend items
            ItemRecommender irec = rec.getItemRecommender();
            assert irec != null; // not null because we configured one
            // for users
            for (long user: users) {
                // get 10 recommendation for the user
                ScoredLongList recs = irec.recommend(user, 10);
                System.out.format("Recommendations for %d:\n", user);
                for (long item: recs) {
                    System.out.format("\t%d\n", item);
                }
            }

            // If you want to predict ratings, you can use rec.getRatingPredictor
        } finally {
            // and close it when we're done
            rec.close();
        }
    }
}
