mvn  -Pk8s -DskipTests fabric8:resource fabric8:build \
-Dfabric8.resourceDir=src/main/fabric8-gke \
-Dfabric8.generator.name=dd-experiment-registry/microservice:4712
 
 alias k=kubectl  
 k create cm database-env-cm --from-env-file=k8s/database-env-cm.properties
 replace config map
 k create cm database-env-cm --from-env-file=k8s/database-env-cm.properties 