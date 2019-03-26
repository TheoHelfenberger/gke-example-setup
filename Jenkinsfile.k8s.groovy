
def label = "maven-${UUID.randomUUID().toString()}"
def service = "microservice"
def dockerRegistry = "eu.gcr.io"
def gkeProject = "dd-experiment-registry"


podTemplate(label: label,
  containers: [
	containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true, command: 'cat')
  ],
  volumes: [
	hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
	configMapVolume(mountPath: '/gcloud', configMapName: 'sagcloudjson')
  ]) {

  node(label) {
	container('maven') {
		stage('Get sources') {
			checkout scm
		}
	
		stage('Build a Maven project') {
			def mvnProps = " -Dfabric8.resourceDir=src/main/fabric8-gke -Dfabric8.generator.name=$gkeProject/microservice"
			mvn "clean install fabric8:resource fabric8:build -DskipTests=true -Pk8s $mvnProps"
			sh "cat target/classes/META-INF/fabric8/kubernetes.yml"
		}
	}

	container('docker') {
	  def tag = "${dockerRegistry}/${gkeProject}/it/${service}:latest"
	  sh "cat /gcloud/sa_cluster_kubeplayground.json | docker login  -u _json_key --password-stdin https://${dockerRegistry}"
	  sh "docker tag ${gkeProject}/it/${service} ${tag}"
	  sh "docker images"
	  sh "docker push $tag"
	}

  }
}

def mvn(String command) {
   sh "mvn --batch-mode  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn ${command}"
}

