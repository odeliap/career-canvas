# career-canvas
Career Canvas Application

## Running the app

Create the docker image with:

```
sbt docker:publishLocal
```

Set up postgres DB:

```
docker-compose -f docker/host.yml up
```

Run the play app from the root of this project with:

```
sbt -jvm-debug 5005 -Dconfig.file=./modules/career-canvas-app/conf/application.conf -Dhttp.port=9000 clean "careerCanvasApp/run"
```

When that starts in the logs, direct your browser to `https://localhost:9000/` to see the application home.

## Run Unit Tests

Run unit tests from the root of this project with:

```
sbt test
```