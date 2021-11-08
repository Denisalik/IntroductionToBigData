//ThisBuild / version := "0.1.0-SNAPSHOT"
//
//ThisBuild / scalaVersion := "2.13.7"
//
//lazy val root = (project in file("."))
//  .settings(
//    name := "lab6"
//  )

name := "spark-wordcount"

version := "1.0"


scalaVersion := "2.12.10"
val sparkVersion = "3.2.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion