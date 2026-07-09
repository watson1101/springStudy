set MAIN_CLASS=msdemo.hong.com.flinkdemo1.FlinkDemoRunner
set EXEC_ARGS=batch

mvn exec:java -pl flink-demo1 "-Dexec.mainClass=%MAIN_CLASS%" "-Dexec.args=%EXEC_ARGS%"
