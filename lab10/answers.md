#### 1
> Parameters of pregel function are:
* graph
* initialMsg - the message each vertex will receive at the first iteration \\
* maxIterations - the maximum number of iterations to run for
* activeDirection - the direction of edges incident to a vertex that received a message in the previous round on which to run `sendMsg`.
* vprog - the user-defined vertex program which runs on each vertex and receives the inbound message and computes a new vertex value.  On the first iteration the vertex program is invoked on all vertices and is passed the default message.  On subsequent iterations the vertex program is only invoked on those vertices that receive messages.
* sendMsg - a user supplied function that is applied to out edges of vertices that received messages in the current iteration
* mergeMsg - a user supplied function that takes two incoming messages of type A and merges them into a single message of type A.
##### returns the resulting graph at the end of the computation
#### 2
* Any recommendation system. For example in SNS(Twitter, Facebook, VK) feature that suggests to add friend of friends of user. 
* GPS systems. For example algorithm to find path that will take shortest time (evading traffic jam, taking roads with a low traffic) to suggest to user.
#### 3
```
#default
import org.apache.spark.graphx._
val myVertices = sc.makeRDD(Array((1L, "Ann"), (2L, "Bill"),
 (3L, "Charles"), (4L, "Diane"), (5L, "Went to gym this morning")))

val myEdges = sc.makeRDD(Array(Edge(1L, 2L, "is-friends-with"),
 Edge(2L, 3L, "is-friends-with"), Edge(3L, 4L, "is-friends-with"),
 Edge(4L, 5L, "Likes-status"), Edge(3L, 5L, "Wrote-status")))

val myGraph = Graph(myVertices, myEdges)
myGraph.pageRank(0.001).vertices.foreach(println)

/*
(1,0.4390416708169824)
(3,1.1294346981766872)
(5,1.700245122452838)
(4,0.9190514175420745)
(2,0.8122270910114174)
*/
```
```
#new
#page rank evaluate number of edges to the node, so we need to add edges to node 1
import org.apache.spark.graphx._
val myVertices = sc.makeRDD(Array((1L, "Ann"), (2L, "Bill"),
(3L, "Charles"), (4L, "Diane"), (5L, "Went to gym this morning")))

val myEdges = sc.makeRDD(Array(Edge(1L, 2L, "is-friends-with"),
Edge(2L, 3L, "is-friends-with"), Edge(3L, 4L, "is-friends-with"),
Edge(4L, 5L, "Likes-status"), Edge(3L, 5L, "Wrote-status"),
Edge(5L, 1L, "Read-status"), Edge(4L, 1L, "is-friends-with")
))

val myGraph = Graph(myVertices, myEdges)
myGraph.pageRank(0.001).vertices.foreach(println)
/*
(1,1.181229367561623)
(3,1.1320252563538404)
(5,0.8995285059796241)
(4,0.6321413208139575)
(2,1.1550755492909548)
*/
```
