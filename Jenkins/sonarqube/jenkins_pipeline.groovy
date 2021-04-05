pipeline{
    agent{
        label "master"
    }

    environment {
        sonarqube_server_url = "http://localhost:9000"
        sonarqube_token = "your-sonarq-token"
        sonar_scanner_path = "E:/your/sonar_scanner_path/"

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
                cd ${NUGET_PATH}
                nuget.exe restore "E:/foo/bar/project1/package.sln"
                """
            }
        }
        stage("MSBuild"){
            steps{
                bat """
                set PATH=%PATH%;C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\Community\\MSBuild\\Current\\Bin

                C:
                cd ${sonar_scanner_path}
                SonarScanner.MSBuild.exe begin /k:"your_project_key" /n:"your_project_name" /d:sonar.host.url="${sonarqube_server_url}" /d:sonar.login="${sonarqube_token}"
                MSBuild.exe ${csproj_file} /p:VisualStudioVersion=15.0 /p:Configuration=DEBUG /t:Rebuild
                SonarScanner.MSBuild.exe end /d:sonar.login="${sonarqube_token}"
                """
            }
        }
    }
}