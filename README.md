# job-hunter
Job Hunter Application

Run the play app:

```
sbt -jvm-debug 5005 -Dconfig.file=./modules/job-hunter-app/conf/application.conf -Dhttp.port=9000 clean "jobHunterApp/run"
```

When that starts in the logs, direct your browser to `https://localhost:9000/` to see the application home.