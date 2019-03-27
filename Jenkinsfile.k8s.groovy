def label = "maven-${UUID.randomUUID().toString()}"
def service = "microservice"
def dockerRegistry = "eu.gcr.io"
def gkeProject = "dd-experiment-registry"
def imageTag = ""

podTemplate(label: label, imagePullSecrets: ['registry-pull-push'], serviceAccount: 'jenkins',
containers: [
	containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'docker', image: 'docker', ttyEnabled: true, command: 'cat'),
	containerTemplate(name: 'helm-kubectl', image: 'dtzar/helm-kubectl', ttyEnabled: true, command: 'cat')
],
volumes: [
	hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
	configMapVolume(mountPath: '/gcloud', configMapName: 'registry-pull-push')
]) {

	node(label) {

		container('maven') {

			//			dir('RepoOne') {
			//				checkout([
			//					$class: 'GitSCM',
			//					branches: [[name: 'refs/heads/master']],
			//					doGenerateSubmoduleConfigurations: false,
			//					extensions: [
			//						[$class: 'RelativeTargetDirectory', relativeTargetDir: 'openshift-engineering']
			//					],
			//					submoduleCfg: [],
			//					userRemoteConfigs: [
			//						[ credentialsId: 'GitHub_login_token2', url: 'https://github.com/e-medicus/openshift-engineering.git']]
			//				])
			//			}

			//			dir('RepoOne2') {
			stage('Get sources') {
				def scmResult = checkout scm
				println(scmResult)
				imageTag = "${scmResult.GIT_COMMIT}".substring(0,6)
			}


			stage('Build a Maven project') {
				def mvnProps = " -Dfabric8.resourceDir=src/main/fabric8-gke -Dfabric8.generator.name=$gkeProject/it/${service}:${imageTag}"
				mvn "clean install fabric8:resource fabric8:build -DskipTests=true -Pk8s $mvnProps"
			}
		}
		//		}

		container('docker') {
			def tag = "${dockerRegistry}/${gkeProject}/it/${service}:${imageTag}"
			sh "cat /gcloud/registry-pull-push.json | docker login  -u _json_key --password-stdin https://${dockerRegistry}"
			sh "docker tag ${gkeProject}/it/${service} ${tag}"
			sh "docker push $tag"
		}

		container('helm-kubectl') {
			def tag = "${dockerRegistry}/${gkeProject}/it/${service}:${imageTag}"
			dir('k8s/helm/microservice') {
				sh """
					helm upgrade -i --name microservice --values=values.yaml \
					--namespace it \
					--set image.repository=${dockerRegistry}/${gkeProject}/it/${service} \
					--set iamge.tag=${imageTag} \
					.
				"""
			}
		}
	}
}

def mvn(String command) {
	sh "mvn --batch-mode  -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn ${command}"
}

