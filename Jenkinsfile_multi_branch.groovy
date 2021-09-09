@Library('shared-library') _
import com.davidleonm.WeatherStationDashboardVariables
import com.davidleonm.GlobalVariables

pipeline {
    agent { label 'net-core-slave' }

    environment {
        SONAR_CREDENTIALS = credentials('sonarqube-token')
    }

    stages {
    /*stage('Prepare Python ENV') {
      steps {
        script {
          setBuildStatus('pending', "${WeatherStationDashboardVariables.RepositoryName}")

          // Clean & Prepare new python environment
          sh '''
             rm -rf ENV
             python3 -m venv ENV

             ENV/bin/pip install --no-cache-dir --upgrade pip
             ENV/bin/pip install --no-cache-dir --upgrade wheel
             ENV/bin/pip install --no-cache-dir --upgrade setuptools

             ENV/bin/pip install --no-cache-dir psycopg2 gpiozero coverage
             '''
        }
      }
    }*/

    /*
    stage('Execute unit tests and code coverage') {
      steps {
        script {
          sh """
             ENV/bin/python -m unittest discover -s ${WORKSPACE}/WeatherStationSensorsReader
             ENV/bin/coverage run -m unittest discover -s ${WORKSPACE}/WeatherStationSensorsReader
             """

          sh "dotnet test ${WORKSPACE}/Code"
        }
      }
    }
    */

        stage('SonarQube analysis') {
            environment {
                def scannerHome = tool 'Sonarqube'
            }

            steps {
                script {
                    withSonarQubeEnv('Sonarqube') {
                        sh """
                           dotnet ${scannerHome}/SonarScanner.MSBuild.dll begin /k:Dashboard /d:sonar.login=${SONAR_CREDENTIALS}
                           dotnet build ${WORKSPACE}/Code/WeatherStationProjectDashboard.sln
                           dotnet ${scannerHome}/SonarScanner.MSBuild.dll end /d:sonar.login=${SONAR_CREDENTIALS}
                           """
                    }

                    timeout(time: 10, unit: 'MINUTES') {
                        sleep(10)

                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            input "Quality gate failed with status: ${qg.status}. Continue?"
                        }
                    }
                }
            }
        }

        stage('Deploy on staging') {
            stages {
                stage('Deploy Client App') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.DashboardDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.App/WeatherStationProject.Dashboard.App.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=true --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.App')
                        }
                    }
                }

                stage('Deploy AirParametersService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.AirParametersServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.AirParametersService/WeatherStationProject.Dashboard.AirParametersService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.AirParametersService')
                        }
                    }
                }

                stage('Deploy AmbientTemperatureService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.AmbientTemperatureServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.AmbientTemperatureService/WeatherStationProject.Dashboard.AmbientTemperatureService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.AmbientTemperatureService')
                        }
                    }
                }

                stage('Deploy AuthenticationService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.AuthenticationServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.AuthenticationService/WeatherStationProject.Dashboard.AuthenticationService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.AuthenticationService')
                        }
                    }
                }

                stage('GatewayService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.GatewayServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.GatewayService/WeatherStationProject.Dashboard.GatewayService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.GatewayService')
                        }
                    }
                }

                stage('Deploy GroundTemperatureService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.GroundTemperatureServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.GroundTemperatureService/WeatherStationProject.Dashboard.GroundTemperatureService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.GroundTemperatureService')
                        }
                    }
                }

                stage('Deploy RainfallService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.RainfallServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.RainfallService/WeatherStationProject.Dashboard.RainfallService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.RainfallService')
                        }
                    }
                }

                stage('Deploy WindMeasurementsService') {
                    steps {
                        script {
                            deployImageOnDockerRegistry("${GlobalVariables.StagingDockerRegistry}",
                                                        "${WeatherStationDashboardVariables.WindMeasurementsServiceDockerRegistryName}",
                                                        "${GlobalVariables.StagingCredentialsDockerRegistryKey}",
                                                        "${getVersionFromCsproj('./Code/src/WeatherStationProject.Dashboard.WindMeasurementsService/WeatherStationProject.Dashboard.WindMeasurementsService.csproj')}",
                                                        './Dockerfile',
                                                        '--build-arg INCLUDE_NPM=false --build-arg PROJECT_NAME=WeatherStationProject.Dashboard.WindMeasurementsService')
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                cleanImages(false, true)
            }
        }

        success {
            script {
                setBuildStatus('success', "${WeatherStationDashboardVariables.RepositoryName}")
            }
        }

        failure {
            script {
                setBuildStatus('failure', "${WeatherStationDashboardVariables.RepositoryName}")
            }
        }
    }
}
