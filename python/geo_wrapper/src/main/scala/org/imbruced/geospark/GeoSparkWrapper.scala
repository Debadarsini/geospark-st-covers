package org.imbruced.geospark

import org.apache.spark.sql.SparkSession
import org.datasyslab.geosparksql.utils.GeoSparkSQLRegistrator

object GeoSparkWrapper {

  def registerAll: Unit ={
    val spark: SparkSession = SparkSession.
      builder().
      getOrCreate()
    GeoSparkSQLRegistrator.registerAll(spark)
  }
}