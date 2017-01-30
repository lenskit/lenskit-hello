import org.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer
import org.lenskit.transform.normalize.UserVectorNormalizer
import org.lenskit.api.ItemScorer
import org.lenskit.baseline.BaselineScorer
import org.lenskit.baseline.ItemMeanRatingItemScorer
import org.lenskit.baseline.UserMeanBaseline
import org.lenskit.baseline.UserMeanItemScorer
import org.lenskit.knn.MinNeighbors
import org.lenskit.knn.item.ItemItemScorer
import org.lenskit.knn.item.ModelSize

// ... and configure the item scorer.  The bind and set methods
// are what you use to do that. Here, we want an item-item scorer.
bind ItemScorer to ItemItemScorer.class
// Item-item works best with a minimum neighbor count
set MinNeighbors to 2

// Limit the model size
set ModelSize to 1000

// let's use personalized mean rating as the baseline/fallback predictor.
// 2-step process:
// First, use the user mean rating as the baseline scorer
bind (BaselineScorer, ItemScorer) to UserMeanItemScorer
// Second, use the item mean rating as the base for user means
bind (UserMeanBaseline, ItemScorer) to ItemMeanRatingItemScorer
// and normalize ratings by baseline prior to computing similarities
bind UserVectorNormalizer to BaselineSubtractingUserVectorNormalizer
