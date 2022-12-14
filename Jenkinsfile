pipeline{
    agent any
    tools{
        maven "Maven"
    }
    stages{
        stage("build app"){
            steps{
                sh "mvn clean install"
            }
        }
        stage("docker build"){
            steps{
                echo "========Building image ========"
                sh "pwd"
                sh "ls -l deploy/S-Monitor"
                sh "docker build -t localhost:5000/sm:latest ./deploy/S-Monitor/"
                sh "docker image ls"
                sh "docker push localhost:5000/sm:latest"
                sh "docker rmi localhost:5000/sm:latest"
                sh "docker image ls"
            }
        }
        stage("docker run"){
            steps{
                sh "docker rm -f sm_latets"
                sh "docker run -d -p 8088:8080 --name sm_latets localhost:5000/sm:latest"
                sh "docker ps"
            }
        }

        stage("kubernates deploy"){
            steps{
                sh "kubectl get nodes"
            }
        }
            // post{
            //     always{
            //         echo "========always========"
            //     }
            //     success{
            //         echo "========A executed successfully========"
            //     }
            //     failure{
            //         echo "========A execution failed========"
            //     }
            // }
        
    }
    // post{
    //     always{
    //         echo "========always========"
    //     }
    //     success{
    //         echo "========pipeline executed successfully ========"
    //     }
    //     failure{
    //         echo "========pipeline execution failed========"
    //     }
    // }
}