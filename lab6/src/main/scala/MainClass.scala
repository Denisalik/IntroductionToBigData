import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object MainClass {
  def main(args: Array[String]){

    val conf = new SparkConf().setAppName("appName")
    val sc = new SparkContext(conf)

    val inputPath = args(0)
    val outputPath = args(1)

    val textFile = sc.textFile(inputPath)
    val counts = textFile.flatMap(line => line.split(" ")).filter(word=> !(word.contains(".")
      || word.contains("?") || word.contains(",") || word.contains("!") || word.contains(":")
      || word.contains("‘") || word.contains("“") || word.contains(";") || word.contains("’")
      ))
      .map(word => (word, 1))
      .reduceByKey(_ + _)
      .map(item=>item.swap)
      .sortByKey(false, 1)
      .map{case (s, n) => s"$s\t$n"}
    counts.saveAsTextFile(outputPath)
  }
}
