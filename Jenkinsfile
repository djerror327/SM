pipeline{
    agent any
    tools{
        maven "Maven"
    }
    stages{
        stage("build app"){
            steps{
                sh "mvn build"
            }
        }
        stage("docker build"){
            steps{
                echo "========Building image ========"
                sh "pwd"
                sh "ls -l"
                sh "docker build -t localhost:5000/sm:latest ."
                sh "docker image ls"
                sh "docker push localhost:5000/sm:latest"
                sh "docker rmi localhost:5000/sm:latest"
                sh "docker image ls"
            }
        }
        stage("docker run"){
            steps{
                sh "docker run -d -p 8088:8080 --name sm_latets localhost:5000/sm:latest"
                sh "docker ps"
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