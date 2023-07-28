# job-hunter
Job Hunter Application

## Running the app

Run the play app from the root of this project with:

```
sbt -jvm-debug 5005 -Dconfig.file=./modules/job-hunter-app/conf/application.conf -Dhttp.port=9000 clean "jobHunterApp/run"
```

When that starts in the logs, direct your browser to `https://localhost:9000/` to see the application home.

## Run Unit Tests

Run unit tests from the root of this project with:

```
sbt test
```