mvn  -Pk8s -DskipTests fabric8:resource fabric8:build \
-Dfabric8.resourceDir=src/main/fabric8-gke \
-Dfabric8.generator.name=dd-experiment-registry/microservice
   