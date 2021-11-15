import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, IDF, RegexTokenizer}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object Main {
  def main(args: Array[String]): Unit = {

    val schema = StructType("ItemID Sentiment SentimentText".split(" ").map(fieldName => {
      if(fieldName == "ItemID" || fieldName == "Sentiment")StructField(fieldName, IntegerType, nullable = false)
      else StructField(fieldName, StringType, nullable= false)
    }))
    val path = "twitter_sentiment_data.csv"
    val spark = SparkSession
      .builder()
      .appName("lab7")
      .getOrCreate()
    import spark.implicits._
    val data = spark.read.format("csv")
      .schema(schema)
      .option("header", "true")
      .load(path)


    val removeRepetitive = udf{ str: String => str.replaceAll("((.))\\1+","$1").trim.toLowerCase()}
    val noRepetitiveData = data.withColumn("Collapsed", removeRepetitive('SentimentText))

    val tokenizer = new RegexTokenizer()
      .setInputCol("Collapsed")
      .setOutputCol("Tokens")
      .setPattern("\\s+")
    val wordsData = tokenizer.transform(noRepetitiveData)

    val hashingTF = new HashingTF().setInputCol("Tokens").setOutputCol("tf").setNumFeatures(200000)
    val featurizedData = hashingTF.transform(wordsData)

    val idf = new IDF().setInputCol("tf").setOutputCol("tfidfFeatures")
    val idfModel = idf.fit(featurizedData)
    val rescaledData = idfModel.transform(featurizedData)


    val lr = new LogisticRegression()
      .setFeaturesCol("tfidfFeatures")
      .setLabelCol("Sentiment")
    val lrModel = lr.fit(rescaledData)

    val pipe = new Pipeline()
      .setStages(Array(
        tokenizer,
        hashingTF,
        idf,
        lr
      ))
    val model = pipe.fit(noRepetitiveData)

//    val testData = Seq("It iiis Working", "It doesn't work").toDF().withColumn("Collapsed", removeRepetitive('value))
//    val testLabeled = model.transform(testData)
//    println(testLabeled.select("value", "predicted").show())

    val paramGrid = new ParamGridBuilder()
      .addGrid(lr.tol, Array(1e-20, 1e-10, 1e-5))
      .addGrid(lr.maxIter, Array(100, 200))
      .build()

    val cv = new CrossValidator()
      .setEstimator(pipe)
      .setEvaluator(new BinaryClassificationEvaluator()
        .setRawPredictionCol("prediction")
        .setLabelCol("Sentiment"))
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(3)
      .setParallelism(2)

    val cv_model = cv.fit(noRepetitiveData)

    println("Max iter " + cv_model.bestModel.asInstanceOf[PipelineModel].stages(3).asInstanceOf[LogisticRegressionModel].getMaxIter)
    println("Max Tol " + cv_model.bestModel.asInstanceOf[PipelineModel].stages(3).asInstanceOf[LogisticRegressionModel].getTol)
}
}
