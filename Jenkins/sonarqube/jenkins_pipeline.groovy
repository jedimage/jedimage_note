pipeline{
    agent{
        label "master"
    }

    environment {
        sonarqube_server_url = "http://localhost:9000"
        sonarqube_token = "your-sonarq-token"

        MSBUILD_PATH = "C:/Program Files (x86)/Microsoft Visual Studio/2019/Community/MSBuild/Current/Bin"
        csproj_file = "E:/foo/bar/project1/project1.csproj"
    }

    stages{
        stage("Git Checkout"){
            steps{
                //checkout your git here
            }
        }
        stage("Install APP Package"){
            steps{
                bat """
                cd\\
                E:
                cd $NUGET_PATH
                nuget.exe restore "E:/foo/bar/project1/package.sln"
                """
            }
        }
        stage("MSBuild"){
            steps{
                bat """
                set PATH=%PATH%;E:\\Tools\\sonar-scanner-msbuild-5.1.0.28487-net46

                C:
                cd $MSBUILD_PATH
                SonarScanner.MSBuild.exe begin /k:"your_project_key" /n:"your_project_name" /d:sonar.host.url="$sonarqube_server_url" /d:sonar.login="$sonarqube_token"
                MSBuild.exe $csproj_file /p:VisualStudioVersion=15.0 /p:Configuration=DEBUG /t:Rebuild
                SonarScanner.MSBuild.exe end /d:sonar.login="$sonarqube_token"
                """
            }
        }
    }
}