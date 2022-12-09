pipeline{
    agent any
    stages{
        stage("docker build"){
            steps{
                echo "========Building image ========"
                sh "pwd"
                sh "ls -l"
                sh "docker build -t sm:latest ."
                sh "docker image ls"
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